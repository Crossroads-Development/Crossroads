package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class CosAxisTileEntity extends AbstractMathAxisTE{

	private EnumFacing facing;

	@Override
	protected double getOutSpeed(double speed1, double speed2){
		return Math.cos(speed1);
	}

	@Override
	protected EnumFacing getInOne(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(EssentialsProperties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		}
		return facing.getOpposite();
	}

	@Nullable
	@Override
	protected EnumFacing getInTwo(){
		return null;
	}

	@Override
	protected EnumFacing getOut(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(EssentialsProperties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
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
