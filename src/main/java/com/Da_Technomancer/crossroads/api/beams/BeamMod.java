package com.Da_Technomancer.crossroads.api.beams;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;

/**
 * Represents a modification to be performed to incoming beam units
 */
public record BeamMod(float energyMult, float potentialMult, float stabilityMult, float voidMult, float voidConvert){

	public static Codec<BeamMod> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("energy", 1f).forGetter(BeamMod::energyMult),
			Codec.FLOAT.optionalFieldOf("potential", 1f).forGetter(BeamMod::potentialMult),
			Codec.FLOAT.optionalFieldOf("stability", 1f).forGetter(BeamMod::stabilityMult),
			Codec.FLOAT.optionalFieldOf("void", 1f).forGetter(BeamMod::voidMult),
			Codec.FLOAT.optionalFieldOf("void_convert", 0f).forGetter(BeamMod::voidConvert)
	).apply(instance, BeamMod::new));

	public static StreamCodec<ByteBuf, BeamMod> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, BeamMod::energyMult,
			ByteBufCodecs.FLOAT, BeamMod::potentialMult,
			ByteBufCodecs.FLOAT, BeamMod::stabilityMult,
			ByteBufCodecs.FLOAT, BeamMod::voidMult,
			ByteBufCodecs.FLOAT, BeamMod::voidConvert,
			BeamMod::new
	);

	public static final BeamMod IDENTITY = new BeamMod(1, 1, 1, 1, 0);

	public BeamMod(float[] mults){
		this(mults[0], mults[1], mults[2], mults[3], mults[4]);
	}

	public BeamMod{
		if(energyMult < 0 || potentialMult < 0 || stabilityMult < 0 || voidMult < 0 || voidConvert < 0){
			throw new IllegalArgumentException("Negative BeamMod input! EN: " + energyMult + "; PO: " + potentialMult + "; ST: " + stabilityMult + "; VO: " + voidMult + "; VO-CONV: " + voidConvert);
		}
	}

	public boolean isEmpty(){
		return equals(BeamMod.IDENTITY);
	}

	/**
	 * @param u The beam unit to modify
	 * @return A BeamUnit modified by this set of multipliers and the void conversion factor.
	 */
	public BeamUnit mult(BeamUnit u){
		int energy = Math.round(u.getEnergy() * this.energyMult());
		int potential = Math.round(u.getPotential() * this.potentialMult());
		int stability = Math.round(u.getStability() * this.stabilityMult());
		int voi = Math.round(u.getVoid() * this.voidMult());

		int powToVoid = Math.round((energy + potential + stability) * this.voidConvert());
		if(powToVoid > 0){
			int[] toWithdraw = MiscUtil.withdrawExact(new int[] {energy, potential, stability}, powToVoid);

			energy -= toWithdraw[0];
			potential -= toWithdraw[1];
			stability -= toWithdraw[2];
			voi += toWithdraw[0] + toWithdraw[1] + toWithdraw[2];
		}

		return new BeamUnit(energy, potential, stability, voi);
	}

	public void writeToNBT(@Nonnull String key, CompoundTag nbt){
		CompoundTag newNBT = new CompoundTag();
		newNBT.putFloat("energy", energyMult);
		newNBT.putFloat("potential", potentialMult);
		newNBT.putFloat("stability", stabilityMult);
		newNBT.putFloat("void", voidMult);
		newNBT.putFloat("voidConvert", voidConvert);
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
		return BeamMod.IDENTITY;
	}
}
