package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum EnumTransferMode implements IStringSerializable{
	
	INPUT(true, false),
	OUTPUT(false, true),
	BOTH(true, true),
	NONE(false, false);

	private final boolean canInput;
	private final boolean canOutput;
	
	EnumTransferMode(boolean canInput, boolean canOutput){
		this.canInput = canInput;
		this.canOutput = canOutput;
	}
	
	public boolean isInput(){
		return canInput;
	}
	
	public boolean isOutput(){
		return canOutput;
	}

	public boolean isConnection(){
		return this != NONE;
	}

	@Override
	public String getName(){
		return name().toLowerCase();
	}

	@Nonnull
	public static EnumTransferMode fromString(String s){
		try{
			return valueOf(s.toUpperCase(Locale.US));
		}catch(IllegalArgumentException e){
			return NONE;
		}
	}
}
