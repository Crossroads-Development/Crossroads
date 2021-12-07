package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.API.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.TESR.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class MechanismSmallGear implements IMechanism<GearFactory.GearMaterial>{

	protected static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = Block.box(0, 0, 0, 16, 2, 16);//DOWN
		SHAPES[1] = Block.box(0, 14, 0, 16, 16, 16);//UP
		SHAPES[2] = Block.box(0, 0, 0, 16, 16, 2);//NORTH
		SHAPES[3] = Block.box(0, 0, 14, 16, 16, 16);//SOUTH
		SHAPES[4] = Block.box(0, 0, 0, 2, 16, 16);//WEST
		SHAPES[5] = Block.box(14, 0, 0, 16, 16, 16);//EAST
	}

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		// assume each gear is 1/8 of a cubic meter and has a radius of 1/2 meter.
		// mass is rounded to make things nicer for everyone
		if(mat instanceof GearFactory.GearMaterial){
			return MiscUtil.preciseRound(0.125D * ((GearFactory.GearMaterial) mat).getDensity() / 8, 3);// .125 because r*r/2 so .5*.5/2
		}else{
			return 0;
		}
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return (cap == Capabilities.COG_CAPABILITY || cap == Capabilities.AXLE_CAPABILITY) && side == capSide;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should never be in the axle slot
		if(side == null){
			return;
		}

		if(lastRadius != 0){
			rotRatioIn *= lastRadius * 2D;
		}

		//If true, this has already been checked.
		if(key == handler.updateKey){
			//If true, there is rotation conflict.
			if(handler.rotRatio != rotRatioIn){
				masterIn.lock();
			}
			return;
		}

		if(masterIn.addToList(handler)){
			return;
		}

		handler.rotRatio = rotRatioIn;
		handler.updateKey = key;

		//Other internal gears
		for(int i = 0; i < 6; i++){
			if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue() && te.members[i] != null && te.members[i].hasCap(Capabilities.COG_CAPABILITY, Direction.from3DDataValue(i), te.mats[i], Direction.from3DDataValue(i), te.getAxleAxis(), te)){
				te.axleHandlers[i].propagate(masterIn, key, RotaryUtil.getDirSign(side, Direction.from3DDataValue(i)) * handler.rotRatio, .5D, !handler.renderOffset);
			}
		}

		BlockEntity sideTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(side));
		for(int i = 0; i < 6; i++){
			if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue()){
				Direction facing = Direction.from3DDataValue(i);
				// Adjacent gears
				BlockEntity adjTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(facing));
				if(adjTE != null){
					LazyOptional<ICogHandler> cogOpt;
					if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, side)).isPresent()){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -handler.rotRatio, .5D, facing.getOpposite(), handler.renderOffset);
					}else if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent()){
						//Check for large gears
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * handler.rotRatio, .5D, side, handler.renderOffset);
					}
				}

				// Diagonal gears
				BlockEntity diagTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(facing).relative(side));
				LazyOptional<ICogHandler> cogOpt;
				if(diagTE != null && (cogOpt = diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent() && RotaryUtil.canConnectThrough(te.getLevel(), te.getBlockPos().relative(facing), facing.getOpposite(), side)){
					cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * handler.rotRatio, .5D, side.getOpposite(), handler.renderOffset);
				}

				if(sideTE != null && (cogOpt = sideTE.getCapability(Capabilities.COG_CAPABILITY, facing)).isPresent()){
					cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, .5D, side.getOpposite(), handler.renderOffset);
				}
			}
		}

		//Connected block
		RotaryUtil.propagateAxially(sideTE, side.getOpposite(), handler, masterIn, key, handler.renderOffset);
//		if(sideTE != null){
//			LazyOptional<IAxisHandler> axisOpt = sideTE.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
//			if(axisOpt.isPresent()){
//				axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
//			}
//			LazyOptional<IAxleHandler> axleOpt = sideTE.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
//			if(axleOpt.isPresent()){
//				axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
//			}
//		}

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(Capabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
			te.axleHandlers[6].propagate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof GearFactory.GearMaterial){
			return CRItems.smallGear.withMaterial((OreSetup.OreProfile) mat, 1);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? Shapes.empty() : SHAPES[side.get3DDataValue()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[side.get3DDataValue()];

		matrix.mulPose(side.getOpposite().getRotation());//Apply orientation
		float angle = handler.getAngle(partialTicks);
		matrix.translate(0, -0.4375D, 0);
		matrix.mulPose(Vector3f.YP.rotationDegrees(-(float) RotaryUtil.getCCWSign(side) * angle));
		CRModels.draw8Gear(matrix, buffer.getBuffer(RenderType.solid()), CRRenderUtil.convertColor(mat instanceof GearFactory.GearMaterial ? ((GearFactory.GearMaterial) mat).getColor() : Color.WHITE), combinedLight);
	}

	@Override
	public GearFactory.GearMaterial deserializeProperty(int serial){
		return GearFactory.GearMaterial.deserialize(serial);
	}

	@Override
	public GearFactory.GearMaterial loadProperty(String name){
		return GearFactory.findMaterial(name);
	}
}
