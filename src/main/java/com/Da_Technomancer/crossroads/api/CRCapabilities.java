package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.integration.CRIntegration;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
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
		e.registerBlock(IRedstoneHandler.REDS_HANDLER_BLOCK, IRedstoneCapable.CAPABLE_PROVIDER, ESBlocks.wireCircuit, ESBlocks.wireJunctionCircuit, ESBlocks.consCircuit, ESBlocks.interfaceCircuit, ESBlocks.andCircuit, ESBlocks.notCircuit, ESBlocks.orCircuit, ESBlocks.xorCircuit, ESBlocks.maxCircuit, ESBlocks.minCircuit, ESBlocks.sumCircuit, ESBlocks.difCircuit, ESBlocks.prodCircuit, ESBlocks.quotCircuit, ESBlocks.powCircuit, ESBlocks.invCircuit, ESBlocks.sinCircuit, ESBlocks.cosCircuit, ESBlocks.tanCircuit, ESBlocks.asinCircuit, ESBlocks.acosCircuit, ESBlocks.atanCircuit, ESBlocks.equalsCircuit, ESBlocks.lessCircuit, ESBlocks.moreCircuit, ESBlocks.roundCircuit, ESBlocks.floorCircuit, ESBlocks.ceilCircuit, ESBlocks.logCircuit, ESBlocks.moduloCircuit, ESBlocks.absCircuit, ESBlocks.signCircuit, ESBlocks.readerCircuit, ESBlocks.timerCircuit, ESBlocks.timerCircuit, ESBlocks.delayCircuit, ESBlocks.pulseCircuitRising, ESBlocks.pulseCircuitFalling, ESBlocks.pulseCircuitDual, ESBlocks.dCounterCircuit, ESBlocks.redstoneTransmitter, ESBlocks.redstoneReceiver);
		CRIntegration.init();
	}
}
