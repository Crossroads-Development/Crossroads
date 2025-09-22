package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;


public class CRCapabilities{
	public static BlockCapability<IHeatHandler, Direction> HEAT_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "heat_handler"), IHeatHandler.class);

	public static BlockCapability<IAxleHandler, Direction> AXLE_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "axle_handler"), IAxleHandler.class);

	public static BlockCapability<ICogHandler, Direction> COG_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "cog_handler"), ICogHandler.class);

	public static BlockCapability<IBeamHandler, Direction> BEAM_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "beam_handler"), IBeamHandler.class);

	public static BlockCapability<IAxisHandler, Direction> AXIS_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "axis_handler"), IAxisHandler.class);

	public static BlockCapability<IChemicalHandler, Direction> CHEMICAL_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "chemical_handler"), IChemicalHandler.class);

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent e){
		//TODO need to register every CR block providing any type of capability. For reference, see Essentials' ESEventHandlerCommon.registerCapabilities(...)

		e.registerItem(Capabilities.EnergyStorage.ITEM, LeydenJar.ENERGY_STORAGE_PROVIDER);
	}
}
