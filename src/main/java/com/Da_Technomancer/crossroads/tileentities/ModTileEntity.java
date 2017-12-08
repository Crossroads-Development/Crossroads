package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AlchemicalTubeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ChemicalVentTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.CoolingCoilTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DynamoTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FlowLimiterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FluidInjectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatLimiterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatedTubeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactionChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.SifterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.BasicFluidSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatCollectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatCongealerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatFeederTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTankTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTubeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidVoidTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RadiatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RedstoneFluidTubeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamBoilerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.FuelHeaterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatExchangerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.RedstoneHeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.SaltReactorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneExtractorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneReflectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeaconHarnessTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterBasicTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystalMasterAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystallinePrismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.LensHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.QuartzStabilizerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.AxleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.GrindstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ItemChutePortTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.AdditionAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ArcCosAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ArcSinAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CageChargerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChunkUnlockerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CosAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.EqualsAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxManipulatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxReaderAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GreaterThanAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.LessThanAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalBeamSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MultiplicationAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RateManipulatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SinAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SquareRootAxisTileEntity;

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
		register(SifterTileEntity.class, "sifter");
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
