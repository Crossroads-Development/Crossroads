package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxle;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class HamsterWheelTileEntity extends TileEntity implements ITickable{

	public float angle = 0;
	public float nextAngle = 0;

	@Override
	public void update(){
		EnumFacing facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
			IAxleHandler axle = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite());
			if(world.isRemote){
				angle = (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && te instanceof IAxle ? -1F : 1F) * (float) axle.getAngle();
				nextAngle = ((float) axle.getNextAngle());
				return;
			}
			axle.addEnergy(facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && te instanceof IAxle ? -2 : 2, true, true);
		}else if(world.isRemote){
			nextAngle = angle;//
		}
	}
}
