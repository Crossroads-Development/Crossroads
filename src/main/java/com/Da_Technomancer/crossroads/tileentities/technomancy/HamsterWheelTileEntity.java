package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;

public class HamsterWheelTileEntity extends TileEntity implements ITickableTileEntity{

	public float angle = 0;
	public float nextAngle = 0;

	@Override
	public void tick(){
		Direction facing = world.getBlockState(pos).get(CrossroadsProperties.HORIZ_FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, facing.getOpposite())){
			IAxleHandler axle = te.getCapability(Capabilities.AXLE_CAPABILITY, facing.getOpposite());
			if(world.isRemote){
				angle = axle.getAngle(0);
				nextAngle = axle.getAngle(1F);
				return;
			}
			axle.addEnergy(2 * facing.getAxisDirection().getOffset(), true, true);
		}else if(world.isRemote){
			nextAngle = angle;//
		}
	}
}
