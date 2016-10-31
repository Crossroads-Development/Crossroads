package com.Da_Technomancer.crossroads.API.magic;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public class MagicUnitStorage{
	
	private int[] stored = new int[4];
	
	public void addMagic(@Nullable MagicUnit mag){
		if(mag == null){
			return;
		}
		
		stored[0] += mag.getEnergy();
		stored[1] += mag.getPotential();
		stored[2] += mag.getStability();
		stored[3] += mag.getVoid();
	}
	
	public void subtractMagic(@Nullable MagicUnit mag){
		if(mag == null){
			return;
		}
		
		stored[0] -= mag.getEnergy();
		stored[1] -= mag.getPotential();
		stored[2] -= mag.getStability();
		stored[3] -= mag.getVoid();
		
		if(stored[0] < 0){
			stored[0] = 0;
		}
		if(stored[1] < 0){
			stored[1] = 0;
		}
		if(stored[2] < 0){
			stored[2] = 0;
		}
		if(stored[3] < 0){
			stored[3] = 0;
		}
	}
	
	public boolean isEmpty(){
		return stored[0] == 0 && stored[1] == 0 && stored[2] == 0 && stored[3] == 0;
	}
	
	public void clear(){
		stored[0] = 0;
		stored[1] = 0;
		stored[2] = 0;
		stored[3] = 0;
	}
	
	@Nullable
	public MagicUnit getOutput(){
		return isEmpty() ? null : new MagicUnit(stored[0], stored[1], stored[2], stored[3]);
	}
	
	public void writeToNBT(@Nullable String key, NBTTagCompound nbt){
		if(!isEmpty()){
			new MagicUnit(stored[0], stored[1], stored[2], stored[3]).setNBT(nbt, key);
		}
	}
	
	public static MagicUnitStorage readFromNBT(@Nullable String key, NBTTagCompound nbt){
		MagicUnitStorage out = new MagicUnitStorage();
		out.addMagic(MagicUnit.loadNBT(nbt, key));
		return out;
	}
}
