package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

public enum PrototypePortTypes{
	
	HEAT(Capabilities.HEAT_CAPABILITY, new ResourceLocation(Main.MODID, "blocks/prototype/heat"), true, true, true, true),
	ROTARY(Capabilities.AXLE_CAPABILITY, new ResourceLocation(Main.MODID, "blocks/prototype/rotary"), true, true, true, true),
	MAGIC_IN(Capabilities.BEAM_CAPABILITY, new ResourceLocation(Main.MODID, "blocks/prototype/magic_in"), true, false, true, false),
	MAGIC_OUT(Capabilities.BEAM_CAPABILITY, new ResourceLocation(Main.MODID, "blocks/prototype/magic_out"), false, true, false, true),
	REDSTONE_IN(Capabilities.ADVANCED_REDSTONE_CAPABILITY, new ResourceLocation(Main.MODID, "blocks/prototype/redstone_in"), true, false, false, true),
	REDSTONE_OUT(Capabilities.ADVANCED_REDSTONE_CAPABILITY, new ResourceLocation(Main.MODID, "blocks/prototype/redstone_out"), false, true, true, false);

	private final Capability<?> cap;
	private final ResourceLocation text;
	private final boolean input;
	private final boolean output;
	/**
	 * Whether the internal side should expose the capability. Should be checked in the port. 
	 */
	private final boolean exposeInternal;
	/**
	 * Whether the external side should expose the capability. Should be checked in the IPrototypeOwner.
	 */
	private final boolean exposeExternal;
	
	PrototypePortTypes(Capability<?> cap, ResourceLocation text, boolean input, boolean output, boolean exposeExternal, boolean exposeInternal){
		this.cap = cap;
		this.text = text;
		this.input = input;
		this.output = output;
		this.exposeInternal = exposeInternal;
		this.exposeExternal = exposeExternal;
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
	
	public boolean exposeInternal(){
		return exposeInternal;
	}
	
	public boolean exposeExternal(){
		return exposeExternal;
	}
	
	@Override
	public String toString(){
		return name().replaceAll("_", " ");
	}
}
