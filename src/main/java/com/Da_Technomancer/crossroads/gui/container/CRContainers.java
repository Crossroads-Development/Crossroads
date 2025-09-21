package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.screen.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CRContainers{

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Crossroads.MODID);

	public static final DeferredHolder<MenuType<?>, MenuType<FireboxContainer>> FIREBOX_CONTAINER = CONTAINERS.register("firebox", () -> conType(FireboxContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<IceboxContainer>> ICEBOX_CONTAINER = CONTAINERS.register("icebox", () -> conType(IceboxContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FluidCoolerContainer>> FLUID_COOLER_CONTAINER = CONTAINERS.register("fluid_cooler", () -> conType(FluidCoolerContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CrucibleContainer>> CRUCIBLE_CONTAINER = CONTAINERS.register("crucible", () -> conType(CrucibleContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SaltReactorContainer>> SALT_REACTOR_CONTAINER = CONTAINERS.register("salt_reactor", () -> conType(SaltReactorContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SmelterContainer>> SMELTER_CONTAINER = CONTAINERS.register("smelter", () -> conType(SmelterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BlastFurnaceContainer>> IND_BLAST_FURNACE_CONTAINER = CONTAINERS.register("ind_blast_furnace", () -> conType(BlastFurnaceContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<MillstoneContainer>> MILLSTONE_CONTAINER = CONTAINERS.register("millstone", () -> conType(MillstoneContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<StampMillContainer>> STAMP_MILL_CONTAINER = CONTAINERS.register("stamp_mill", () -> conType(StampMillContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FatCollectorContainer>> FAT_COLLECTOR_CONTAINER = CONTAINERS.register("fat_collector", () -> conType(FatCollectorContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FatCongealerContainer>> FAT_CONGEALER_CONTAINER = CONTAINERS.register("fat_congealer", () -> conType(FatCongealerContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FatFeederContainer>> FAT_FEEDER_CONTAINER = CONTAINERS.register("fat_feeder", () -> conType(FatFeederContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FluidTankContainer>> FLUID_TANK_CONTAINER = CONTAINERS.register("fluid_tank", () -> conType(FluidTankContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<OreCleanserContainer>> ORE_CLEANSER_CONTAINER = CONTAINERS.register("ore_cleanser", () -> conType(OreCleanserContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<RadiatorContainer>> RADIATOR_CONTAINER = CONTAINERS.register("radiator", () -> conType(RadiatorContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SteamBoilerContainer>> STEAM_BOILER_CONTAINER = CONTAINERS.register("steam_boiler", () -> conType(SteamBoilerContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<WaterCentrifugeContainer>> WATER_CENTRIFUGE_CONTAINER = CONTAINERS.register("water_centrifuge", () -> conType(WaterCentrifugeContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ColorChartContainer>> COLOR_CHART_CONTAINER = CONTAINERS.register("color_chart", () -> conType(ColorChartContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BeamExtractorContainer>> BEAM_EXTRACTOR_CONTAINER = CONTAINERS.register("beam_extractor", () -> conType(BeamExtractorContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<HeatLimiterContainer>> HEAT_LIMITER_CONTAINER = CONTAINERS.register("heat_limiter", () -> conType(HeatLimiterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<RotaryPumpContainer>> ROTARY_PUMP_CONTAINER = CONTAINERS.register("rotary_pump", () -> conType(RotaryPumpContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<DetailedCrafterContainer>> DETAILED_CRAFTER_CONTAINER = CONTAINERS.register("detailed_crafter", () -> conType(DetailedCrafterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ReagentFilterContainer>> REAGENT_FILTER_CONTAINER = CONTAINERS.register("reagent_filter", () -> conType(ReagentFilterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CopshowiumMakerContainer>> COPSHOWIUM_MAKER_CONTAINER = CONTAINERS.register("copshowium_maker", () -> conType(CopshowiumMakerContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SteamerContainer>> STEAMER_CONTAINER = CONTAINERS.register("steamer", () -> conType(SteamerContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<WindingTableContainer>> WINDING_TABLE_CONTAINER = CONTAINERS.register("winding_table", () -> conType(WindingTableContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<DetailedAutoCrafterContainer>> DETAILED_AUTO_CRAFTER_CONTAINER = CONTAINERS.register("detailed_auto_crafter", () -> conType(DetailedAutoCrafterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SequenceBoxContainer>> SEQUENCE_BOX_CONTAINER = CONTAINERS.register("sequence_box", () -> conType(SequenceBoxContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SteamTurbineContainer>> STEAM_TURBINE_CONTAINER = CONTAINERS.register("steam_turbine", () -> conType(SteamTurbineContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BeaconHarnessContainer>> BEACON_HARNESS_CONTAINER = CONTAINERS.register("beacon_harness", () -> conType(BeaconHarnessContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FormulationVatContainer>> FORMULATION_VAT_CONTAINER = CONTAINERS.register("formulation_vat", () -> conType(FormulationVatContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BrewingVatContainer>> BREWING_VAT_CONTAINER = CONTAINERS.register("brewing_vat", () -> conType(BrewingVatContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<AutoInjectorContainer>> AUTO_INJECTOR_CONTAINER = CONTAINERS.register("auto_injector", () -> conType(AutoInjectorContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ColdStorageContainer>> COLD_STORAGE_CONTAINER = CONTAINERS.register("cold_storage", () -> conType(ColdStorageContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<HydroponicsTroughContainer>> HYDROPONICS_TROUGH_CONTAINER = CONTAINERS.register("hydroponics_trough", () -> conType(HydroponicsTroughContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<StasisStorageContainer>> STASIS_STORAGE_CONTAINER = CONTAINERS.register("stasis_storage", () -> conType(StasisStorageContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CultivatorVatContainer>> CULTIVATOR_VAT_CONTAINER = CONTAINERS.register("cultivator_vat", () -> conType(CultivatorVatContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<IncubatorContainer>> INCUBATOR_CONTAINER = CONTAINERS.register("incubator", () -> conType(IncubatorContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BloodCentrifugeContainer>> BLOOD_CENTRIFUGE_CONTAINER = CONTAINERS.register("blood_centrifuge", () -> conType(BloodCentrifugeContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<EmbryoLabContainer>> EMBRYO_LAB_CONTAINER = CONTAINERS.register("embryo_lab", () -> conType(EmbryoLabContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<HeatReservoirCreativeContainer>> HEAT_RESERVOIR_CREATIVE_CONTAINER = CONTAINERS.register("heat_reservoir_creative", () -> conType(HeatReservoirCreativeContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<MasterAxisCreativeContainer>> MASTER_AXIS_CREATIVE_CONTAINER = CONTAINERS.register("master_axis_creative", () -> conType(MasterAxisCreativeContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BeamExtractorCreativeContainer>> BEAM_EXTRACTOR_CREATIVE_CONTAINER = CONTAINERS.register("beam_extractor_creative", () -> conType(BeamExtractorCreativeContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BloodBeamLinkerContainer>> BLOOD_BEAM_LINKER_CONTAINER = CONTAINERS.register("blood_beam_linker", () -> conType(BloodBeamLinkerContainer::new));

	private static <T extends AbstractContainerMenu> MenuType<T> conType(IContainerFactory<T> cons){
		return new MenuType<>(cons, FeatureFlags.VANILLA_SET);
	}

	@OnlyIn(Dist.CLIENT)
	public static void initClient(RegisterMenuScreensEvent e){
		e.register(FIREBOX_CONTAINER.get(), FireboxScreen::new);
		e.register(ICEBOX_CONTAINER.get(), IceboxScreen::new);
		e.register(FLUID_COOLER_CONTAINER.get(), FluidCoolerScreen::new);
		e.register(CRUCIBLE_CONTAINER.get(), CrucibleScreen::new);
		e.register(SALT_REACTOR_CONTAINER.get(), SaltReactorScreen::new);
		e.register(SMELTER_CONTAINER.get(), SmelterScreen::new);
		e.register(IND_BLAST_FURNACE_CONTAINER.get(), BlastFurnaceScreen::new);
		e.register(MILLSTONE_CONTAINER.get(), MillstoneScreen::new);
		e.register(STAMP_MILL_CONTAINER.get(), StampMillScreen::new);
		e.register(FAT_COLLECTOR_CONTAINER.get(), FatCollectorScreen::new);
		e.register(FAT_CONGEALER_CONTAINER.get(), FatCongealerScreen::new);
		e.register(FAT_FEEDER_CONTAINER.get(), FatFeederScreen::new);
		e.register(FLUID_TANK_CONTAINER.get(), FluidTankScreen::new);
		e.register(ORE_CLEANSER_CONTAINER.get(), OreCleanserScreen::new);
		e.register(RADIATOR_CONTAINER.get(), RadiatorScreen::new);
		e.register(STEAM_BOILER_CONTAINER.get(), SteamBoilerScreen::new);
		e.register(WATER_CENTRIFUGE_CONTAINER.get(), WaterCentrifugeScreen::new);
		e.register(COLOR_CHART_CONTAINER.get(), ColorChartScreen::new);
		e.register(BEAM_EXTRACTOR_CONTAINER.get(), BeamExtractorScreen::new);
		e.register(HEAT_LIMITER_CONTAINER.get(), HeatLimiterScreen::new);
		e.register(ROTARY_PUMP_CONTAINER.get(), RotaryPumpScreen::new);
		e.register(DETAILED_CRAFTER_CONTAINER.get(), DetailedCrafterScreen::new);
		e.register(REAGENT_FILTER_CONTAINER.get(), ReagentFilterScreen::new);
		e.register(COPSHOWIUM_MAKER_CONTAINER.get(), CopshowiumMakerScreen::new);
		e.register(STEAMER_CONTAINER.get(), SteamerScreen::new);
		e.register(WINDING_TABLE_CONTAINER.get(), WindingTableScreen::new);
		e.register(DETAILED_AUTO_CRAFTER_CONTAINER.get(), DetailedAutoCrafterScreen::new);
		e.register(SEQUENCE_BOX_CONTAINER.get(), SequenceBoxScreen::new);
		e.register(STEAM_TURBINE_CONTAINER.get(), SteamTurbineScreen::new);
		e.register(BEACON_HARNESS_CONTAINER.get(), BeaconHarnessScreen::new);
		e.register(FORMULATION_VAT_CONTAINER.get(), FormulationVatScreen::new);
		e.register(BREWING_VAT_CONTAINER.get(), BrewingVatScreen::new);
		e.register(AUTO_INJECTOR_CONTAINER.get(), AutoInjectorScreen::new);
		e.register(COLD_STORAGE_CONTAINER.get(), ColdStorageScreen::new);
		e.register(HYDROPONICS_TROUGH_CONTAINER.get(), HydroponicsTroughScreen::new);
		e.register(STASIS_STORAGE_CONTAINER.get(), StasisStorageScreen::new);
		e.register(CULTIVATOR_VAT_CONTAINER.get(), CultivatorVatScreen::new);
		e.register(INCUBATOR_CONTAINER.get(), IncubatorScreen::new);
		e.register(BLOOD_CENTRIFUGE_CONTAINER.get(), BloodCentrifugeScreen::new);
		e.register(EMBRYO_LAB_CONTAINER.get(), EmbryoLabScreen::new);
		e.register(HEAT_RESERVOIR_CREATIVE_CONTAINER.get(), HeatReservoirCreativeScreen::new);
		e.register(MASTER_AXIS_CREATIVE_CONTAINER.get(), MasterAxisCreativeScreen::new);
		e.register(BEAM_EXTRACTOR_CREATIVE_CONTAINER.get(), BeamExtractorCreativeScreen::new);
		e.register(BLOOD_BEAM_LINKER_CONTAINER.get(), BloodBeamLinkerScreen::new);
	}

	public static void init(IEventBus modBus){
		CONTAINERS.register(modBus);
	}
}
