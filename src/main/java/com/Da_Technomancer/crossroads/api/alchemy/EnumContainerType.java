package com.Da_Technomancer.crossroads.api.alchemy;

import net.minecraft.util.StringRepresentable;

public enum EnumContainerType implements StringRepresentable{
	
	NONE(),
	GLASS(),
	CRYSTAL();

	@Override
	public String getSerializedName(){
		return name().toLowerCase();
	}

	public boolean connectsWith(EnumContainerType otherType){
		switch(otherType){
			case NONE:
				return true;
			case GLASS:
				return this == NONE || this == GLASS;
			case CRYSTAL:
				return this == NONE || this == CRYSTAL;
		}
		return false;
	}
}
