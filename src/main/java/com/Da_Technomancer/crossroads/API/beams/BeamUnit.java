package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;

import java.awt.*;
import java.util.Arrays;

public class BeamUnit{

	private final int contents[] = new int[4];//0: Energy, 1: Potential, 2: stability, 3: Void

	public BeamUnit(int[] magic){
		this(magic[0], magic[1], magic[2], magic[3]);
	}

	public BeamUnit(int energy, int potential, int stability, int voi){
		contents[0] = energy;
		contents[1] = potential;
		contents[2] = stability;
		contents[3] = voi;

		if(energy < 0 || potential < 0 || stability < 0 || voi < 0){
			throw new IllegalArgumentException("Negative BeamUnit input! EN: " + energy + "; PO: " + potential + "; ST: " + stability + "; VO: " + voi);
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

	/**
	 * @return A size four array containing energy, potential, stability, and void in that order. Changes to the array will not write back to the BeamUnit
	 */
	public int[] getValues(){
		return Arrays.copyOf(contents, 4);
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

	public BeamUnit mult(double multiplier, boolean floor){
		return mult(multiplier, multiplier, multiplier, multiplier, floor);
	}

	public BeamUnit mult(double e, double p, double s, double v, boolean floor){
		return floor ? new BeamUnit((int) Math.floor(e * (double) contents[0]), (int) Math.floor(p * (double) contents[1]), (int) Math.floor(s * (double) contents[2]), (int) Math.floor(v * (double) contents[3])) : new BeamUnit(MiscUtil.safeRound(e * (double) contents[0]), MiscUtil.safeRound(p * (double) contents[1]), MiscUtil.safeRound(s * (double) contents[2]), MiscUtil.safeRound(v * (double) contents[3]));
	}

	@Override
	public String toString(){
		Color col = getRGB();
		return EnumBeamAlignments.getAlignment(this).toString() + "-R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue() + "-En: " + contents[0] + ", Po: " + contents[1] + ", St: " + contents[2] + ", Vo: " + contents[3];
	}

	@Override
	public boolean equals(Object other){
		return other == this || (other instanceof BeamUnit && ((BeamUnit) other).getEnergy() == contents[0] && ((BeamUnit) other).getStability() == contents[2] && ((BeamUnit) other).getPotential() == contents[1] && ((BeamUnit) other).getVoid() == contents[3]);
	}
	
	@Override
	public int hashCode(){
		return (contents[0] << 12) + ((contents[1] & 0xE) << 8) + ((contents[2] & 0xE) << 4) + (contents[3] & 0xE);
	}
}
