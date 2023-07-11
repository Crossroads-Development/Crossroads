package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.gui.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.IContainerFactory;

import java.util.HashMap;

public final class CRContainers{
	
	public static void initServer(){
		registerContainerType(FireboxContainer.TYPE, "firebox");
		registerContainerType(IceboxContainer.TYPE, "icebox");
		registerContainerType(FluidCoolerContainer.TYPE, "fluid_cooler");
		registerContainerType(CrucibleContainer.TYPE, "crucible");
		registerContainerType(SaltReactorContainer.TYPE, "salt_reactor");
		registerContainerType(SmelterContainer.TYPE, "smelter");
		registerContainerType(BlastFurnaceContainer.TYPE, "ind_blast_furnace");
		registerContainerType(MillstoneContainer.TYPE, "millstone");
		registerContainerType(StampMillContainer.TYPE, "stamp_mill");
		registerContainerType(FatCollectorContainer.TYPE, "fat_collector");
		registerContainerType(FatCongealerContainer.TYPE, "fat_congealer");
		registerContainerType(FatFeederContainer.TYPE, "fat_feeder");
		registerContainerType(FluidTankContainer.TYPE, "fluid_tank");
		registerContainerType(OreCleanserContainer.TYPE, "ore_cleanser");
		registerContainerType(RadiatorContainer.TYPE, "radiator");
		registerContainerType(SteamBoilerContainer.TYPE, "steam_boiler");
		registerContainerType(WaterCentrifugeContainer.TYPE, "water_centrifuge");
		registerContainerType(ColorChartContainer.TYPE, "color_chart");
		registerContainerType(BeamExtractorContainer.TYPE, "beam_extractor");
		registerContainerType(HeatLimiterContainer.TYPE, "heat_limiter");
		registerContainerType(RotaryPumpContainer.TYPE, "rotary_pump");
		registerContainerType(DetailedCrafterContainer.TYPE, "detailed_crafter");
		registerContainerType(ReagentFilterContainer.TYPE, "reagent_filter");
		registerContainerType(CopshowiumMakerContainer.TYPE, "copshowium_maker");
		registerContainerType(SteamerContainer.TYPE, "steamer");
		registerContainerType(WindingTableContainer.TYPE, "winding_table");
		registerContainerType(DetailedAutoCrafterContainer.TYPE, "detailed_auto_crafter");
		registerContainerType(SequenceBoxContainer.TYPE, "sequence_box");
		registerContainerType(SteamTurbineContainer.TYPE, "steam_turbine");
		registerContainerType(BeaconHarnessContainer.TYPE, "beacon_harness");
		registerContainerType(FormulationVatContainer.TYPE, "formulation_vat");
		registerContainerType(BrewingVatContainer.TYPE, "brewing_vat");
		registerContainerType(AutoInjectorContainer.TYPE, "auto_injector");
		registerContainerType(ColdStorageContainer.TYPE, "cold_storage");
		registerContainerType(HydroponicsTroughContainer.TYPE, "hydroponics_trough");
		registerContainerType(StasisStorageContainer.TYPE, "stasis_storage");
		registerContainerType(CultivatorVatContainer.TYPE, "cultivator_vat");
		registerContainerType(IncubatorContainer.TYPE, "incubator");
		registerContainerType(BloodCentrifugeContainer.TYPE, "blood_centrifuge");
		registerContainerType(EmbryoLabContainer.TYPE, "embryo_lab");
		registerContainerType(HeatReservoirCreativeContainer.TYPE, "heat_reservoir_creative");
		registerContainerType(MasterAxisCreativeContainer.TYPE, "master_axis_creative");
		registerContainerType(BeamExtractorCreativeContainer.TYPE, "beam_extractor_creative");
		registerContainerType(BloodBeamLinkerContainer.TYPE, "blood_beam_linker");
	}

	@OnlyIn(Dist.CLIENT)
	public static void initClient(){
		registerConClient(FireboxContainer.TYPE, FireboxScreen::new, "firebox");
		registerConClient(IceboxContainer.TYPE, IceboxScreen::new, "icebox");
		registerConClient(FluidCoolerContainer.TYPE, FluidCoolerScreen::new, "fluid_cooler");
		registerConClient(CrucibleContainer.TYPE, CrucibleScreen::new, "crucible");
		registerConClient(SaltReactorContainer.TYPE, SaltReactorScreen::new, "salt_reactor");
		registerConClient(SmelterContainer.TYPE, SmelterScreen::new, "smelter");
		registerConClient(BlastFurnaceContainer.TYPE, BlastFurnaceScreen::new, "ind_blast_furnace");
		registerConClient(MillstoneContainer.TYPE, MillstoneScreen::new, "millstone");
		registerConClient(StampMillContainer.TYPE, StampMillScreen::new, "stamp_mill");
		registerConClient(FatCollectorContainer.TYPE, FatCollectorScreen::new, "fat_collector");
		registerConClient(FatCongealerContainer.TYPE, FatCongealerScreen::new, "fat_congealer");
		registerConClient(FatFeederContainer.TYPE, FatFeederScreen::new, "fat_feeder");
		registerConClient(FluidTankContainer.TYPE, FluidTankScreen::new, "fluid_tank");
		registerConClient(OreCleanserContainer.TYPE, OreCleanserScreen::new, "ore_cleanser");
		registerConClient(RadiatorContainer.TYPE, RadiatorScreen::new, "radiator");
		registerConClient(SteamBoilerContainer.TYPE, SteamBoilerScreen::new, "steam_boiler");
		registerConClient(WaterCentrifugeContainer.TYPE, WaterCentrifugeScreen::new, "water_centrifuge");
		registerConClient(ColorChartContainer.TYPE, ColorChartScreen::new, "color_chart");
		registerConClient(BeamExtractorContainer.TYPE, BeamExtractorScreen::new, "beam_extractor");
		registerConClient(HeatLimiterContainer.TYPE, HeatLimiterScreen::new, "heat_limiter");
		registerConClient(RotaryPumpContainer.TYPE, RotaryPumpScreen::new, "rotary_pump");
		registerConClient(DetailedCrafterContainer.TYPE, DetailedCrafterScreen::new, "detailed_crafter");
		registerConClient(ReagentFilterContainer.TYPE, ReagentFilterScreen::new, "reagent_filter");
		registerConClient(CopshowiumMakerContainer.TYPE, CopshowiumMakerScreen::new, "copshowium_maker");
		registerConClient(SteamerContainer.TYPE, SteamerScreen::new, "steamer");
		registerConClient(WindingTableContainer.TYPE, WindingTableScreen::new, "winding_table");
		registerConClient(DetailedAutoCrafterContainer.TYPE, DetailedAutoCrafterScreen::new, "detailed_auto_crafter");
		registerConClient(SequenceBoxContainer.TYPE, SequenceBoxScreen::new, "sequence_box");
		registerConClient(SteamTurbineContainer.TYPE, SteamTurbineScreen::new, "steam_turbine");
		registerConClient(BeaconHarnessContainer.TYPE, BeaconHarnessScreen::new, "beacon_harness");
		registerConClient(FormulationVatContainer.TYPE, FormulationVatScreen::new, "formulation_vat");
		registerConClient(BrewingVatContainer.TYPE, BrewingVatScreen::new, "brewing_vat");
		registerConClient(AutoInjectorContainer.TYPE, AutoInjectorScreen::new, "auto_injector");
		registerConClient(ColdStorageContainer.TYPE, ColdStorageScreen::new, "cold_storage");
		registerConClient(HydroponicsTroughContainer.TYPE, HydroponicsTroughScreen::new, "hydroponics_trough");
		registerConClient(StasisStorageContainer.TYPE, StasisStorageScreen::new, "stasis_storage");
		registerConClient(CultivatorVatContainer.TYPE, CultivatorVatScreen::new, "cultivator_vat");
		registerConClient(IncubatorContainer.TYPE, IncubatorScreen::new, "incubator");
		registerConClient(BloodCentrifugeContainer.TYPE, BloodCentrifugeScreen::new, "blood_centrifuge");
		registerConClient(EmbryoLabContainer.TYPE, EmbryoLabScreen::new, "embryo_lab");
		registerConClient(HeatReservoirCreativeContainer.TYPE, HeatReservoirCreativeScreen::new, "heat_reservoir_creative");
		registerConClient(MasterAxisCreativeContainer.TYPE, MasterAxisCreativeScreen::new, "master_axis_creative");
		registerConClient(BeamExtractorCreativeContainer.TYPE, BeamExtractorCreativeScreen::new, "beam_extractor_creative");
		registerConClient(BloodBeamLinkerContainer.TYPE, BloodBeamLinkerScreen::new, "blood_beam_linker");
	}

	public static <T extends AbstractContainerMenu> MenuType<T> createConType(IContainerFactory<T> cons){
		return new MenuType<>(cons, FeatureFlags.VANILLA_SET);
	}

	/**
	 * Queues a container type for register
	 * @param id The ID to use
	 * @param contType The container type to register   
	 * @param <T> Container subclass
	 */
	private static <T extends AbstractContainerMenu> void registerContainerType(MenuType<T> contType, String id){
		toRegisterMenu.put(id, contType);
	}

	/**
	 * Performs necessary menu pre-registration on the client
	 * @param contType The container type to register
	 * @param screenFactory The screen factory to be linked to the type
	 * @param id The ID to use
	 * @param <T> Container subclass for the menu type
	 * @param <U> Container subclass for the screen factory (usually the same as T)
	 */
	@OnlyIn(Dist.CLIENT)
	private static <T extends U, U extends AbstractContainerMenu> void registerConClient(MenuType<T> contType, MenuScreens.ScreenConstructor<U, AbstractContainerScreen<U>> screenFactory, String id){
		registerContainerType(contType, id);
		MenuScreens.register(contType, screenFactory);
	}
	
	public static final HashMap<String, MenuType<?>> toRegisterMenu = new HashMap<>();
}
