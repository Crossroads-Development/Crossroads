package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

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
	
	/** Returns RGB with void. For rendering. */
	public Color getRGB(){
		if(getTrueRGB() == null && voi == 0){
			return null;
		}
		
		double mult = ((double) (energy + potential + stability)) / (double) getPower();
		
		Color col = getTrueRGB();
		return col == null ? new Color(0, 0, 0) : new Color((int) Math.round(((double) col.getRed()) * mult), (int) Math.round(((double) col.getGreen()) * mult), (int) Math.round(((double) col.getBlue()) * mult));
	}
	
	/**Returns the MagicUnit with the lowest power with a color that matches the goal. Will decrease power to below count at cost of accuracy, setting count to -1 disables this
	 * Please note that this method is not 100% accurate due to taking rounding errors (RGB is stored as int, sometimes rounding occurs) into account.
	 */
	public static MagicUnit getClosestMatch(Color goal, int count){
		double partVoid = 1D - ((double) Math.max(goal.getRed(), Math.max(goal.getBlue(), goal.getGreen())) / 255D);
		
		double partR = ((double) goal.getRed()) / (double) Math.max(goal.getRed(), Math.max(goal.getBlue(), goal.getGreen()));
		double partG = ((double) goal.getGreen()) / (double) Math.max(goal.getRed(), Math.max(goal.getBlue(), goal.getGreen()));
		double partB = ((double) goal.getBlue()) / (double) Math.max(goal.getRed(), Math.max(goal.getBlue(), goal.getGreen()));
		
		double i = 0;
		
		double innac = partVoid == 0 ? 510D : 255D;
		
		while(true){
			++i;
			if(((i * partR) % 1 >= (1D - (i / innac)) || (i * partR) % 1 <= (i / innac)) && ((i * partG) % 1 >= (1D - (i / innac)) || (i * partG) % 1 <= (i / innac)) && ((i * partB) % 1 >= (1D - (i / innac)) || (i * partB) % 1 <= (i / innac))){
				partR *= i;
				partG *= i;
				partB *= i;
				partVoid *= (partR + partG + partB) / (1 - partVoid);
				break;
			}
		}
		
		if(count != -1 && partR + partG + partB + partVoid > count){
			double holder = count / (partR + partG + partB + partVoid);
			partR = Math.round(partR * holder);
			partG = Math.round(partG * holder);
			partB = Math.round(partB * holder);
			partVoid = Math.round(partVoid * holder);
		}
		
		return new MagicUnit((int) partR, (int) partG, (int) partB, (int) partVoid);
	}
	
	@Override
	public String toString(){
		return "Energy: " + energy + ", Potential: " + potential + ", Stability: " + stability + ", Void: " + voi;
	}
}
