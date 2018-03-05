package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.MiscOp;

import net.minecraft.nbt.NBTTagCompound;

public class MagicUnit{

	private final int energy;
	private final int potential;
	private final int stability;
	private final int voi;

	public MagicUnit(int energy, int potential, int stability, int voi){
		this.energy = energy;
		this.potential = potential;
		this.stability = stability;
		this.voi = voi;
	}

	public int getEnergy(){
		return energy;
	}

	public int getPotential(){
		return potential;
	}

	public int getStability(){
		return stability;
	}

	public int getVoid(){
		return voi;
	}

	public int getPower(){
		return energy + potential + stability + voi;
	}

	/** Returns the RGB value when ignoring void*/
	public Color getTrueRGB(){
		if(energy == 0 && potential == 0 && stability == 0){
			return null;
		}
		double top = Math.max(energy, Math.max(potential, stability));

		return new Color((int) Math.round(255D * ((double) energy) / top), (int) Math.round(255D * ((double) potential) / top), (int) Math.round(255D * ((double) stability) / top));
	}

	/** Returns RGB with void.*/
	public Color getRGB(){
		if(getTrueRGB() == null){
			return new Color(0, 0, 0);
		}

		double mult = ((double) (energy + potential + stability)) / (double) getPower();

		Color col = getTrueRGB();
		return new Color((int) Math.round(((double) col.getRed()) * mult), (int) Math.round(((double) col.getGreen()) * mult), (int) Math.round(((double) col.getBlue()) * mult));
	}

	public MagicUnit mult(double multiplier, boolean floor){
		return mult(multiplier, multiplier, multiplier, multiplier, floor);
	}

	public MagicUnit mult(double e, double p, double s, double v, boolean floor){
		return floor ? new MagicUnit((int) Math.floor(e * (double) energy), (int) Math.floor(p * (double) potential), (int) Math.floor(s * (double) stability), (int) Math.floor(v * (double) voi)) : new MagicUnit(MiscOp.safeRound(e * (double) energy), MiscOp.safeRound(p * (double) potential), MiscOp.safeRound(s * (double) stability), MiscOp.safeRound(v * (double) voi));
	}

	public NBTTagCompound setNBT(NBTTagCompound nbt, @Nullable String key){
		NBTTagCompound holder = key == null ? nbt : new NBTTagCompound();
		holder.setInteger("en", energy);
		holder.setInteger("po", potential);
		holder.setInteger("st", stability);
		holder.setInteger("vo", voi);

		if(key != null){
			nbt.setTag(key, holder);
		}
		return nbt;
	}

	@Nullable
	public static MagicUnit loadNBT(NBTTagCompound nbt, @Nullable String key){
		if(key == null ? !nbt.hasKey("en") : !nbt.hasKey(key)){
			return null;
		}
		NBTTagCompound holder = key == null ? nbt : nbt.getCompoundTag(key);
		return new MagicUnit(holder.getInteger("en"), holder.getInteger("po"), holder.getInteger("st"), holder.getInteger("vo"));
	}

	@Override
	public String toString(){
		Color col = getRGB();
		return "R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue() + ", Element: " + EnumMagicElements.getElement(this).toString() + ", Energy: " + energy + ", Potential: " + potential + ", Stability: " + stability + ", Void: " + voi;
	}

	@Override
	public boolean equals(Object other){
		return other == this || (other instanceof MagicUnit && ((MagicUnit) other).getEnergy() == energy && ((MagicUnit) other).getStability() == stability && ((MagicUnit) other).getPotential() == potential && ((MagicUnit) other).getVoid() == voi);
	}
	
	@Override
	public int hashCode(){
		return (energy << 12) + ((potential & 0xE) << 8) + ((stability & 0xE) << 4) + (voi & 0xE);
	}
}
