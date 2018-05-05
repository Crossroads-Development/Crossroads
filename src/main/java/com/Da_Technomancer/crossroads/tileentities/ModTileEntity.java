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
		registerOld(HeatCableTileEntity.class, "heatCable");
		registerOld(SidedGearHolderTileEntity.class, "sidedGearHolder");
		registerOld(MasterAxisTileEntity.class, "masterAxis");
		registerOld(GrindstoneTileEntity.class, "grindstone");
		registerOld(HeatingCrucibleTileEntity.class, "heatingCrucible");
		registerOld(FluidTubeTileEntity.class, "fluidTube");
		registerOld(SteamBoilerTileEntity.class, "steamBoiler");
		registerOld(RotaryPumpTileEntity.class, "rotaryPump");
		registerOld(BrazierTileEntity.class, "brazier");
		registerOld(SteamTurbineTileEntity.class, "steamTurbine");
		registerOld(FluidVoidTileEntity.class, "fluidVoid");
		registerOld(HeatExchangerTileEntity.class, "heatExchanger");
		registerOld(FluidTankTileEntity.class, "fluidTank");
		registerOld(FuelHeaterTileEntity.class, "coalHeater");
		registerOld(HeatingChamberTileEntity.class, "heatingChamber");
		registerOld(SaltReactorTileEntity.class, "saltReactor");
		registerOld(FluidCoolingChamberTileEntity.class, "fluidCoolingChamber");
		registerOld(SlottedChestTileEntity.class, "slottedChest");
		registerOld(SortingHopperTileEntity.class, "sortingHopper");
		registerOld(LargeGearSlaveTileEntity.class, "largeGearSlave");
		registerOld(LargeGearMasterTileEntity.class, "largeGearMaster");
		registerOld(ItemChutePortTileEntity.class, "itemChutePort");
		registerOld(RadiatorTileEntity.class, "radiator");
		registerOld(RotaryDrillTileEntity.class, "rotaryDrill");
		registerOld(FatCollectorTileEntity.class, "fatCollector");
		registerOld(FatCongealerTileEntity.class, "fatCongealer");
		registerOld(RedstoneHeatCableTileEntity.class, "redstoneHeatCable");
		registerOld(ToggleGearTileEntity.class, "toggleGear");
		registerOld(RedstoneFluidTubeTileEntity.class, "redstoneFluidTube");
		registerOld(WaterCentrifugeTileEntity.class, "waterCentrifuge");
		registerOld(ArcaneExtractorTileEntity.class, "arcaneExtractor");
		registerOld(QuartzStabilizerTileEntity.class, "quartzStabilizer");
		registerOld(CrystallinePrismTileEntity.class, "crystalPrism");
		registerOld(ArcaneReflectorTileEntity.class, "arcaneReflector");
		registerOld(LensHolderTileEntity.class, "lensHolder");
		registerOld(BeamSplitterTileEntity.class, "beamSplitter");
		registerOld(BeamSplitterBasicTileEntity.class, "beamSplitterBasic");
		registerOld(CrystalMasterAxisTileEntity.class, "crystalMasterAxis");
		registerOld(AxleTileEntity.class, "axle");
		registerOld(RatiatorTileEntity.class, "ratiator");
		registerOld(BeaconHarnessTileEntity.class, "beaconHarness");
		registerOld(FatFeederTileEntity.class, "fatFeeder");
		registerOld(ChunkUnlockerTileEntity.class, "chunkUnlocker");
		registerOld(RateManipulatorTileEntity.class, "rateManipulator");
		registerOld(FluxManipulatorTileEntity.class, "fluxManipulator");
		registerOld(FluxReaderAxisTileEntity.class, "fluxReaderAxis");
		registerOld(MultiplicationAxisTileEntity.class, "multiplicationAxis");
		registerOld(AdditionAxisTileEntity.class, "additionAxis");
		registerOld(EqualsAxisTileEntity.class, "equalsAxis");
		registerOld(GreaterThanAxisTileEntity.class, "greaterThanAxis");
		registerOld(LessThanAxisTileEntity.class, "lessThanAxis");
		registerOld(RedstoneAxisTileEntity.class, "redstoneAxis");
		registerOld(SquareRootAxisTileEntity.class, "squareRootAxis");
		registerOld(MechanicalBeamSplitterTileEntity.class, "mechanicalBeamSplitter");
		registerOld(CageChargerTileEntity.class, "cage_charger");
		registerOld(HamsterWheelTileEntity.class, "hamster_wheel");
		registerOld(FluidSplitterTileEntity.class, "fluid_splitter");
		registerOld(BasicFluidSplitterTileEntity.class, "basic_fluid_splitter");
		registerOld(CopshowiumCreationChamberTileEntity.class, "copshowium_creation_chamber");
		registerOld(SinAxisTileEntity.class, "sin_axis");
		registerOld(CosAxisTileEntity.class, "cos_axis");
		registerOld(ArcSinAxisTileEntity.class, "arcsin_axis");
		registerOld(ArcCosAxisTileEntity.class, "arccos_axis");
		registerOld(GatewayFrameTileEntity.class, "gateway_frame");
		registerOld(RedstoneKeyboardTileEntity.class, "redstone_keyboard");
		registerOld(PrototypingTableTileEntity.class, "prototyping_table");
		registerOld(PrototypeTileEntity.class, "prototype");
		registerOld(PrototypePortTileEntity.class, "prototype_port");
		register(MechanicalArmTileEntity.class, "mechanical_arm");
		register(RedstoneRegistryTileEntity.class, "redstone_registry");
		register(AlchemicalTubeTileEntity.class, "alchemical_tube");
		register(FluidInjectorTileEntity.class, "fluid_injector");
		register(FlowLimiterTileEntity.class, "flow_limiter");
		register(HeatedTubeTileEntity.class, "heated_tube");
		register(CoolingCoilTileEntity.class, "cooling_coil");
		register(ChemicalVentTileEntity.class, "chemical_vent");
		register(PortExtenderTileEntity.class, "port_extender");
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
		register(SamplingBenchTileEntity.class, "sampling_bench");
		register(DensusPlateTileEntity.class, "densus_plate");
		register(ChargingStandTileEntity.class, "charging_stand");
		register(AtmosChargerTileEntity.class, "atmos_charger");
		register(ReactiveSpotTileEntity.class, "reactive_spot");
	}

	/**
	 * @deprecated A single character was wrong. Changing it will destroy all Crossroads stuff in existing worlds. All new tile entities should use the other method. 
	 */
	@Deprecated
	private static void registerOld(Class<? extends TileEntity> locat, String ID){
		GameRegistry.registerTileEntity(locat, Main.MODID + '_' + ID);
	}

	/**
	 * @param clazz The class of the TileEntity being registered. 
	 * @param ID Should be lower-case.
	 */
	private static void register(Class<? extends TileEntity> clazz, String ID){
		GameRegistry.registerTileEntity(clazz, Main.MODID + ':' + ID);
	}
}
