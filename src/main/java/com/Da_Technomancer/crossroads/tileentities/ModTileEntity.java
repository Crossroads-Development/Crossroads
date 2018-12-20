package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.tileentities.alchemy.*;
import com.Da_Technomancer.crossroads.tileentities.beams.*;
import com.Da_Technomancer.crossroads.tileentities.fluid.*;
import com.Da_Technomancer.crossroads.tileentities.heat.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntity{

	public static void init(){
		register(HeatCableTileEntity.class, "heat_cable");
		register(MechanismTileEntity.class, "mechanism");
		register(MasterAxisTileEntity.class, "master_axis");
		register(MillstoneTileEntity.class, "millstone");
		register(HeatingCrucibleTileEntity.class, "crucible");
		register(FluidTubeTileEntity.class, "fluid_tube");
		register(SteamBoilerTileEntity.class, "steam_boiler");
		register(RotaryPumpTileEntity.class, "rotary_pump");
		register(SteamTurbineTileEntity.class, "steam_turbine");
		register(FluidVoidTileEntity.class, "fluid_void");
		register(HeatSinkTileEntity.class, "heat_sink");
		register(FluidTankTileEntity.class, "fluid_tank");
		register(FireboxTileEntity.class, "firebox");
		register(SmelterTileEntity.class, "smelter");
		register(SaltReactorTileEntity.class, "salt_reactor");
		register(FluidCoolingChamberTileEntity.class, "fluid_cooling_chamber");
		register(LargeGearSlaveTileEntity.class, "large_gear_slave");
		register(LargeGearMasterTileEntity.class, "large_gear_master");
		register(RadiatorTileEntity.class, "radiator");
		register(RotaryDrillTileEntity.class, "rotary_drill");
		register(FatCollectorTileEntity.class, "fat_collector");
		register(FatCongealerTileEntity.class, "fat_congealer");
		register(RedstoneHeatCableTileEntity.class, "redstone_heat_cable");
		register(RedstoneFluidTubeTileEntity.class, "redstone_fluid_tube");
		register(WaterCentrifugeTileEntity.class, "water_centrifuge");
		register(BeamExtractorTileEntity.class, "beam_extractor");
		register(QuartzStabilizerTileEntity.class, "quartz_stabilizer");
		register(CrystallinePrismTileEntity.class, "crystal_prism");
		register(BeamReflectorTileEntity.class, "beam_reflector");
		register(LensFrameTileEntity.class, "lens_frame");
		register(BeamSiphonTileEntity.class, "beam_siphon");
		register(CrystalMasterAxisTileEntity.class, "crystal_master_axis");
		register(RatiatorTileEntity.class, "ratiator");
		register(BeaconHarnessTileEntity.class, "beacon_harness");
		register(FatFeederTileEntity.class, "fat_feeder");
		register(RedstoneAxisTileEntity.class, "redstone_axis");
		register(MathAxisTileEntity.class, "math_axis");
		register(CageChargerTileEntity.class, "cage_charger");
		register(HamsterWheelTileEntity.class, "hamster_wheel");
		register(FluidSplitterTileEntity.class, "fluid_splitter");
		register(BasicFluidSplitterTileEntity.class, "basic_fluid_splitter");
		register(CopshowiumCreationChamberTileEntity.class, "copshowium_creation_chamber");
		register(GatewayFrameTileEntity.class, "gateway_frame");
		register(RedstoneKeyboardTileEntity.class, "redstone_keyboard");
		register(PrototypingTableTileEntity.class, "prototyping_table");
		register(PrototypeTileEntity.class, "prototype");
		register(PrototypePortTileEntity.class, "prototype_port");
		register(MechanicalArmTileEntity.class, "mechanical_arm");
		register(RedstoneRegistryTileEntity.class, "redstone_registry");
		register(AlchemicalTubeTileEntity.class, "alchemical_tube");
		register(FluidInjectorTileEntity.class, "fluid_injector");
		register(FlowLimiterTileEntity.class, "flow_limiter");
		register(HeatedTubeTileEntity.class, "heated_tube");
		register(CoolingCoilTileEntity.class, "cooling_coil");
		register(ChemicalVentTileEntity.class, "chemical_vent");
		register(ReactionChamberTileEntity.class, "reaction_chamber");
		register(HeatLimiterTileEntity.class, "heat_limiter");
		register(DynamoTileEntity.class, "dynamo");
		register(TeslaCoilTileEntity.class, "tesla_coil");
		register(ReagentTankTileEntity.class, "reagent_tank");
		register(ReagentPumpTileEntity.class, "reagent_pump");
		register(MaxwellDemonTileEntity.class, "maxwell_demon");
		register(GlasswareHolderTileEntity.class, "glassware_holder");
		register(RedsAlchemicalTubeTileEntity.class, "reds_alchemical_tube");
		register(AlembicTileEntity.class, "alembic");
		register(DensusPlateTileEntity.class, "densus_plate");
		register(ChargingStandTileEntity.class, "charging_stand");
		register(AtmosChargerTileEntity.class, "atmos_charger");
		register(ReactiveSpotTileEntity.class, "reactive_spot");
		register(ClockworkStabilizerTileEntity.class, "clock_stab");
		register(WindTurbineTileEntity.class, "wind_turbine");
		register(SolarHeaterTileEntity.class, "solar_heater");
		register(HeatReservoirTileEntity.class, "heat_reservoir");
		register(StirlingEngineTileEntity.class, "stirling_engine");
		register(StampMillTileEntity.class, "stamp_mill");
		register(IceboxTileEntity.class, "icebox");
		register(OreCleanserTileEntity.class, "ore_cleanser");
		register(BlastFurnaceTileEntity.class, "blast_furnace");
		register(BeamRedirectorTileEntity.class, "beam_redirector");
		register(RedstoneTransmitterTileEntity.class, "redstone_transmitter");
		register(RedstoneReceiverTileEntity.class, "redstone_receiver");
		register(TeslaCoilTopTileEntity.class, "tesla_coil_top");
		register(FluxNodeTileEntity.class, "flux_node");
		register(FluxConsumerTileEntity.class, "flux_void");
		register(TemporalAcceleratorTileEntity.class, "temporal_accelerator");
		register(ChronoHarnessTileEntity.class, "chrono_harness");
	}

	/**
	 * @param clazz The class of the TileEntity being registered. 
	 * @param ID Must be lower-case.
	 */
	private static void register(Class<? extends TileEntity> clazz, String ID){
		GameRegistry.registerTileEntity(clazz, new ResourceLocation(Main.MODID, ID));
	}
}
