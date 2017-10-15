package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.IStringSerializable;

public enum EnumContainerType implements IStringSerializable{
	
	NONE(),
	GLASS(),
	CRYSTAL();

	@Override
	public String getName(){
		return name().toLowerCase();
	}
}
