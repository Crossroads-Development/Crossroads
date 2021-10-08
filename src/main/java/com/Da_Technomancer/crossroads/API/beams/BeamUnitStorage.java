package com.Da_Technomancer.crossroads.API.beams;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class BeamUnitStorage{

	private int[] stored = new int[4];

	public void addBeam(@Nonnull BeamUnit mag){
		stored[0] += mag.getEnergy();
		stored[1] += mag.getPotential();
		stored[2] += mag.getStability();
		stored[3] += mag.getVoid();
	}

	public void addBeam(BeamUnitStorage otherStorage){
		for(int i = 0; i < 4; i++){
			stored[i] += otherStorage.stored[i];
		}
	}

	public void subtractBeam(@Nonnull BeamUnit mag){
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
		return getPower() == 0;
	}

	public int getPower(){
		return stored[0] + stored[1] + stored[2] + stored[3];
	}

	public void clear(){
		stored[0] = 0;
		stored[1] = 0;
		stored[2] = 0;
		stored[3] = 0;
	}

	@Nonnull
	public BeamUnit getOutput(){
		return isEmpty() ? BeamUnit.EMPTY : new BeamUnit(stored[0], stored[1], stored[2], stored[3]);
	}

	public void writeToNBT(@Nonnull String key, CompoundTag nbt){
		if(!isEmpty()){
			nbt.putIntArray(key, stored);
		}
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		BeamUnitStorage that = (BeamUnitStorage) o;
		return Arrays.equals(stored, that.stored);
	}

	@Override
	public int hashCode(){
		return Arrays.hashCode(stored);
	}

	public static BeamUnitStorage readFromNBT(@Nonnull String key, CompoundTag nbt){
		BeamUnitStorage out = new BeamUnitStorage();
		if(nbt.contains(key)){
			out.stored = nbt.getIntArray(key);
		}
		return out;
	}
}
