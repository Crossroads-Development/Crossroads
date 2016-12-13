package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatCollectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatCongealerTileEntity;
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
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterBasicTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystallinePrismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.LensHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.QuartzStabilizerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.AxleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.CrystalMasterAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.GrindstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ItemChutePortTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;

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
	}

	private static void register(Class<? extends TileEntity> locat, String ID){
		GameRegistry.registerTileEntity(locat, Main.MODID + "_" + ID);
	}

}
