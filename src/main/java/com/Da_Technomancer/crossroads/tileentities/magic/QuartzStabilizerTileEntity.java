package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;

import net.minecraft.util.ITickable;

public class QuartzStabilizerTileEntity extends BeamRenderTE implements ITickable{

	private boolean large;
	private int[] stored = new int[4];
	private final int[] LIMIT = new int[] {10, 30};
	private final int[] RATE = new int[] {3, 9};
	
	public QuartzStabilizerTileEntity(){
		
	}
	
	public QuartzStabilizerTileEntity(boolean large){
		this.large = large;
	}
	
	@Override
	public Triple<Color, Integer, Integer> getBeam(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(){
		// TODO Auto-generated method stub
		
	}

}
