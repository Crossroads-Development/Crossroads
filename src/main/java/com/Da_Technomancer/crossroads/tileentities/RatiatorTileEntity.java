package com.Da_Technomancer.crossroads.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class RatiatorTileEntity extends TileEntity{
	
	private double output;
	
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
}
