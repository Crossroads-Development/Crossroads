package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.Properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

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
	
	private final IAdvancedRedstoneHandler redstoneHandler = new RedstoneHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && side == world.getBlockState(pos).getValue(Properties.FACING)){
			return true;
		}
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && side == world.getBlockState(pos).getValue(Properties.FACING)){
			return (T) redstoneHandler;
		}
		return super.getCapability(cap, side);
	}
	
	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			return output;
		}
	}
}
