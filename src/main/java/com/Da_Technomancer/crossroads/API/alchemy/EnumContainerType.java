package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.IStringSerializable;

public enum EnumContainerType implements IStringSerializable{
	
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
