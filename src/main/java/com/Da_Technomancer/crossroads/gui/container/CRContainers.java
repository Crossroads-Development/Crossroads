package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.gui.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.IContainerFactory;

import java.util.HashMap;

public final class CRContainers{
	
	public static void initServer(){
		registerConType(FireboxContainer::new, "firebox");
		registerConType(IceboxContainer::new, "icebox");
		registerConType(FluidCoolerContainer::new, "fluid_cooler");
		registerConType(CrucibleContainer::new, "crucible");
		registerConType(SaltReactorContainer::new, "salt_reactor");
		registerConType(SmelterContainer::new, "smelter");
		registerConType(BlastFurnaceContainer::new, "ind_blast_furnace");
		registerConType(MillstoneContainer::new, "millstone");
		registerConType(StampMillContainer::new, "stamp_mill");
		registerConType(FatCollectorContainer::new, "fat_collector");
		registerConType(FatCongealerContainer::new, "fat_congealer");
		registerConType(FatFeederContainer::new, "fat_feeder");
		registerConType(FluidTankContainer::new, "fluid_tank");
		registerConType(OreCleanserContainer::new, "ore_cleanser");
		registerConType(RadiatorContainer::new, "radiator");
		registerConType(SteamBoilerContainer::new, "steam_boiler");
		registerConType(WaterCentrifugeContainer::new, "water_centrifuge");
		registerConType(ColorChartContainer::new, "color_chart");
		registerConType(BeamExtractorContainer::new, "beam_extractor");
		registerConType(HeatLimiterContainer::new, "heat_limiter");
		registerConType(RotaryPumpContainer::new, "rotary_pump");
		registerConType(DetailedCrafterContainer::new, "detailed_crafter");
		registerConType(ReagentFilterContainer::new, "reagent_filter");
		registerConType(CopshowiumMakerContainer::new, "copshowium_maker");
		registerConType(SteamerContainer::new, "steamer");
		registerConType(WindingTableContainer::new, "winding_table");
		registerConType(DetailedAutoCrafterContainer::new, "detailed_auto_crafter");
		registerConType(SequenceBoxContainer::new, "sequence_box");
		registerConType(SteamTurbineContainer::new, "steam_turbine");
		registerConType(BeaconHarnessContainer::new, "beacon_harness");
		registerConType(FormulationVatContainer::new, "formulation_vat");
		registerConType(BrewingVatContainer::new, "brewing_vat");
		registerConType(AutoInjectorContainer::new, "auto_injector");
		registerConType(ColdStorageContainer::new, "cold_storage");
		registerConType(HydroponicsTroughContainer::new, "hydroponics_trough");
		registerConType(StasisStorageContainer::new, "stasis_storage");
		registerConType(CultivatorVatContainer::new, "cultivator_vat");
		registerConType(IncubatorContainer::new, "incubator");
		registerConType(BloodCentrifugeContainer::new, "blood_centrifuge");
		registerConType(EmbryoLabContainer::new, "embryo_lab");
		registerConType(HeatReservoirCreativeContainer::new, "heat_reservoir_creative");
		registerConType(MasterAxisCreativeContainer::new, "master_axis_creative");
		registerConType(BeamExtractorCreativeContainer::new, "beam_extractor_creative");
	}

	@OnlyIn(Dist.CLIENT)
	public static void initClient(){
		registerConClient(FireboxContainer::new, FireboxScreen::new, "firebox");
		registerConClient(IceboxContainer::new, IceboxScreen::new, "icebox");
		registerConClient(FluidCoolerContainer::new, FluidCoolerScreen::new, "fluid_cooler");
		registerConClient(CrucibleContainer::new, CrucibleScreen::new, "crucible");
		registerConClient(SaltReactorContainer::new, SaltReactorScreen::new, "salt_reactor");
		registerConClient(SmelterContainer::new, SmelterScreen::new, "smelter");
		registerConClient(BlastFurnaceContainer::new, BlastFurnaceScreen::new, "ind_blast_furnace");
		registerConClient(MillstoneContainer::new, MillstoneScreen::new, "millstone");
		registerConClient(StampMillContainer::new, StampMillScreen::new, "stamp_mill");
		registerConClient(FatCollectorContainer::new, FatCollectorScreen::new, "fat_collector");
		registerConClient(FatCongealerContainer::new, FatCongealerScreen::new, "fat_congealer");
		registerConClient(FatFeederContainer::new, FatFeederScreen::new, "fat_feeder");
		registerConClient(FluidTankContainer::new, FluidTankScreen::new, "fluid_tank");
		registerConClient(OreCleanserContainer::new, OreCleanserScreen::new, "ore_cleanser");
		registerConClient(RadiatorContainer::new, RadiatorScreen::new, "radiator");
		registerConClient(SteamBoilerContainer::new, SteamBoilerScreen::new, "steam_boiler");
		registerConClient(WaterCentrifugeContainer::new, WaterCentrifugeScreen::new, "water_centrifuge");
		registerConClient(ColorChartContainer::new, ColorChartScreen::new, "color_chart");
		registerConClient(BeamExtractorContainer::new, BeamExtractorScreen::new, "beam_extractor");
		registerConClient(HeatLimiterContainer::new, HeatLimiterScreen::new, "heat_limiter");
		registerConClient(RotaryPumpContainer::new, RotaryPumpScreen::new, "rotary_pump");
		registerConClient(DetailedCrafterContainer::new, DetailedCrafterScreen::new, "detailed_crafter");
		registerConClient(ReagentFilterContainer::new, ReagentFilterScreen::new, "reagent_filter");
		registerConClient(CopshowiumMakerContainer::new, CopshowiumMakerScreen::new, "copshowium_maker");
		registerConClient(SteamerContainer::new, SteamerScreen::new, "steamer");
		registerConClient(WindingTableContainer::new, WindingTableScreen::new, "winding_table");
		registerConClient(DetailedAutoCrafterContainer::new, DetailedAutoCrafterScreen::new, "detailed_auto_crafter");
		registerConClient(SequenceBoxContainer::new, SequenceBoxScreen::new, "sequence_box");
		registerConClient(SteamTurbineContainer::new, SteamTurbineScreen::new, "steam_turbine");
		registerConClient(BeaconHarnessContainer::new, BeaconHarnessScreen::new, "beacon_harness");
		registerConClient(FormulationVatContainer::new, FormulationVatScreen::new, "formulation_vat");
		registerConClient(BrewingVatContainer::new, BrewingVatScreen::new, "brewing_vat");
		registerConClient(AutoInjectorContainer::new, AutoInjectorScreen::new, "auto_injector");
		registerConClient(ColdStorageContainer::new, ColdStorageScreen::new, "cold_storage");
		registerConClient(HydroponicsTroughContainer::new, HydroponicsTroughScreen::new, "hydroponics_trough");
		registerConClient(StasisStorageContainer::new, StasisStorageScreen::new, "stasis_storage");
		registerConClient(CultivatorVatContainer::new, CultivatorVatScreen::new, "cultivator_vat");
		registerConClient(IncubatorContainer::new, IncubatorScreen::new, "incubator");
		registerConClient(BloodCentrifugeContainer::new, BloodCentrifugeScreen::new, "blood_centrifuge");
		registerConClient(EmbryoLabContainer::new, EmbryoLabScreen::new, "embryo_lab");
		registerConClient(HeatReservoirCreativeContainer::new, HeatReservoirCreativeScreen::new, "heat_reservoir_creative");
		registerConClient(MasterAxisCreativeContainer::new, MasterAxisCreativeScreen::new, "master_axis_creative");
		registerConClient(BeamExtractorCreativeContainer::new, BeamExtractorCreativeScreen::new, "beam_extractor_creative");
	}

	/**
	 * Creates a container type and queues it for register
	 * @param cons Container factory
	 * @param id The ID to use
	 * @param <T> Container subclass
	 * @return The newly created type
	 */
	private static <T extends AbstractContainerMenu> MenuType<T> registerConType(IContainerFactory<T> cons, String id){
		MenuType<T> contType = new MenuType<>(cons);
		toRegisterMenu.put(id, contType);
		return contType;
	}

	/**
	 * Creates both a container type and a screen factory; queues for register. Not usable on the physical server due to screen factory.
	 * @param cons Container factory
	 * @param screenFactory The screen factory to be linked to the type
	 * @param id The ID to use
	 * @param <T> Container subclass
	 */
	@OnlyIn(Dist.CLIENT)
	private static <T extends AbstractContainerMenu> void registerConClient(IContainerFactory<T> cons, MenuScreens.ScreenConstructor<T, AbstractContainerScreen<T>> screenFactory, String id){
		MenuType<T> contType = registerConType(cons, id);
		MenuScreens.register(contType, screenFactory);
	}
	
	public static final HashMap<String, MenuType<?>> toRegisterMenu = new HashMap<>();
}
