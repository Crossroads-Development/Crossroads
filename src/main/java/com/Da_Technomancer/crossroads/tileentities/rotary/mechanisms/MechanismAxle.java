package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismAxle implements IMechanism{

	private static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[3];
	static{
		BOUNDING_BOXES[0] = new AxisAlignedBB(0, .4375D, .4375D, 1, .5625D, .5625D);//X
		BOUNDING_BOXES[1] = new AxisAlignedBB(.4375D, 0, .4375D, .5625D, 1, .5625D);//Y
		BOUNDING_BOXES[2] = new AxisAlignedBB(.4375D, .4375D, 0, .5625D, .5625D, 1);//Z
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
					if(endTE.hasCapability(Capabilities.AXIS_CAPABILITY, oEndDir)){
						endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir).trigger(masterIn, key);
					}

					if(endTE.hasCapability(Capabilities.SLAVE_AXIS_CAPABILITY, oEndDir)){
						masterIn.addAxisToList(endTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, oEndDir), oEndDir);
					}

					if(endTE.hasCapability(Capabilities.AXLE_CAPABILITY, oEndDir)){
						endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
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
	public AxisAlignedBB getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side != null || axis == null ? Block.NULL_AABB : BOUNDING_BOXES[axis.ordinal()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		GlStateManager.pushMatrix();
		GlStateManager.rotate(axis == Direction.Axis.Y ? 0 : 90F, axis == Direction.Axis.Z ? 1 : 0, 0, axis == Direction.Axis.X ? -1 : 0);
		float angle = handler.getAngle(partialTicks);
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		ModelAxle.render(mat.getColor());
		GlStateManager.popMatrix();
	}
}
