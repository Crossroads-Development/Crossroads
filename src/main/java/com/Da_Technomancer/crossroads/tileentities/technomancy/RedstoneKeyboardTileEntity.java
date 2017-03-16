package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class RedstoneKeyboardTileEntity extends TileEntity implements IDoubleReceiver{

	public double output;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		output = nbt.getDouble("output");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("output", output);
		return nbt;
	}

	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("output") || context.equals("newOutput")){
			output = message;
		}
	}
}
