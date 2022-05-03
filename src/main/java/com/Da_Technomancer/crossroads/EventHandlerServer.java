package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.gui.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class EventHandlerServer{

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Crossroads.MODID, value = Dist.DEDICATED_SERVER)
	public static class CRModEventsServer{

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerContainerTypes(RegistryEvent.Register<MenuType<?>> e){
			EventHandlerCommon.CRModEventsCommon.registerConType(FireboxContainer::new, "firebox", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(IceboxContainer::new, "icebox", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(FluidCoolerContainer::new, "fluid_cooler", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(CrucibleContainer::new, "crucible", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(SaltReactorContainer::new, "salt_reactor", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(SmelterContainer::new, "smelter", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(BlastFurnaceContainer::new, "ind_blast_furnace", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(MillstoneContainer::new, "millstone", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(StampMillContainer::new, "stamp_mill", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(FatCollectorContainer::new, "fat_collector", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(FatCongealerContainer::new, "fat_congealer", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(FatFeederContainer::new, "fat_feeder", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(FluidTankContainer::new, "fluid_tank", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(OreCleanserContainer::new, "ore_cleanser", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(RadiatorContainer::new, "radiator", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(SteamBoilerContainer::new, "steam_boiler", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(WaterCentrifugeContainer::new, "water_centrifuge", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(ColorChartContainer::new, "color_chart", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(BeamExtractorContainer::new, "beam_extractor", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(HeatLimiterContainer::new, "heat_limiter", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(RotaryPumpContainer::new, "rotary_pump", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(DetailedCrafterContainer::new, "detailed_crafter", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(ReagentFilterContainer::new, "reagent_filter", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(CopshowiumMakerContainer::new, "copshowium_maker", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(SteamerContainer::new, "steamer", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(WindingTableContainer::new, "winding_table", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(DetailedAutoCrafterContainer::new, "detailed_auto_crafter", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(SequenceBoxContainer::new, "sequence_box", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(SteamTurbineContainer::new, "steam_turbine", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(BeaconHarnessContainer::new, "beacon_harness", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(FormulationVatContainer::new, "formulation_vat", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(BrewingVatContainer::new, "brewing_vat", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(AutoInjectorContainer::new, "auto_injector", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(ColdStorageContainer::new, "cold_storage", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(HydroponicsTroughContainer::new, "hydroponics_trough", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(StasisStorageContainer::new, "stasis_storage", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(CultivatorVatContainer::new, "cultivator_vat", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(IncubatorContainer::new, "incubator", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(BloodCentrifugeContainer::new, "blood_centrifuge", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(EmbryoLabContainer::new, "embryo_lab", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(HeatReservoirCreativeContainer::new, "heat_reservoir_creative", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(MasterAxisCreativeContainer::new, "master_axis_creative", e);
			EventHandlerCommon.CRModEventsCommon.registerConType(BeamExtractorCreativeContainer::new, "beam_extractor_creative", e);
		}
	}
}
