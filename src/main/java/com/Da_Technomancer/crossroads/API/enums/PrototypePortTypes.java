package com.Da_Technomancer.crossroads.API.enums;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

public enum PrototypePortTypes{
	
	HEAT(Capabilities.HEAT_HANDLER_CAPABILITY, new ResourceLocation(Main.MODID, "TODO"), true, true),
	ROTARY(Capabilities.AXLE_HANDLER_CAPABILITY, new ResourceLocation(Main.MODID, "TODO"), true, true),
	MAGIC_IN(Capabilities.MAGIC_HANDLER_CAPABILITY, new ResourceLocation(Main.MODID, "TODO"), true, false),
	MAGIC_OUT(Capabilities.MAGIC_HANDLER_CAPABILITY, new ResourceLocation(Main.MODID, "TODO"), false, true),
	REDSTONE_IN(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, new ResourceLocation(Main.MODID, "TODO"), true, false),
	REDSTONE_OUT(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, new ResourceLocation(Main.MODID, "TODO"), false, true);

	private final Capability<?> cap;
	private final ResourceLocation text;
	private final boolean input;
	private final boolean output;
	
	PrototypePortTypes(Capability<?> cap, ResourceLocation text, boolean input, boolean output){
		this.cap = cap;
		this.text = text;
		this.input = input;
		this.output = output;
	}
	
	public Capability<?> getCapability(){
		return cap;
	}
	
	public ResourceLocation getTexture(){
		return text;
	}
	
	public boolean isInput(){
		return input;
	}
	
	public boolean isOutput(){
		return output;
	}
}
