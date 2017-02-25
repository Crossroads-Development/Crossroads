package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class HamsterWheelTileEntity extends TileEntity implements ITickable{

	public float angle = 0;

	@Override
	public void update(){
		EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);
		if(MiscOp.safeHasCap(world, pos.offset(facing), Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
			if(world.isRemote){
				angle = (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && world.getBlockState(pos.offset(facing)).getBlock() == ModBlocks.axle ? -1F : 1F) * (float) world.getTileEntity(pos.offset(facing)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).getAngle();
				return;
			}
			world.getTileEntity(pos.offset(facing)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).addEnergy(facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && world.getBlockState(pos.offset(facing)).getBlock() == ModBlocks.axle ? -2 : 2, true, true);
		}

	}
}
