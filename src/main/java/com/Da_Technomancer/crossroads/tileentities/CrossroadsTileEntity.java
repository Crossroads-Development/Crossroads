package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.tileentities.alchemy.*;
import com.Da_Technomancer.crossroads.tileentities.beams.*;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTileEntity;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.*;
import com.Da_Technomancer.crossroads.tileentities.heat.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import com.mojang.datafixers.DSL;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.function.Supplier;

import static com.Da_Technomancer.crossroads.blocks.CRBlocks.*;

public class CrossroadsTileEntity{

	public static void init(IForgeRegistry<TileEntityType<?>> reg){
		register(HeatCableTileEntity::new, "heat_cable", reg, HeatCableFactory.HEAT_CABLES.values());
		register(MechanismTileEntity::new, "mechanism", reg, mechanism);
		register(MasterAxisTileEntity::new, "master_axis", reg, masterAxis);
		register(MillstoneTileEntity::new, "millstone", reg, millstone);
		register(HeatingCrucibleTileEntity::new, "crucible", reg, heatingCrucible);
		register(FluidTubeTileEntity::new, "fluid_tube", reg, fluidTube);
		register(SteamBoilerTileEntity::new, "steam_boiler", reg, steamBoiler);
		register(RotaryPumpTileEntity::new, "rotary_pump", reg, rotaryPump);
		register(SteamTurbineTileEntity::new, "steam_turbine", reg, steamTurbine);
		register(FluidVoidTileEntity::new, "fluid_void", reg, fluidVoid);
		register(HeatSinkTileEntity::new, "heat_sink", reg, heatSink);
		register(FluidTankTileEntity::new, "fluid_tank", reg, fluidTank);
		register(FireboxTileEntity::new, "firebox", reg, firebox);
		register(SmelterTileEntity::new, "smelter", reg, smelter);
		register(SaltReactorTileEntity::new, "salt_reactor", reg, saltReactor);
		register(FluidCoolingChamberTileEntity::new, "fluid_cooling_chamber", reg, fluidCoolingChamber);
		register(LargeGearSlaveTileEntity::new, "large_gear_slave", reg, largeGearSlave);
		register(LargeGearMasterTileEntity::new, "large_gear_master", reg, largeGearMaster);
		register(RadiatorTileEntity::new, "radiator", reg, radiator);
		register(RotaryDrillTileEntity::new, "rotary_drill", reg, rotaryDrill, rotaryDrillGold);
		register(FatCollectorTileEntity::new, "fat_collector", reg, fatCollector);
		register(FatCongealerTileEntity::new, "fat_congealer", reg, fatCongealer);
		register(RedstoneHeatCableTileEntity::new, "redstone_heat_cable", reg, HeatCableFactory.REDSTONE_HEAT_CABLES.values());
		register(RedstoneFluidTubeTileEntity::new, "redstone_fluid_tube", reg, redstoneFluidTube);
		register(WaterCentrifugeTileEntity::new, "water_centrifuge", reg, waterCentrifuge);
		register(BeamExtractorTileEntity::new, "beam_extractor", reg, beamExtractor);
		register(QuartzStabilizerTileEntity::new, "quartz_stabilizer", reg, quartzStabilizer);
		register(CrystallinePrismTileEntity::new, "crystal_prism", reg, crystallinePrism);
		register(BeamReflectorTileEntity::new, "beam_reflector", reg, beamReflector);
		register(LensFrameTileEntity::new, "lens_frame", reg, lensFrame);
		register(BeamSiphonTileEntity::new, "beam_siphon", reg, beamSiphon);
		register(BeamSplitterTileEntity::new, "beam_splitter", reg, beamSplitter);
		register(CrystalMasterAxisTileEntity::new, "crystal_master_axis", reg, crystalMasterAxis);
//		register(RatiatorTileEntity::new, "ratiator", reg, raitator);
		register(BeaconHarnessTileEntity::new, "beacon_harness", reg, beaconHarness);
		register(FatFeederTileEntity::new, "fat_feeder", reg, fatFeeder);
		register(RedstoneAxisTileEntity::new, "redstone_axis", reg, redstoneAxis);
//		register(MathAxisTileEntity::new, "math_axis", reg, mathAxis);
		register(CageChargerTileEntity::new, "cage_charger", reg, cageCharger);
		register(HamsterWheelTileEntity::new, "hamster_wheel", reg, hamsterWheel);
		register(CopshowiumCreationChamberTileEntity::new, "copshowium_creation_chamber", reg, copshowiumCreationChamber);
		register(GatewayFrameTileEntity::new, "gateway_frame", reg, gatewayFrame);
//		register(RedstoneKeyboardTileEntity::new, "redstone_keyboard", reg, redstoneKeyboard);
//		register(PrototypingTableTileEntity::new, "prototyping_table", reg, prototypingTable);
//		register(PrototypeTileEntity::new, "prototype", reg, prototype);
//		register(PrototypePortTileEntity::new, "prototype_port", reg, prototypePort);
		register(MechanicalArmTileEntity::new, "mechanical_arm", reg, mechanicalArm);
		register(RedstoneRegistryTileEntity::new, "redstone_registry", reg, redstoneRegistry);
		register(AlchemicalTubeTileEntity::new, "alchemical_tube", reg, alchemicalTubeCrystal, alchemicalTubeGlass);
		register(FluidInjectorTileEntity::new, "fluid_injector", reg, fluidInjectorGlass, fluidInjectorCrystal);
		register(FlowLimiterTileEntity::new, "flow_limiter", reg, flowLimiterCrystal, flowLimiterGlass);
		register(HeatedTubeTileEntity::new, "heated_tube", reg, heatedTubeCrystal, heatedTubeGlass);
		register(CoolingCoilTileEntity::new, "cooling_coil", reg, coolingCoilCrystal, coolingCoilGlass);
		register(ChemicalVentTileEntity::new, "chemical_vent", reg, chemicalVent);
		register(ReactionChamberTileEntity::new, "reaction_chamber", reg, reactionChamberCrystal, reactionChamberGlass);
		register(HeatLimiterBasicTileEntity::new, "heat_limiter_basic", reg, heatLimiterBasic);
		register(HeatLimiterRedstoneTileEntity::new, "heat_limiter", reg, heatLimiterRedstone);
		register(DynamoTileEntity::new, "dynamo", reg, dynamo);
		register(TeslaCoilTileEntity::new, "tesla_coil", reg, teslaCoil);
		register(ReagentTankTileEntity::new, "reagent_tank", reg, reagentTankCrystal, reagentTankGlass);
		register(ReagentPumpTileEntity::new, "reagent_pump", reg, reagentPumpGlass, reagentPumpCrystal);
		register(MaxwellDemonTileEntity::new, "maxwell_demon", reg, maxwellDemon);
		register(GlasswareHolderTileEntity::new, "glassware_holder", reg, glasswareHolder);
		register(RedsAlchemicalTubeTileEntity::new, "reds_alchemical_tube", reg, redsAlchemicalTubeCrystal, redsAlchemicalTubeGlass);
		register(AlembicTileEntity::new, "alembic", reg, alembic);
		register(DensusPlateTileEntity::new, "densus_plate", reg, densusPlate, antiDensusPlate);
		register(ChargingStandTileEntity::new, "charging_stand", reg, chargingStand);
		register(AtmosChargerTileEntity::new, "atmos_charger", reg, atmosCharger);
		register(VoltusGeneratorTileEntity::new, "voltus_generator", reg, voltusGenerator);
		register(ReactiveSpotTileEntity::new, "reactive_spot", reg, reactiveSpot);
		register(ClockworkStabilizerTileEntity::new, "clock_stab", reg, clockworkStabilizer);
		register(WindTurbineTileEntity::new, "wind_turbine", reg, windTurbine);
		register(SolarHeaterTileEntity::new, "solar_heater", reg, solarHeater);
		register(HeatReservoirTileEntity::new, "heat_reservoir", reg, heatReservoir);
		register(StirlingEngineTileEntity::new, "stirling_engine", reg, stirlingEngine);
		register(StampMillTileEntity::new, "stamp_mill", reg, stampMill);
		register(IceboxTileEntity::new, "icebox", reg, icebox);
		register(OreCleanserTileEntity::new, "ore_cleanser", reg, oreCleanser);
		register(BlastFurnaceTileEntity::new, "ind_blast_furnace", reg, blastFurnace);
		register(BeamRedirectorTileEntity::new, "beam_redirector", reg, beamRedirector);
//		register(RedstoneTransmitterTileEntity::new, "redstone_transmitter", reg, redstoneTransmitter);
//		register(RedstoneReceiverTileEntity::new, "redstone_receiver", reg, redstoneReceiver);
		register(TeslaCoilTopTileEntity::new, "tesla_coil_top", reg, teslaCoilTopAttack, teslaCoilTopDecorative, teslaCoilTopDistance, teslaCoilTopEfficiency, teslaCoilTopIntensity, teslaCoilTopNormal);
		register(FluxNodeTileEntity::new, "flux_node", reg, fluxNode);
//		register(AbstractStabilizerTileEntity::new, "flux_void", reg, fluxStabilizerBeam, fluxStabilizerCrystalBeam, fluxStabilizerCrystalElectric, fluxStabilizerElectric);
		register(TemporalAcceleratorTileEntity::new, "temporal_accelerator", reg, temporalAccelerator);
		register(ChronoHarnessTileEntity::new, "chrono_harness", reg, chronoHarness);
		register(ReagentFilterTileEntity::new, "reagent_filter", reg, reagentFilterCrystal, reagentFilterGlass);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Block> void register(Supplier<? extends TileEntity> cons, String id, IForgeRegistry<TileEntityType<?>> reg, Collection<T> blocks){
		register(cons, id, reg, (T[]) blocks.toArray());
	}
	
	private static void register(Supplier<? extends TileEntity> cons, String id, IForgeRegistry<TileEntityType<?>> reg, Block... blocks){
		TileEntityType<? extends TileEntity> teType = TileEntityType.Builder.create(cons, blocks).build(DSL.nilType());
		teType.setRegistryName(new ResourceLocation(Crossroads.MODID, id));
		reg.register(teType);
	}
}
