package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismAxle implements IMechanism{

	protected static final VoxelShape[] SHAPES = new VoxelShape[3];
	static{
		SHAPES[0] = Block.makeCuboidShape(0, 7, 7, 16, 9, 9);//X
		SHAPES[1] = Block.makeCuboidShape(7, 0, 7, 9, 16, 9);//Y
		SHAPES[2] = Block.makeCuboidShape(7, 7, 0, 9, 9, 16);//Z
	}

	@Override
	public double getInertia(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		return mat.getDensity() / 32_000D;
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == Capabilities.AXLE_CAPABILITY && side == null && capSide.getAxis() == axis;
	}

	@Override
	public void propogate(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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
			Direction endDir = Direction.getFacingFromAxis(direct, axis);
			
			if(te.members[endDir.getIndex()] != null){
				//Do internal connection
				if(te.members[endDir.getIndex()].hasCap(Capabilities.AXLE_CAPABILITY, endDir, te.mats[endDir.getIndex()], endDir, axis, te)){
					te.axleHandlers[endDir.getIndex()].propogate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
				}
			}else{
				//Connect externally
				TileEntity endTE = te.getWorld().getTileEntity(te.getPos().offset(endDir));
				Direction oEndDir = endDir.getOpposite();
				if(endTE != null){
					LazyOptional<IAxisHandler> axisOpt = endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir);
					if(axisOpt.isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}

					LazyOptional<ISlaveAxisHandler> saxisOpt = endTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, oEndDir);
					if(saxisOpt.isPresent()){
						masterIn.addAxisToList(saxisOpt.orElseThrow(NullPointerException::new), oEndDir);
					}
					LazyOptional<IAxleHandler> axleOpt = endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir);
					if(axleOpt.isPresent()){
						axleOpt.orElseThrow(NullPointerException::new).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(GearFactory.GearMaterial mat){
		return new ItemStack(GearFactory.gearTypes.get(mat).getAxle(), 1);
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side != null || axis == null ? VoxelShapes.empty() : SHAPES[axis.ordinal()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		GlStateManager.pushMatrix();
		GlStateManager.rotatef(axis == Direction.Axis.Y ? 0 : 90F, axis == Direction.Axis.Z ? 1 : 0, 0, axis == Direction.Axis.X ? -1 : 0);
		float angle = handler.getAngle(partialTicks);
		GlStateManager.rotatef(angle, 0F, 1F, 0F);
		ModelAxle.render(mat.getColor());
		GlStateManager.popMatrix();
	}
}
