package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearOctagon;
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

public class MechanismSmallGear implements IMechanism{

	protected static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);//DOWN
		SHAPES[1] = Block.makeCuboidShape(0, 14, 0, 16, 16, 16);//UP
		SHAPES[2] = Block.makeCuboidShape(0, 0, 0, 16, 16, 2);//NORTH
		SHAPES[3] = Block.makeCuboidShape(0, 0, 14, 16, 16, 16);//SOUTH
		SHAPES[4] = Block.makeCuboidShape(0, 0, 0, 2, 16, 16);//WEST
		SHAPES[5] = Block.makeCuboidShape(14, 0, 0, 16, 16, 16);//EAST
	}

	@Override
	public double getInertia(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		// assume each gear is 1/8 of a cubic meter and has a radius of 1/2 meter.
		// mass is rounded to make things nicer for everyone
		return 0.125D * MiscUtil.betterRound(mat.getDensity() / 8, 1);// .125 because r*r/2 so .5*.5/2
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return (cap == Capabilities.COG_CAPABILITY || cap == Capabilities.AXLE_CAPABILITY) && side == capSide;
	}

	@Override
	public void propogate(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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
			if(i != side.getIndex() && i != side.getOpposite().getIndex() && te.members[i] != null && te.members[i].hasCap(Capabilities.COG_CAPABILITY, Direction.byIndex(i), te.mats[i], Direction.byIndex(i), te.axleAxis, te)){
				te.axleHandlers[i].propogate(masterIn, key, RotaryUtil.getDirSign(side, Direction.byIndex(i)) * handler.rotRatio, .5D, !handler.renderOffset);
			}
		}

		TileEntity sideTE = te.getWorld().getTileEntity(te.getPos().offset(side));
		for(int i = 0; i < 6; i++){
			if(i != side.getIndex() && i != side.getOpposite().getIndex()){
				Direction facing = Direction.byIndex(i);
				// Adjacent gears
				TileEntity adjTE = te.getWorld().getTileEntity(te.getPos().offset(facing));
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
				TileEntity diagTE = te.getWorld().getTileEntity(te.getPos().offset(facing).offset(side));
				LazyOptional<ICogHandler> cogOpt;
				if(diagTE != null && (cogOpt = diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent() && RotaryUtil.canConnectThrough(te.getWorld(), te.getPos().offset(facing), facing.getOpposite(), side)){
					cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * handler.rotRatio, .5D, side.getOpposite(), handler.renderOffset);
				}

				if(sideTE != null && (cogOpt = sideTE.getCapability(Capabilities.COG_CAPABILITY, facing)).isPresent()){
					cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, .5D, side.getOpposite(), handler.renderOffset);
				}
			}
		}

		//Connected block
		if(sideTE != null){
			LazyOptional<IAxisHandler> axisOpt = sideTE.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
			if(axisOpt.isPresent()){
				axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
			}
			LazyOptional<ISlaveAxisHandler> slaveOpt = sideTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, side.getOpposite());
			if(slaveOpt.isPresent()){
				masterIn.addAxisToList(slaveOpt.orElseThrow(NullPointerException::new), side.getOpposite());
			}
			LazyOptional<IAxleHandler> axleOpt = sideTE.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
			if(axleOpt.isPresent()){
				axleOpt.orElseThrow(NullPointerException::new).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
			}
		}

		//Axle slot
		if(te.axleAxis == side.getAxis() && te.members[6] != null && te.members[6].hasCap(Capabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.axleAxis, te)){
			te.axleHandlers[6].propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(GearFactory.GearMaterial mat){
		return new ItemStack(GearFactory.gearTypes.get(mat).getSmallGear(), 1);
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? VoxelShapes.empty() : SHAPES[side.getIndex()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[side.getIndex()];

		GlStateManager.pushMatrix();
		GlStateManager.rotatef(side == Direction.DOWN ? 0 : side == Direction.UP ? 180F : side == Direction.NORTH || side == Direction.EAST ? 90F : -90F, side.getAxis() == Direction.Axis.Z ? 1 : 0, 0, side.getAxis() == Direction.Axis.Z ? 0 : 1);
		float angle = handler.getAngle(partialTicks);
		GlStateManager.translatef(0, -0.4375F, 0);
		GlStateManager.rotatef((float) -side.getAxisDirection().getOffset() * angle, 0F, 1F, 0F);
		ModelGearOctagon.render(mat.getColor());
		GlStateManager.popMatrix();
	}
}
