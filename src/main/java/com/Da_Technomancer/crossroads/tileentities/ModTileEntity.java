package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.Main;
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
import com.Da_Technomancer.crossroads.tileentities.heat.CoalHeaterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;
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
import com.Da_Technomancer.crossroads.tileentities.technomancy.BackCounterGearTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChunkUnlockerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CosAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CounterGearTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.EqualsAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxManipulatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxReaderAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GreaterThanAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.LessThanAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalBeamSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MultiplicationAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RateManipulatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneKeyboardTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SinAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SquareRootAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.StaffChargerTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntity{

	public static void init(){
		register(HeatCableTileEntity.class, "heatCable");
		register(SidedGearHolderTileEntity.class, "sidedGearHolder");
		register(MasterAxisTileEntity.class, "masterAxis");
		register(GrindstoneTileEntity.class, "grindstone");
		register(HeatingCrucibleTileEntity.class, "heatingCrucible");
		register(FluidTubeTileEntity.class, "fluidTube");
		register(SteamBoilerTileEntity.class, "steamBoiler");
		register(RotaryPumpTileEntity.class, "rotaryPump");
		register(BrazierTileEntity.class, "brazier");
		register(SteamTurbineTileEntity.class, "steamTurbine");
		register(FluidVoidTileEntity.class, "fluidVoid");
		register(HeatExchangerTileEntity.class, "heatExchanger");
		register(FluidTankTileEntity.class, "fluidTank");
		register(CoalHeaterTileEntity.class, "coalHeater");
		register(HeatingChamberTileEntity.class, "heatingChamber");
		register(SaltReactorTileEntity.class, "saltReactor");
		register(FluidCoolingChamberTileEntity.class, "fluidCoolingChamber");
		register(SlottedChestTileEntity.class, "slottedChest");
		register(SortingHopperTileEntity.class, "sortingHopper");
		register(LargeGearSlaveTileEntity.class, "largeGearSlave");
		register(LargeGearMasterTileEntity.class, "largeGearMaster");
		register(ItemChutePortTileEntity.class, "itemChutePort");
		register(RadiatorTileEntity.class, "radiator");
		register(RotaryDrillTileEntity.class, "rotaryDrill");
		register(FatCollectorTileEntity.class, "fatCollector");
		register(FatCongealerTileEntity.class, "fatCongealer");
		register(RedstoneHeatCableTileEntity.class, "redstoneHeatCable");
		register(ToggleGearTileEntity.class, "toggleGear");
		register(RedstoneFluidTubeTileEntity.class, "redstoneFluidTube");
		register(WaterCentrifugeTileEntity.class, "waterCentrifuge");
		register(ArcaneExtractorTileEntity.class, "arcaneExtractor");
		register(QuartzStabilizerTileEntity.class, "quartzStabilizer");
		register(CrystallinePrismTileEntity.class, "crystalPrism");
		register(ArcaneReflectorTileEntity.class, "arcaneReflector");
		register(LensHolderTileEntity.class, "lensHolder");
		register(BeamSplitterTileEntity.class, "beamSplitter");
		register(BeamSplitterBasicTileEntity.class, "beamSplitterBasic");
		register(CrystalMasterAxisTileEntity.class, "crystalMasterAxis");
		register(AxleTileEntity.class, "axle");
		register(RatiatorTileEntity.class, "ratiator");
		register(BeaconHarnessTileEntity.class, "beaconHarness");
		register(FatFeederTileEntity.class, "fatFeeder");
		register(ChunkUnlockerTileEntity.class, "chunkUnlocker");
		register(RateManipulatorTileEntity.class, "rateManipulator");
		register(FluxManipulatorTileEntity.class, "fluxManipulator");
		register(FluxReaderAxisTileEntity.class, "fluxReaderAxis");
		register(MultiplicationAxisTileEntity.class, "multiplicationAxis");
		register(CounterGearTileEntity.class, "counterGear");
		register(BackCounterGearTileEntity.class, "backCounterGear");
		register(AdditionAxisTileEntity.class, "additionAxis");
		register(EqualsAxisTileEntity.class, "equalsAxis");
		register(GreaterThanAxisTileEntity.class, "greaterThanAxis");
		register(LessThanAxisTileEntity.class, "lessThanAxis");
		register(RedstoneAxisTileEntity.class, "redstoneAxis");
		register(SquareRootAxisTileEntity.class, "squareRootAxis");
		register(MechanicalBeamSplitterTileEntity.class, "mechanicalBeamSplitter");
		register(StaffChargerTileEntity.class, "staffCharger");
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
	}

	/**
	 * 
	 * @param locat
	 * @param ID For all new TileEntities, this should be lowercase just in case they limit this to lowercase in a later update (future-proofing). Pre-existing TileEntities should keep their old ID.
	 */
	private static void register(Class<? extends TileEntity> locat, String ID){
		GameRegistry.registerTileEntity(locat, Main.MODID + "_" + ID);
	}

}
