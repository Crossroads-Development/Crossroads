package com.Da_Technomancer.crossroads.API.magic;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	public void addMagic(MagicUnitStorage otherStorage){
		for(int i = 0; i < 4; i++){
			stored[i] += otherStorage.stored[i];
		}
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

	public void writeToNBT(@Nonnull String key, NBTTagCompound nbt){
		if(!isEmpty()){
			nbt.setIntArray(key, stored);
		}
	}

	public static void writeToNBT(@Nonnull String key, NBTTagCompound nbt, MagicUnit mag){
		if(mag != null){
			nbt.setIntArray(key, new int[] {mag.getEnergy(), mag.getPotential(), mag.getStability(), mag.getVoid()});
		}
	}

	public static MagicUnit readUnitFromNBT(@Nonnull String key, NBTTagCompound nbt){
		if(nbt.hasKey(key)){
			return new MagicUnit(nbt.getIntArray(key));
		}
		return null;
	}

	public static MagicUnitStorage readFromNBT(@Nonnull String key, NBTTagCompound nbt){
		MagicUnitStorage out = new MagicUnitStorage();
		if(nbt.hasKey(key)){
			out.stored = nbt.getIntArray(key);
		}
		return out;
	}
}
