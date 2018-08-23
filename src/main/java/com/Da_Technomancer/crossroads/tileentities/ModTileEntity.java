package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.tileentities.alchemy.*;
import com.Da_Technomancer.crossroads.tileentities.fluid.*;
import com.Da_Technomancer.crossroads.tileentities.heat.*;
import com.Da_Technomancer.crossroads.tileentities.magic.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntity{

	public static void init(){
		register(HeatCableTileEntity.class, "heat_cable");
		register(SidedGearHolderTileEntity.class, "sextuple_gear");
		register(MasterAxisTileEntity.class, "master_axis");
		register(GrindstoneTileEntity.class, "grindstone");
		register(HeatingCrucibleTileEntity.class, "heating_crucible");
		register(FluidTubeTileEntity.class, "fluid_tube");
		register(SteamBoilerTileEntity.class, "steam_boiler");
		register(RotaryPumpTileEntity.class, "rotary_pump");
		register(SteamTurbineTileEntity.class, "steam_turbine");
		register(FluidVoidTileEntity.class, "fluid_void");
		register(HeatExchangerTileEntity.class, "heat_exchanger");
		register(FluidTankTileEntity.class, "fluid_tank");
		register(FuelHeaterTileEntity.class, "coal_heater");
		register(HeatingChamberTileEntity.class, "heating_chamber");
		register(SaltReactorTileEntity.class, "salt_reactor");
		register(FluidCoolingChamberTileEntity.class, "fluid_cooling_chamber");
		register(LargeGearSlaveTileEntity.class, "large_gear_slave");
		register(LargeGearMasterTileEntity.class, "large_gear_master");
		register(RadiatorTileEntity.class, "radiator");
		register(RotaryDrillTileEntity.class, "rotary_drill");
		register(FatCollectorTileEntity.class, "fat_collector");
		register(FatCongealerTileEntity.class, "fat_congealer");
		register(RedstoneHeatCableTileEntity.class, "redstone_heat_cable");
		register(ToggleGearTileEntity.class, "toggle_gear");
		register(RedstoneFluidTubeTileEntity.class, "redstone_fluid_tube");
		register(WaterCentrifugeTileEntity.class, "water_centrifuge");
		register(ArcaneExtractorTileEntity.class, "arcane_extractor");
		register(QuartzStabilizerTileEntity.class, "quartz_stabilizer");
		register(CrystallinePrismTileEntity.class, "crystal_prism");
		register(ArcaneReflectorTileEntity.class, "arcane_reflector");
		register(LensHolderTileEntity.class, "lens_holder");
		register(BeamSplitterTileEntity.class, "beam_splitter");
		register(BeamSplitterBasicTileEntity.class, "beam_splitter_basic");
		register(CrystalMasterAxisTileEntity.class, "crystal_master_axis");
		register(AxleTileEntity.class, "axle");
		register(RatiatorTileEntity.class, "ratiator");
		register(BeaconHarnessTileEntity.class, "beacon_harness");
		register(FatFeederTileEntity.class, "fat_feeder");
		register(ChunkUnlockerTileEntity.class, "chunk_unlocker");
		register(RateManipulatorTileEntity.class, "rate_uanipulator");
		register(FluxManipulatorTileEntity.class, "flux_manipulator");
		register(FluxReaderAxisTileEntity.class, "flux_reader_axis");
		register(MultiplicationAxisTileEntity.class, "multiplication_axis");
		register(AdditionAxisTileEntity.class, "addition_axis");
		register(EqualsAxisTileEntity.class, "equals_axis");
		register(GreaterThanAxisTileEntity.class, "greater_than_axis");
		register(LessThanAxisTileEntity.class, "less_than_axis");
		register(RedstoneAxisTileEntity.class, "redstone_axis");
		register(SquareRootAxisTileEntity.class, "square_root_axis");
		register(MechanicalBeamSplitterTileEntity.class, "mechanical_beam_splitter");
		register(CageChargerTileEntity.class, "cage_charger");
		register(HamsterWheelTileEntity.class, "hamster_wheel");
		register(FluidSplitterTileEntity.class, "fluid_splitter");
		register(BasicFluidSplitterTileEntity.class, "basic_fluid_splitter");
		register(CopshowiumCreationChamberTileEntity.class, "copshowium_creation_chamber");
		register(SinAxisTileEntity.class, "sin_axis");
		register(CosAxisTileEntity.class, "cos_axis");
		register(ArcSinAxisTileEntity.class, "arcsin_axis");
		register(ArcCosAxisTileEntity.class, "arccos_axis");
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
		//register(SamplingBenchTileEntity.class, "sampling_bench");
		register(DensusPlateTileEntity.class, "densus_plate");
		register(ChargingStandTileEntity.class, "charging_stand");
		register(AtmosChargerTileEntity.class, "atmos_charger");
		register(ReactiveSpotTileEntity.class, "reactive_spot");
		register(ClockworkStabilizerTileEntity.class, "clock_stab");
	}

	/**
	 * @param clazz The class of the TileEntity being registered. 
	 * @param ID Should be lower-case.
	 */
	private static void register(Class<? extends TileEntity> clazz, String ID){
		GameRegistry.registerTileEntity(clazz, Main.MODID + ':' + ID);
	}
}
