package com.Da_Technomancer.crossroads.tileentities.fluid;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;

public class FluidSplitterTileEntity extends BasicFluidSplitterTileEntity{

	public FluidSplitterTileEntity(){
		super();
	}

	public int redstone;

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("reds", redstone);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		redstone = nbt.getInt("reds");
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
