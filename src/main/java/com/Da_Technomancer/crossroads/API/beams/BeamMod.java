package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;

/**
 * An immutable class that represents one beam pulse lasting one cycle. It stores the energy, potential, stability, and void values and has several helper methods
 * For a mutable version, see BeamUnitStorage
 */
public class BeamMod {

	public static final BeamMod EMPTY = new BeamMod(1, 1, 1, 0);

	private final float[] multipliers = new float[4];//0: Energy, 1: Potential, 2: stability, 3: Void

	public BeamMod(float[] mults){
		this(mults[0], mults[1], mults[2], mults[3]);
	}

	public BeamMod(float energy, float potential, float stability, float voi){
		multipliers[0] = energy;
		multipliers[1] = potential;
		multipliers[2] = stability;
		multipliers[3] = voi;

		if(energy < 0 || potential < 0 || stability < 0 || voi < 0){
			throw new IllegalArgumentException("Negative BeamMod input! EN: " + energy + "; PO: " + potential + "; ST: " + stability + "; VO: " + voi);
		}
	}

	public float getEnergyMult(){
		return multipliers[0];
	}

	public float getPotentialMult(){
		return multipliers[1];
	}

	public float getStabilityMult(){
		return multipliers[2];
	}

	public float getVoidMult(){
		return multipliers[3];
	}

	public boolean isEmpty(){
		return multipliers[0] == 0 && multipliers[1] == 0 && multipliers[2] == 0 && multipliers[3] == 0;
	}

	/**
	 * @return A size four array containing energy, potential, stability, and void in that order. Changes to the array will not write back to the BeamMod
	 */
	public float[] getValues(){
		return Arrays.copyOf(multipliers, 4);
	}

	/**
	 * @param u
	 * @return A BeamUnit modified by this set of multipliers and the void conversion factor.
	 */
	public BeamUnit mult(BeamUnit u){
		float energy = u.getEnergy() * getEnergyMult();
		float potential = u.getPotential() * getPotentialMult();
		float stability = u.getStability() * getStabilityMult();

		// Void converts a percentage of the other colors to itself
		float voi = u.getVoid() + (energy + potential + stability) * getVoidMult();
		energy *= 1 - getVoidMult();
		potential *= 1 - getVoidMult();
		stability *= 1 - getVoidMult();

		// Numbers are truncated in order to prevent possible positive feedback loops
		// This is necessary since lenses can't simply redirect the excess elsewhere
		return new BeamUnit((int)energy, (int)potential, (int)stability, (int)voi);
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof BeamMod){
			BeamMod o = (BeamMod)other;
			return o == this || o.multipliers[0] == multipliers[0] && o.multipliers[1] == multipliers[1] && o.multipliers[2] == multipliers[2] && o.multipliers[3] == multipliers[3];
		}
		return false;
	}

	public void writeToNBT(@Nonnull String key, CompoundNBT nbt){
		CompoundNBT newNBT = new CompoundNBT();
		newNBT.putFloat("energy", multipliers[0]);
		newNBT.putFloat("potential", multipliers[1]);
		newNBT.putFloat("stability", multipliers[2]);
		newNBT.putFloat("void", multipliers[3]);
		nbt.put(key, newNBT);
	}

	public static BeamMod readFromNBT(@Nonnull String key, CompoundNBT nbt){
		if(nbt.contains(key)){
			CompoundNBT compound = nbt.getCompound(key);
			return new BeamMod(
					compound.getFloat("energy"),
					compound.getFloat("potential"),
					compound.getFloat("stability"),
					compound.getFloat("void")
			);
		}
		return BeamMod.EMPTY;
	}
}
