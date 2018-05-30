package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class LessThanAxisTileEntity extends AbstractMathAxisTE{

	private EnumFacing facing;

	@Override
	protected double getOutSpeed(double speed1, double speed2){
		return Math.min(speed1, speed2);
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
