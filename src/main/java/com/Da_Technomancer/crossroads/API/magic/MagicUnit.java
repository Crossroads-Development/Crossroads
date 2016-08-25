package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import javax.annotation.Nullable;

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
	@Nullable
	public Color getTrueRGB(){
		if(energy == 0 && potential == 0 && stability == 0){
			return null;
		}
		double top = Math.max(energy, Math.max(potential, stability));
		
		return new Color((int) (255D * ((double) energy) / top), (int) (255D * ((double) potential) / top), (int) (255D * ((double) stability) / top));
	}
	
	/** Returns RGB with void. For rendering. */
	@Nullable
	public Color getRGB(){
		if(getTrueRGB() == null && voi == 0){
			return null;
		}
		
		double mult = getPower() / (getPower() + voi);
		
		Color col = getTrueRGB();
		return col == null ? new Color(0, 0, 0) : new Color((int) (col.getRed() * mult), (int) (col.getGreen() * mult), (int) (col.getBlue() * mult));
	}

}
