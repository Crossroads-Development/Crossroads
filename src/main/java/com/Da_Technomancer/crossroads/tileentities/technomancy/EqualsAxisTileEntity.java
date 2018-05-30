package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class EqualsAxisTileEntity extends AbstractMathAxisTE{

	private EnumFacing facing;

	private static final double MARGIN = 0.005D;

	@Override
	protected double getOutSpeed(double speed1, double speed2){
		return Math.abs(speed1 - speed2) <= MARGIN ? speed1 : 0;
	}

	@Override
	protected EnumFacing getInOne(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(Properties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(Properties.FACING);
		}
		return facing.rotateY();
	}

	@Nullable
	@Override
	protected EnumFacing getInTwo(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(Properties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(Properties.FACING);
		}
		return facing.rotateYCCW();
	}

	@Override
	protected EnumFacing getOut(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(Properties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(Properties.FACING);
		}
		return facing;
	}

	@Override
	protected EnumFacing getBattery(){
		return EnumFacing.DOWN;
	}

	@Override
	protected void cleanDirCache(){
		facing = null;
	}
}
