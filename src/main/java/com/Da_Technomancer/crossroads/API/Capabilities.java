package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.DefaultStorageHelper.DefaultStorage;
import com.Da_Technomancer.crossroads.API.alchemy.DefaultChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.heat.DefaultHeatHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.magic.DefaultMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.redstone.DefaultAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultCogHandler;
import com.Da_Technomancer.crossroads.API.rotary.DefaultSlaveAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;

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
	
	@CapabilityInject(IAdvancedRedstoneHandler.class)
	public static Capability<IAdvancedRedstoneHandler> ADVANCED_REDSTONE_HANDLER_CAPABILITY = null;
	
	@CapabilityInject(IChemicalHandler.class)
	public static Capability<IChemicalHandler> CHEMICAL_HANDLER_CAPABILITY = null;

	public static void register(){
		CapabilityManager.INSTANCE.register(IHeatHandler.class, new DefaultStorage<>(), DefaultHeatHandler::new);
		CapabilityManager.INSTANCE.register(IAxleHandler.class, new DefaultStorage<>(), DefaultAxleHandler::new);
		CapabilityManager.INSTANCE.register(ICogHandler.class, new DefaultStorage<>(), DefaultCogHandler::new);
		CapabilityManager.INSTANCE.register(IMagicHandler.class, new DefaultStorage<>(), DefaultMagicHandler::new);
		CapabilityManager.INSTANCE.register(IAxisHandler.class, new DefaultStorage<>(), DefaultAxisHandler::new);
		CapabilityManager.INSTANCE.register(ISlaveAxisHandler.class, new DefaultStorage<>(), DefaultSlaveAxisHandler::new);
		CapabilityManager.INSTANCE.register(IAdvancedRedstoneHandler.class, new DefaultStorage<>(), DefaultAdvancedRedstoneHandler::new);
		CapabilityManager.INSTANCE.register(IChemicalHandler.class, new DefaultStorage<>(), DefaultChemicalHandler::new);
	}
}
