package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.render.TESR.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class MechanismAxle implements IMechanism<GearFactory.GearMaterial>{

	protected static final VoxelShape[] SHAPES = new VoxelShape[3];
	static{
		SHAPES[0] = Block.box(0, 7, 7, 16, 9, 9);//X
		SHAPES[1] = Block.box(7, 0, 7, 9, 16, 9);//Y
		SHAPES[2] = Block.box(7, 7, 0, 9, 9, 16);//Z
	}

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(mat instanceof GearFactory.GearMaterial){
			return MiscUtil.preciseRound(((GearFactory.GearMaterial) mat).getDensity() / 32_000D, 3);
		}else{
			return 0;
		}
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == Capabilities.AXLE_CAPABILITY && side == null && capSide.getAxis() == axis;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should always be in the axle slot
		if(side != null){
			return;
		}

		if(rotRatioIn == 0){
			rotRatioIn = 1;
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

		
		
		for(Direction.AxisDirection direct : Direction.AxisDirection.values()){
			Direction endDir = Direction.get(direct, axis);
			
			if(te.members[endDir.get3DDataValue()] != null){
				//Do internal connection
				if(te.members[endDir.get3DDataValue()].hasCap(Capabilities.AXLE_CAPABILITY, endDir, te.mats[endDir.get3DDataValue()], endDir, axis, te)){
					te.axleHandlers[endDir.get3DDataValue()].propagate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
				}
			}else{
				//Connect externally
				Direction oEndDir = endDir.getOpposite();
				RotaryUtil.propagateAxially(te.getLevel().getBlockEntity(te.getBlockPos().relative(endDir)), oEndDir, handler, masterIn, key, handler.renderOffset);
//				TileEntity endTE = te.getWorld().getTileEntity(te.getPos().offset(endDir));
//				if(endTE != null){
//					LazyOptional<IAxisHandler> axisOpt = endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir);
//					if(axisOpt.isPresent()){
//						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
//					}
//
//					LazyOptional<IAxleHandler> axleOpt = endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir);
//					if(axleOpt.isPresent()){
//						axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
//					}
//				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof GearFactory.GearMaterial){
			return CRItems.axle.withMaterial((OreSetup.OreProfile) mat, 1);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side != null || axis == null ? Shapes.empty() : SHAPES[axis.ordinal()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		if(axis != Direction.Axis.Y){
			Quaternion rotation = (axis == Direction.Axis.X ? Vector3f.ZN : Vector3f.XP).rotationDegrees(90);
			matrix.mulPose(rotation);
		}

		float angle = handler.getAngle(partialTicks);
		matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
		CRModels.drawAxle(matrix, buffer, combinedLight, mat instanceof GearFactory.GearMaterial ? ((GearFactory.GearMaterial) mat).getColor() : Color.WHITE);
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
