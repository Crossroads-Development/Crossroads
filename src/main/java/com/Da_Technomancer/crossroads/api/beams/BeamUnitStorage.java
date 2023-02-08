package com.Da_Technomancer.crossroads.api.beams;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Mutable version of BeamUnit with caching and helper methods
 */
public class BeamUnitStorage{

	private int[] stored = new int[4];
	/**
	 * Cache of the beamUnit for stored
	 */
	@Nullable
	private BeamUnit beamUnit;

	public void addBeam(@Nonnull BeamUnit mag){
		stored[0] += mag.getEnergy();
		stored[1] += mag.getPotential();
		stored[2] += mag.getStability();
		stored[3] += mag.getVoid();
		if(beamUnit != null && beamUnit.isEmpty()){
			//Empty storage; the added beam is the new cache
			beamUnit = mag;
		}else{
			//Reset the cache
			beamUnit = null;
		}
	}

	public void addBeam(BeamUnitStorage otherStorage){
		for(int i = 0; i < 4; i++){
			stored[i] += otherStorage.stored[i];
		}
		beamUnit = null;//Reset the cache
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
		beamUnit = null;//Reset the cache
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
		beamUnit = BeamUnit.EMPTY;
	}

	@Nonnull
	public BeamUnit getOutput(){
		if(beamUnit == null){
			beamUnit = isEmpty() ? BeamUnit.EMPTY : new BeamUnit(stored[0], stored[1], stored[2], stored[3]);
		}
		//Use the cached value
		return beamUnit;
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
