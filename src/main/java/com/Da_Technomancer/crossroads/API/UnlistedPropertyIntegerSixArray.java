package com.Da_Technomancer.crossroads.API;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyIntegerSixArray implements IUnlistedProperty<Integer[]>{

	private final String name;

	public UnlistedPropertyIntegerSixArray(String name){
		this.name = name;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public boolean isValid(Integer[] value){
		return value.length == 6;
	}

	@Override
	public Class<Integer[]> getType(){
		return Integer[].class;
	}

	@Override
	public String valueToString(Integer[] value){
		return value.toString();
	}

}
