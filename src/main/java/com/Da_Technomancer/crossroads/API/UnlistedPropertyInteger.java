package com.Da_Technomancer.crossroads.API;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyInteger implements IUnlistedProperty<Integer>{

	private final String name;
	private final int lowerBound;
	private final int upperBound;

	public UnlistedPropertyInteger(String name, int lowerBound, int upperBound){
		this.name = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public boolean isValid(Integer value){
		return value != null && lowerBound <= value && value <= upperBound;
	}

	@Override
	public Class<Integer> getType(){
		return Integer.class;
	}

	@Override
	public String valueToString(Integer value){
		return value.toString();
	}
}
