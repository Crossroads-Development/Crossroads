package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.essentials.api.MathUtil;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.render.tesr.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class MechanismSmallGear implements IMechanism<CRMaterialLibrary.GearMaterial>{

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
		if(mat instanceof CRMaterialLibrary.GearMaterial){
			return MathUtil.preciseRound(0.125D * ((CRMaterialLibrary.GearMaterial) mat).getDensity() / 8, 3);// .125 because r*r/2 so .5*.5/2
		}else{
			return 0;
		}
	}

	@Override
	public boolean hasCap(BlockCapability<?, ?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return (cap == CRCapabilities.COG_CAPABILITY || cap == CRCapabilities.AXLE_CAPABILITY) && side == capSide;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should never be in the axle slot
		if(side == null){
			return;
		}

		if(lastRadius != 0){
			rotRatioIn *= lastRadius * 2D;
		}

		//If true, this has already been checked.
		if(key == handler.getUpdateKey()){
			//If true, there is rotation conflict.
			if(handler.getRotationRatio() != rotRatioIn){
				masterIn.lock();
			}
			return;
		}

		if(masterIn.addToList(handler)){
			return;
		}

		handler.setRotRatio(rotRatioIn);
		handler.setUpdateKey(key);

		//Other internal gears
		for(int i = 0; i < 6; i++){
			if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue() && te.members[i] != null && te.members[i].hasCap(CRCapabilities.COG_CAPABILITY, Direction.from3DDataValue(i), te.mats[i], Direction.from3DDataValue(i), te.getAxleAxis(), te)){
				te.axleHandlers[i].propagate(masterIn, key, RotaryUtil.getDirSign(side, Direction.from3DDataValue(i)) * handler.getRotationRatio(), .5D, !handler.renderOffset());
			}
		}

		Level world = te.getLevel();
		BlockPos sidePos = te.getBlockPos().relative(side);
//		BlockState sideState = te.getLevel().getBlockState(sidePos);
//		BlockEntity sideTE = te.getLevel().getBlockEntity(sidePos);
		for(int i = 0; i < 6; i++){
			if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue()){
				Direction facing = Direction.from3DDataValue(i);
				// Adjacent gears
				BlockPos adjPos = te.getBlockPos().relative(facing);
				BlockEntity adjTE = world.getBlockEntity(adjPos);
				BlockState adjState = world.getBlockState(adjPos);
				ICogHandler cogHandler = world.getCapability(CRCapabilities.COG_CAPABILITY, adjPos, adjState, adjTE, side);
				if(cogHandler != null){
					cogHandler.connect(masterIn, key, -handler.getRotationRatio(), .5D, facing.getOpposite(), handler.renderOffset());
				}else if((cogHandler = world.getCapability(CRCapabilities.COG_CAPABILITY, adjPos, adjState, adjTE, facing.getOpposite())) != null){
					//Check for large gears
					cogHandler.connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * handler.getRotationRatio(), .5D, side, handler.renderOffset());
				}

				// Diagonal gears
				BlockPos diagPos = te.getBlockPos().relative(facing).relative(side);
				BlockEntity diagTE = world.getBlockEntity(diagPos);
				BlockState diagState = world.getBlockState(diagPos);
				if((cogHandler = world.getCapability(CRCapabilities.COG_CAPABILITY, diagPos, diagState, diagTE, facing.getOpposite())) != null && RotaryUtil.canConnectThrough(world, te.getBlockPos().relative(facing), facing.getOpposite(), side)){
					cogHandler.connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * handler.getRotationRatio(), .5D, side.getOpposite(), handler.renderOffset());
				}

				if((cogHandler = world.getCapability(CRCapabilities.COG_CAPABILITY, diagPos, diagState, diagTE, facing)) != null){
					cogHandler.connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, .5D, side.getOpposite(), handler.renderOffset());
				}
			}
		}

		//Connected block
		RotaryUtil.propagateAxially(te.getLevel(), sidePos, side.getOpposite(), handler, masterIn, key, handler.renderOffset());
//		if(sideTE != null){
//			IAxisHandler axisOpt = sideTE.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
//			if(axisOpt.isPresent()){
//				axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
//			}
//			IAxleHandler axleOpt = sideTE.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
//			if(axleOpt.isPresent()){
//				axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
//			}
//		}

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(CRCapabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
			te.axleHandlers[6].propagate(masterIn, key, handler.getRotationRatio(), 0, handler.renderOffset());
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof CRMaterialLibrary.GearMaterial){
			return CRItems.smallGear.withMaterial((CRMaterialLibrary.OreProfile) mat, 1);
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

		IAxleHandler handler = te.axleHandlers[side.get3DDataValue()];

		matrix.mulPose(side.getOpposite().getRotation());//Apply orientation
		float angle = handler.getAngle(partialTicks);
		matrix.translate(0, -0.4375D, 0);
		matrix.mulPose(Axis.YP.rotationDegrees(-(float) RotaryUtil.getCCWSign(side) * angle));

		CRModels.draw8GearMilled(matrix, buffer.getBuffer(RenderType.solid()), CRRenderUtil.convertColor(mat instanceof CRMaterialLibrary.GearMaterial ? ((CRMaterialLibrary.GearMaterial) mat).getColor() : Color.WHITE), combinedLight, CRModels.generateZFightFactor(te.getBlockPos(), side == null ? 0 : side.ordinal()));
	}

	@Override
	public CRMaterialLibrary.GearMaterial readProperty(CompoundTag nbt){
		return CRMaterialLibrary.GearMaterial.read(nbt);
	}
}
