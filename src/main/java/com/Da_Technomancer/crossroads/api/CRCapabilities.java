package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalCapable;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.beams.IBeamCapable;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
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
		e.registerBlock(Capabilities.ItemHandler.BLOCK, IItemCapable.CAPABLE_PROVIDER, CRBlocks.autoInjector, CRBlocks.beamExtractor, CRBlocks.blastFurnace, CRBlocks.bloodBeamLinker, CRBlocks.bloodCentrifuge,
				CRBlocks.brewingVat, CRBlocks.cageCharger, CRBlocks.coldStorage, CRBlocks.cultivatorVat, CRBlocks.embryoLab, CRBlocks.fatCollector, CRBlocks.fatCongealer,
				CRBlocks.firebox, CRBlocks.fluidCoolingChamber, CRBlocks.formulationVat, CRBlocks.heatingCrucible, CRBlocks.hydroponicsTrough, CRBlocks.icebox,
				CRBlocks.incubator, CRBlocks.itemCannon, CRBlocks.lensFrame, CRBlocks.millstone, CRBlocks.oreCleanser, CRBlocks.reactionChamberGlass, CRBlocks.reactionChamberCrystal,
				CRBlocks.saltReactor, CRBlocks.smelter, CRBlocks.stampMill, CRBlocks.stasisStorage, CRBlocks.steamer, CRBlocks.teslaCoil,
				CRBlocks.waterCentrifuge, CRBlocks.windingTable);
		e.registerBlock(Capabilities.FluidHandler.BLOCK, IFluidCapable.CAPABLE_PROVIDER, CRBlocks.blastFurnace, CRBlocks.copshowiumCreationChamber, CRBlocks.cultivatorVat, CRBlocks.fatCollector, CRBlocks.fatCongealer,
				CRBlocks.fatFeeder, CRBlocks.fluidCoolingChamber, CRBlocks.fluidInjectorGlass, CRBlocks.fluidInjectorCrystal, CRBlocks.fluidTank, CRBlocks.fluidTube, CRBlocks.redstoneFluidTube, CRBlocks.fluidVoid,
				CRBlocks.formulationVat, CRBlocks.heatingCrucible, CRBlocks.hydroponicsTrough, CRBlocks.oreCleanser, CRBlocks.radiator, CRBlocks.rotaryPump, CRBlocks.saltReactor, CRBlocks.steamBoiler,
				CRBlocks.steamTurbine, CRBlocks.steamer, CRBlocks.waterCentrifuge);
		e.registerBlock(IRedstoneHandler.REDS_HANDLER_BLOCK, IRedstoneCapable.CAPABLE_PROVIDER, CRBlocks.beamSiphon, CRBlocks.beamSplitter, CRBlocks.heatLimiterRedstone, CRBlocks.itemCannon, CRBlocks.redstoneAxis, CRBlocks.sequenceBox);
		e.registerBlock(Capabilities.EnergyStorage.BLOCK, IEnergyCapable.CAPABLE_PROVIDER, CRBlocks.atmosCharger, CRBlocks.chargingStand, CRBlocks.chronoHarness, CRBlocks.dynamo, CRBlocks.lodestoneDynamo,
				CRBlocks.reactionChamberGlass, CRBlocks.reactionChamberCrystal, CRBlocks.teslaCoil, CRBlocks.voltusGenerator);
		e.registerBlock(CRCapabilities.AXIS_CAPABILITY, IAxisCapable.CAPABLE_PROVIDER, CRBlocks.crystalMasterAxis, CRBlocks.masterAxisCreative, CRBlocks.masterAxis, CRBlocks.redstoneAxis);
		e.registerBlock(CRCapabilities.AXLE_CAPABILITY, IAxleCapable.CAPABLE_PROVIDER, CRBlocks.beamCannon, CRBlocks.blastFurnace, CRBlocks.bloodCentrifuge, CRBlocks.dynamo, CRBlocks.gatewayController,
				CRBlocks.itemCannon, CRBlocks.largeGearMaster, CRBlocks.lodestoneDynamo, CRBlocks.lodestoneTurbine, CRBlocks.mechanism, CRBlocks.millstone, CRBlocks.rotaryDrill, CRBlocks.rotaryDrillGold,
				CRBlocks.rotaryPump, CRBlocks.stampMill, CRBlocks.steamTurbine, CRBlocks.stirlingEngine, CRBlocks.waterCentrifuge, CRBlocks.windTurbine, CRBlocks.windingTable);
		e.registerBlock(CRCapabilities.COG_CAPABILITY, ICogCapable.CAPABLE_PROVIDER, CRBlocks.largeGearSlave, CRBlocks.mechanism);
		e.registerBlock(CRCapabilities.HEAT_CAPABILITY, IHeatCapable.CAPABLE_PROVIDER, CRBlocks.brewingVat, CRBlocks.chargingStand, CRBlocks.coldStorage, CRBlocks.fatCollector, CRBlocks.firebox, CRBlocks.fluidCoolingChamber, CRBlocks.formulationVat, CRBlocks.glasswareHolder,
				CRBlocks.HEAT_CABLES.get(HeatInsulators.CERAMIC), CRBlocks.HEAT_CABLES.get(HeatInsulators.ICE), CRBlocks.HEAT_CABLES.get(HeatInsulators.DENSUS), CRBlocks.HEAT_CABLES.get(HeatInsulators.DIRT), CRBlocks.HEAT_CABLES.get(HeatInsulators.OBSIDIAN), CRBlocks.HEAT_CABLES.get(HeatInsulators.SLIME), CRBlocks.HEAT_CABLES.get(HeatInsulators.WOOL),
				CRBlocks.heatLimiterBasic, CRBlocks.heatLimiterRedstone, CRBlocks.heatReservoir, CRBlocks.heatReservoirCreative, CRBlocks.heatSink, CRBlocks.heatedTubeGlass, CRBlocks.heatedTubeCrystal,
				CRBlocks.heatingCrucible, CRBlocks.icebox, CRBlocks.incubator, CRBlocks.maxwellDemon, CRBlocks.radiator, CRBlocks.reactionChamberGlass, CRBlocks.reactionChamberCrystal,
				CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.WOOL), CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.SLIME), CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.DIRT), CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.ICE), CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.OBSIDIAN), CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.CERAMIC), CRBlocks.REDSTONE_HEAT_CABLES.get(HeatInsulators.DENSUS),
				CRBlocks.saltReactor, CRBlocks.smelter, CRBlocks.solarHeater, CRBlocks.steamBoiler, CRBlocks.stirlingEngine);
		e.registerBlock(CRCapabilities.BEAM_CAPABILITY, IBeamCapable.CAPABLE_PROVIDER, CRBlocks.beaconHarness, CRBlocks.beamCannon, CRBlocks.beamExtractorCreative, CRBlocks.beamExtractor, CRBlocks.beamRedirector,
				CRBlocks.beamReflectorSensitive, CRBlocks.beamReflector, CRBlocks.beamSiphon, CRBlocks.beamSplitter, CRBlocks.bloodBeamLinker, CRBlocks.cageCharger, CRBlocks.chunkAccelerator,
				CRBlocks.clockworkStabilizer, CRBlocks.copshowiumCreationChamber, CRBlocks.crystalMasterAxis, CRBlocks.crystallinePrism, CRBlocks.gatewayController, CRBlocks.lensFrame, CRBlocks.quartzStabilizer,
				CRBlocks.stasisStorage, CRBlocks.temporalAccelerator);
		e.registerBlock(CRCapabilities.CHEMICAL_CAPABILITY, IChemicalCapable.CAPABLE_PROVIDER, CRBlocks.alchemicalTubeGlass, CRBlocks.alchemicalTubeCrystal, CRBlocks.chargingStand, CRBlocks.chemicalVent,
				CRBlocks.coolingCoilGlass, CRBlocks.coolingCoilCrystal, CRBlocks.flowLimiterGlass, CRBlocks.flowLimiterCrystal, CRBlocks.fluidInjectorGlass, CRBlocks.fluidInjectorCrystal, CRBlocks.glasswareHolder,
				CRBlocks.heatedTubeGlass, CRBlocks.heatedTubeCrystal, CRBlocks.reactionChamberGlass, CRBlocks.reactionChamberCrystal, CRBlocks.reagentFilterGlass, CRBlocks.reagentFilterCrystal,
				CRBlocks.reagentPumpGlass, CRBlocks.reagentPumpCrystal, CRBlocks.reagentTankGlass, CRBlocks.reagentTankCrystal, CRBlocks.redsAlchemicalTubeGlass, CRBlocks.redsAlchemicalTubeCrystal, CRBlocks.voltusGenerator);

		e.registerItem(Capabilities.EnergyStorage.ITEM, LeydenJar.ENERGY_STORAGE_PROVIDER, CRItems.leydenJar);
	}
}
