package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.DefaultStorageHelper.DefaultStorage;
import com.Da_Technomancer.crossroads.API.heat.DefaultHeatHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.magic.DefaultMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultCogHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultSlaveAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities{

	@CapabilityInject(IHeatHandler.class)
	public static Capability<IHeatHandler> HEAT_HANDLER_CAPABILITY = null;

	@CapabilityInject(IAxleHandler.class)
	public static Capability<IAxleHandler> AXLE_HANDLER_CAPABILITY = null;
	
	@CapabilityInject(ICogHandler.class)
	public static Capability<ICogHandler> COG_HANDLER_CAPABILITY = null;
	
	@CapabilityInject(IMagicHandler.class)
	public static Capability<IMagicHandler> MAGIC_HANDLER_CAPABILITY = null;
	
	@CapabilityInject(IAxisHandler.class)
	public static Capability<IAxisHandler> AXIS_HANDLER_CAPABILITY = null;
	
	@CapabilityInject(ISlaveAxisHandler.class)
	public static Capability<ISlaveAxisHandler> SLAVE_AXIS_HANDLER_CAPABILITY = null;

	public static void register(){
		CapabilityManager.INSTANCE.register(IHeatHandler.class, new DefaultStorage<>(), DefaultHeatHandler.class);
		CapabilityManager.INSTANCE.register(IAxleHandler.class, new DefaultStorage<>(), DefaultAxleHandler.class);
		CapabilityManager.INSTANCE.register(ICogHandler.class, new DefaultStorage<>(), DefaultCogHandler.class);
		CapabilityManager.INSTANCE.register(IMagicHandler.class, new DefaultStorage<>(), DefaultMagicHandler.class);
		CapabilityManager.INSTANCE.register(IAxisHandler.class, new DefaultStorage<>(), DefaultAxisHandler.class);
		CapabilityManager.INSTANCE.register(ISlaveAxisHandler.class, new DefaultStorage<>(), DefaultSlaveAxisHandler.class);
	}
}
