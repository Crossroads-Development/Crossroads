package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.DefaultStorageHelper.DefaultStorage;
import com.Da_Technomancer.crossroads.API.heat.DefaultHeatHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultRotaryHandler;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities{

	@CapabilityInject(IHeatHandler.class)
	public static Capability<IHeatHandler> HEAT_HANDLER_CAPABILITY = null;

	@CapabilityInject(IRotaryHandler.class)
	public static Capability<IRotaryHandler> ROTARY_HANDLER_CAPABILITY = null;

	public static void register(){
		CapabilityManager.INSTANCE.register(IHeatHandler.class, new DefaultStorage<>(), DefaultHeatHandler.class);
		CapabilityManager.INSTANCE.register(IRotaryHandler.class, new DefaultStorage<>(), DefaultRotaryHandler.class);
	}
}
