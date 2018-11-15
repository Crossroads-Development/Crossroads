package com.Da_Technomancer.crossroads.API.magic;

import com.Da_Technomancer.crossroads.API.MiscUtil;

import java.awt.*;

public class MagicUnit{

	private final int contents[] = new int[4];//0: Energy, 1: Potential, 2: stability, 3: Void

	public MagicUnit(int[] magic){
		this(magic[0], magic[1], magic[2], magic[3]);
	}

	public MagicUnit(int energy, int potential, int stability, int voi){
		contents[0] = energy;
		contents[1] = potential;
		contents[2] = stability;
		contents[3] = voi;

		if(energy < 0 || potential < 0 || stability < 0 || voi < 0){
			throw new IllegalArgumentException("Negative MagicUnit input! EN: " + energy + "; PO: " + potential + "; ST: " + stability + "; VO: " + voi);
		}
	}

	public int getEnergy(){
		return contents[0];
	}

	public int getPotential(){
		return contents[1];
	}

	public int getStability(){
		return contents[2];
	}

	public int getVoid(){
		return contents[3];
	}

	public int getPower(){
		return contents[0] + contents[1] + contents[2] + contents[3];
	}

	/** Returns the RGB value when ignoring void*/
	public Color getTrueRGB(){
		if(contents[0] == 0 && contents[1] == 0 && contents[2] == 0){
			return null;
		}
		double top = Math.max(contents[0], Math.max(contents[1], contents[2]));

		return new Color((int) Math.round(255D * ((double) contents[0]) / top), (int) Math.round(255D * ((double) contents[1]) / top), (int) Math.round(255D * ((double) contents[2]) / top));
	}

	/** Returns RGB with void.*/
	public Color getRGB(){
		if(getTrueRGB() == null){
			return new Color(0, 0, 0);
		}

		double mult = ((double) (contents[0] + contents[1] + contents[2])) / (double) getPower();

		Color col = getTrueRGB();
		return new Color((int) Math.round(((double) col.getRed()) * mult), (int) Math.round(((double) col.getGreen()) * mult), (int) Math.round(((double) col.getBlue()) * mult));
	}

	public MagicUnit mult(double multiplier, boolean floor){
		return mult(multiplier, multiplier, multiplier, multiplier, floor);
	}

	public MagicUnit mult(double e, double p, double s, double v, boolean floor){
		return floor ? new MagicUnit((int) Math.floor(e * (double) contents[0]), (int) Math.floor(p * (double) contents[1]), (int) Math.floor(s * (double) contents[2]), (int) Math.floor(v * (double) contents[3])) : new MagicUnit(MiscUtil.safeRound(e * (double) contents[0]), MiscUtil.safeRound(p * (double) contents[1]), MiscUtil.safeRound(s * (double) contents[2]), MiscUtil.safeRound(v * (double) contents[3]));
	}

	@Override
	public String toString(){
		Color col = getRGB();
		return "R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue() + ", Element: " + EnumMagicElements.getElement(this).toString() + ", Energy: " + contents[0] + ", Potential: " + contents[1] + ", Stability: " + contents[2] + ", Void: " + contents[3];
	}

	@Override
	public boolean equals(Object other){
		return other == this || (other instanceof MagicUnit && ((MagicUnit) other).getEnergy() == contents[0] && ((MagicUnit) other).getStability() == contents[2] && ((MagicUnit) other).getPotential() == contents[1] && ((MagicUnit) other).getVoid() == contents[3]);
	}
	
	@Override
	public int hashCode(){
		return (contents[0] << 12) + ((contents[1] & 0xE) << 8) + ((contents[2] & 0xE) << 4) + (contents[3] & 0xE);
	}
}
