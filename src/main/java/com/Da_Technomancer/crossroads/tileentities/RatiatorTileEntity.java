package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class RatiatorTileEntity extends TileEntity implements ITickable{
	
	private double output;
	private double inputSide;
	private double inputBack;
	
	public double getOutput(){
		return output;
	}
	
	public void setOutput(double outputIn){
		output = outputIn;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		output = nbt.getDouble("out");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("out", output);
		return nbt;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		EnumFacing side = worldObj.getBlockState(pos).getValue(Properties.FACING);
		double sidePower = Math.max(ModBlocks.ratiator.getPowerOnSide(worldObj, pos, side.rotateY(), false), ModBlocks.ratiator.getPowerOnSide(worldObj, pos, side.getOpposite().rotateY(), false));
		double backPower = ModBlocks.ratiator.getPowerOnSide(worldObj, pos, side.getOpposite(), true);
		if(inputSide != sidePower || inputBack != backPower){
			inputSide = sidePower;
			inputBack = backPower;
			ModBlocks.ratiator.neighborChanged(worldObj.getBlockState(pos), worldObj, pos, null);
		}
	}
}
