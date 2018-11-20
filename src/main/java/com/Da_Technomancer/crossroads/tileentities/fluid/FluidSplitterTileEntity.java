package com.Da_Technomancer.crossroads.tileentities.fluid;

import net.minecraft.nbt.NBTTagCompound;

public class FluidSplitterTileEntity extends BasicFluidSplitterTileEntity{

	public FluidSplitterTileEntity(){
		super();
	}

	public int redstone;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("reds", redstone);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		redstone = nbt.getInteger("reds");
	}

	@Override
	protected int getPortion(){
		return redstone;
	}

	@Override
	protected int getBase(){
		return 15;
	}
} 
