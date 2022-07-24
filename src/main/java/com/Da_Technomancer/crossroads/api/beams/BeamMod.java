package com.Da_Technomancer.crossroads.api.beams;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * An immutable class that represents a modification to be performed to incoming beam units
 */
public class BeamMod {

	public static final BeamMod EMPTY = new BeamMod(1, 1, 1, 1, 0);

	private final float[] multipliers = new float[5];//0: Energy, 1: Potential, 2: stability, 3: Void, 4: Void Convert

	public BeamMod(float[] mults){
		this(mults[0], mults[1], mults[2], mults[3], mults[4]);
	}

	public BeamMod(float energy, float potential, float stability, float voi, float voiConv){
		multipliers[0] = energy;
		multipliers[1] = potential;
		multipliers[2] = stability;
		multipliers[3] = voi;
		multipliers[4] = voiConv;

		if(energy < 0 || potential < 0 || stability < 0 || voi < 0 || voiConv < 0){
			throw new IllegalArgumentException("Negative BeamMod input! EN: " + energy + "; PO: " + potential + "; ST: " + stability + "; VO: " + voi + "; VO-CONV: " + voiConv);
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

	public float getVoidConvert(){
		return multipliers[4];
	}

	public boolean isEmpty(){
		return this == BeamMod.EMPTY ||
				multipliers[0] == 1
				&& multipliers[1] == 1
				&& multipliers[2] == 1
				&& multipliers[3] == 1
				&& multipliers[4] == 0;
	}

	/**
	 * @return A size five array containing energy, potential, stability, void, and void conversion in that order.
	 * Changes to the array will not write back to the BeamMod
	 */
	public float[] getValues(){
		return Arrays.copyOf(multipliers, 5);
	}

	/**
	 * @param u The beam unit to modify
	 * @return A BeamUnit modified by this set of multipliers and the void conversion factor.
	 */
	public BeamUnit mult(BeamUnit u){
		int energy = Math.round(u.getEnergy() * getEnergyMult());
		int potential = Math.round(u.getPotential() * getPotentialMult());
		int stability = Math.round(u.getStability() * getStabilityMult());
		int voi = Math.round(u.getVoid() * getVoidMult());

		int powToVoid = Math.round((energy + potential + stability) * getVoidConvert());
		if(powToVoid > 0) {
			int[] toWithdraw = MiscUtil.withdrawExact(new int[]{energy, potential, stability}, powToVoid);

			energy -= toWithdraw[0];
			potential -= toWithdraw[1];
			stability -= toWithdraw[2];
			voi += toWithdraw[0] + toWithdraw[1] + toWithdraw[2];
		}

		return new BeamUnit(energy, potential, stability, voi);
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof BeamMod){
			BeamMod o = (BeamMod)other;
			return o == this ||
					o.multipliers[0] == multipliers[0]
					&& o.multipliers[1] == multipliers[1]
					&& o.multipliers[2] == multipliers[2]
					&& o.multipliers[3] == multipliers[3]
					&& o.multipliers[4] == multipliers[4];
		}
		return false;
	}

	public void writeToNBT(@Nonnull String key, CompoundTag nbt){
		CompoundTag newNBT = new CompoundTag();
		newNBT.putFloat("energy", multipliers[0]);
		newNBT.putFloat("potential", multipliers[1]);
		newNBT.putFloat("stability", multipliers[2]);
		newNBT.putFloat("void", multipliers[3]);
		newNBT.putFloat("voidConvert", multipliers[4]);
		nbt.put(key, newNBT);
	}

	public static BeamMod readFromNBT(@Nonnull String key, CompoundTag nbt){
		if(nbt.contains(key)){
			CompoundTag compound = nbt.getCompound(key);
			return new BeamMod(
					compound.getFloat("energy"),
					compound.getFloat("potential"),
					compound.getFloat("stability"),
					compound.getFloat("void"),
					compound.getFloat("voidConvert")
			);
		}
		return BeamMod.EMPTY;
	}
}
