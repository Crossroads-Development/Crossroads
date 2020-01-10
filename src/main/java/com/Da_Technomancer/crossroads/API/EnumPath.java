package com.Da_Technomancer.crossroads.API;

import javax.annotation.Nullable;
import java.util.Locale;

public enum EnumPath{

	TECHNOMANCY((byte) 0),
	ALCHEMY((byte) 1),
	WITCHCRAFT((byte) 2);

	private final byte index;

	EnumPath(byte ind){
		index = ind;
	}

	public byte getIndex(){
		return index;
	}

	public static EnumPath fromIndex(byte ind){
		return values()[ind];
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}

	@Nullable
	public static EnumPath fromName(String name){
		try{
			return valueOf(name.toUpperCase(Locale.US));
		}catch(Exception e){
			return null;
		}
	}
}
