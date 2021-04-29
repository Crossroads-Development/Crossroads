package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.loot_modifiers.PiglinBarterLootModifier;
import com.Da_Technomancer.crossroads.crafting.recipes.*;
import com.Da_Technomancer.crossroads.entity.*;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.*;
import com.Da_Technomancer.crossroads.gui.container.*;
import com.Da_Technomancer.crossroads.integration.CRIntegration;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.ItemSets;
import com.Da_Technomancer.crossroads.particles.CRParticles;
import com.Da_Technomancer.crossroads.particles.ColorParticleType;
import com.Da_Technomancer.crossroads.particles.sounds.CRSounds;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.TESR.CRRendererRegistry;
import com.Da_Technomancer.crossroads.tileentities.CRTileEntity;
import com.Da_Technomancer.crossroads.world.CRWorldGen;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.Da_Technomancer.crossroads.Crossroads.MODID;

@Mod(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Crossroads{

	public static final String MODID = "crossroads";
	public static final String MODNAME = "Crossroads";
	public static final Logger logger = LogManager.getLogger(MODNAME);

	public Crossroads(){
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonInit);
		bus.addListener(this::clientInit);
		bus.addListener(this::serverStarted);

		CRConfig.init();

		MinecraftForge.EVENT_BUS.register(this);

		CRConfig.load();
	}

	private void commonInit(@SuppressWarnings("unused") FMLCommonSetupEvent e){
		//Pre
		CRPackets.preInit();
		Capabilities.register();
//		CrossroadsTileEntity.init();
//		ModDimensions.init();
		//Main
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
		//NetworkRegistry.INSTANCE.registerGuiHandler(Crossroads.instance, new GuiHandler());
		CRIntegration.init();
		CurioHelper.initIntegration();
//		CrossroadsConfig.config.save();
//		ModIntegration.preInit();
	}

	private void clientInit(@SuppressWarnings("unused") FMLClientSetupEvent e){
//		TESRRegistry.init();
//		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
//		ModelLoaderRegistry.registerLoader(new BakedModelLoader());
		CRItems.clientInit();
		CRRendererRegistry.registerBlockRenderer();
		CRRendererRegistry.registerEntityLayerRenderers();
		Keys.init();
//		CRParticles.clientInit();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	private void serverStarted(FMLDedicatedServerSetupEvent e){
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent e){
		CRBlocks.clientInit();
		CREntities.clientInit();
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e){
		IForgeRegistry<Block> registry = e.getRegistry();
		CRBlocks.init();
		ItemSets.init();
		CRFluids.init();
		for(Block block : CRBlocks.toRegister){
			registry.register(block);
		}
		CRBlocks.toRegister.clear();
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e){
		IForgeRegistry<Item> registry = e.getRegistry();
		CRItems.init();
		for(Item item : CRItems.toRegister){
			registry.register(item);
		}
		CRItems.toRegister.clear();
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> e){
		IForgeRegistry<Fluid> registry = e.getRegistry();
		for(Fluid f : CRFluids.toRegister){
			registry.register(f);
		}
		CRFluids.toRegister.clear();
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e){
		IForgeRegistry<IRecipeSerializer<?>> reg = e.getRegistry();
		reg.register(new SingleIngrRecipe.SingleRecipeSerializer<>(StampMillRec::new).setRegistryName("stamp_mill"));
		reg.register(new MillRec.Serializer().setRegistryName("mill"));
		reg.register(new SingleIngrRecipe.SingleRecipeSerializer<>(OreCleanserRec::new).setRegistryName("ore_cleanser"));
		reg.register(new BeamExtractRec.Serializer().setRegistryName("beam_extract"));
		reg.register(new IceboxRec.Serializer().setRegistryName("cooling"));
		reg.register(new CentrifugeRec.Serializer().setRegistryName("centrifuge"));
		reg.register(new AlchemyRec.Serializer().setRegistryName("alchemy"));
		reg.register(new BlastFurnaceRec.Serializer().setRegistryName("cr_blast_furnace"));
		reg.register(new FluidCoolingRec.Serializer().setRegistryName("fluid_cooling"));
		reg.register(new CrucibleRec.Serializer().setRegistryName("crucible"));
		reg.register(new DetailedCrafterRec.Serializer().setRegistryName("detailed_crafter"));
		reg.register(new BeamTransmuteRec.Serializer().setRegistryName("beam_transmute"));
		reg.register(new BoboRec.Serializer().setRegistryName("bobo"));
		reg.register(new CopshowiumRec.Serializer().setRegistryName("copshowium"));
		reg.register(new ReagentRec.Serializer().setRegistryName("reagents"));
		reg.register(new FormulationVatRec.Serializer().setRegistryName("formulation_vat"));
	}

	@SubscribeEvent
	public static void registerLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> e){
		e.getRegistry().register(new PiglinBarterLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID, "piglin_barter")));
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerEnts(RegistryEvent.Register<EntityType<?>> e){
		IForgeRegistry<EntityType<?>> reg = e.getRegistry();

		reg.register(EntityType.Builder.of(EntityFlameCore::new, EntityClassification.MISC).fireImmune().noSummon().setShouldReceiveVelocityUpdates(false).sized(1, 1).build("flame_core").setRegistryName("flame_core"));
		reg.register(EntityType.Builder.<EntityShell>of(EntityShell::new, EntityClassification.MISC).fireImmune().setTrackingRange(64).setUpdateInterval(5).sized(.25F, .25F).build("shell").setRegistryName("shell"));
		reg.register(EntityType.Builder.<EntityNitro>of(EntityNitro::new, EntityClassification.MISC).setTrackingRange(64).setUpdateInterval(5).build("nitro").setRegistryName("nitro"));
		reg.register(EntityType.Builder.<EntityGhostMarker>of(EntityGhostMarker::new, EntityClassification.MISC).noSummon().setTrackingRange(64).setUpdateInterval(20).fireImmune().setShouldReceiveVelocityUpdates(false).build("ghost_marker").setRegistryName("ghost_marker"));
		reg.register(EntityType.Builder.of(EntityFlyingMachine::new, EntityClassification.MISC).sized(1F, 1.3F).setTrackingRange(64).setUpdateInterval(1).build("flying_machine").setRegistryName("flying_machine"));
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> e){
		IForgeRegistry<ParticleType<?>> registry = e.getRegistry();
		registry.register(new ColorParticleType("color_flame", false));
		registry.register(new ColorParticleType("color_gas", false));
		registry.register(new ColorParticleType("color_liquid", false));
		registry.register(new ColorParticleType("color_solid", false));
		registry.register(new ColorParticleType("color_splash", false));
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent e){
		CRParticles.clientInit();
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> e){
		IForgeRegistry<TileEntityType<?>> reg = e.getRegistry();
		CRTileEntity.init(reg);
	}

	@SuppressWarnings("unused")
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> e){
		registerCon(FireboxContainer::new, FireboxScreen::new, "firebox", e);
		registerCon(IceboxContainer::new, IceboxScreen::new, "icebox", e);
		registerCon(FluidCoolerContainer::new, FluidCoolerScreen::new, "fluid_cooler", e);
		registerCon(CrucibleContainer::new, CrucibleScreen::new, "crucible", e);
		registerCon(SaltReactorContainer::new, SaltReactorScreen::new, "salt_reactor", e);
		registerCon(SmelterContainer::new, SmelterScreen::new, "smelter", e);
		registerCon(BlastFurnaceContainer::new, BlastFurnaceScreen::new, "ind_blast_furnace", e);
		registerCon(MillstoneContainer::new, MillstoneScreen::new, "millstone", e);
		registerCon(StampMillContainer::new, StampMillScreen::new, "stamp_mill", e);
		registerCon(FatCollectorContainer::new, FatCollectorScreen::new, "fat_collector", e);
		registerCon(FatCongealerContainer::new, FatCongealerScreen::new, "fat_congealer", e);
		registerCon(FatFeederContainer::new, FatFeederScreen::new, "fat_feeder", e);
		registerCon(FluidTankContainer::new, FluidTankScreen::new, "fluid_tank", e);
		registerCon(OreCleanserContainer::new, OreCleanserScreen::new, "ore_cleanser", e);
		registerCon(RadiatorContainer::new, RadiatorScreen::new, "radiator", e);
		registerCon(SteamBoilerContainer::new, SteamBoilerScreen::new, "steam_boiler", e);
		registerCon(WaterCentrifugeContainer::new, WaterCentrifugeScreen::new, "water_centrifuge", e);
		registerCon(ColorChartContainer::new, ColorChartScreen::new, "color_chart", e);
		registerCon(BeamExtractorContainer::new, BeamExtractorScreen::new, "beam_extractor", e);
		registerCon(HeatLimiterContainer::new, HeatLimiterScreen::new, "heat_limiter", e);
		registerCon(RotaryPumpContainer::new, RotaryPumpScreen::new, "rotary_pump", e);
		registerCon(DetailedCrafterContainer::new, DetailedCrafterScreen::new, "detailed_crafter", e);
		registerCon(ReagentFilterContainer::new, ReagentFilterScreen::new, "reagent_filter", e);
		registerCon(CopshowiumMakerContainer::new, CopshowiumMakerScreen::new, "copshowium_maker", e);
		registerCon(SteamerContainer::new, SteamerScreen::new, "steamer", e);
		registerCon(WindingTableContainer::new, WindingTableScreen::new, "winding_table", e);
		registerCon(DetailedAutoCrafterContainer::new, DetailedAutoCrafterScreen::new, "detailed_auto_crafter", e);
		registerCon(SequenceBoxContainer::new, SequenceBoxScreen::new, "sequence_box", e);
		registerCon(SteamTurbineContainer::new, SteamTurbineScreen::new, "steam_turbine", e);
		registerCon(BeaconHarnessContainer::new, BeaconHarnessScreen::new, "beacon_harness", e);
		registerCon(FormulationVatContainer::new, FormulationVatScreen::new, "formulation_vat", e);
		registerCon(BrewingVatContainer::new, BrewingVatScreen::new, "brewing_vat", e);
		registerCon(AutoInjectorContainer::new, AutoInjectorScreen::new, "auto_injector", e);
	}

	@SuppressWarnings("unused")
	@OnlyIn(Dist.DEDICATED_SERVER)
	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> e){
		registerConType(FireboxContainer::new, "firebox", e);
		registerConType(IceboxContainer::new, "icebox", e);
		registerConType(FluidCoolerContainer::new, "fluid_cooler", e);
		registerConType(CrucibleContainer::new, "crucible", e);
		registerConType(SaltReactorContainer::new, "salt_reactor", e);
		registerConType(SmelterContainer::new, "smelter", e);
		registerConType(BlastFurnaceContainer::new, "ind_blast_furnace", e);
		registerConType(MillstoneContainer::new, "millstone", e);
		registerConType(StampMillContainer::new, "stamp_mill", e);
		registerConType(FatCollectorContainer::new, "fat_collector", e);
		registerConType(FatCongealerContainer::new, "fat_congealer", e);
		registerConType(FatFeederContainer::new, "fat_feeder", e);
		registerConType(FluidTankContainer::new, "fluid_tank", e);
		registerConType(OreCleanserContainer::new, "ore_cleanser", e);
		registerConType(RadiatorContainer::new, "radiator", e);
		registerConType(SteamBoilerContainer::new, "steam_boiler", e);
		registerConType(WaterCentrifugeContainer::new, "water_centrifuge", e);
		registerConType(ColorChartContainer::new, "color_chart", e);
		registerConType(BeamExtractorContainer::new, "beam_extractor", e);
		registerConType(HeatLimiterContainer::new, "heat_limiter", e);
		registerConType(RotaryPumpContainer::new, "rotary_pump", e);
		registerConType(DetailedCrafterContainer::new, "detailed_crafter", e);
		registerConType(ReagentFilterContainer::new, "reagent_filter", e);
		registerConType(CopshowiumMakerContainer::new, "copshowium_maker", e);
		registerConType(SteamerContainer::new, "steamer", e);
		registerConType(WindingTableContainer::new, "winding_table", e);
		registerConType(DetailedAutoCrafterContainer::new, "detailed_auto_crafter", e);
		registerConType(SequenceBoxContainer::new, "sequence_box", e);
		registerConType(SteamTurbineContainer::new, "steam_turbine", e);
		registerConType(BeaconHarnessContainer::new, "beacon_harness", e);
		registerConType(FormulationVatContainer::new, "formulation_vat", e);
		registerConType(BrewingVatContainer::new, "brewing_vat", e);
		registerConType(AutoInjectorContainer::new, "auto_injector", e);
	}

	/**
	 * Creates and registers a container type
	 * @param cons Container factory
	 * @param id The ID to use
	 * @param reg Registry event
	 * @param <T> Container subclass
	 * @return The newly created type
	 */
	private static <T extends Container> ContainerType<T> registerConType(IContainerFactory<T> cons, String id, RegistryEvent.Register<ContainerType<?>> reg){
		ContainerType<T> contType = new ContainerType<>(cons);
		contType.setRegistryName(new ResourceLocation(MODID, id));
		reg.getRegistry().register(contType);
		return contType;
	}

	/**
	 * Creates and registers both a container type and a screen factory. Not usable on the physical server due to screen factory.
	 * @param cons Container factory
	 * @param screenFactory The screen factory to be linked to the type
	 * @param id The ID to use
	 * @param reg Registry event
	 * @param <T> Container subclass
	 */
	@OnlyIn(Dist.CLIENT)
	private static <T extends Container> void registerCon(IContainerFactory<T> cons, ScreenManager.IScreenFactory<T, ContainerScreen<T>> screenFactory, String id, RegistryEvent.Register<ContainerType<?>> reg){
		ContainerType<T> contType = registerConType(cons, id, reg);
		ScreenManager.register(contType, screenFactory);
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerWorldgen(RegistryEvent.Register<Feature<?>> e){
		CRWorldGen.init();
		CRWorldGen.register(e.getRegistry());
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> e){
		CRSounds.register(e.getRegistry());
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerMobEffects(RegistryEvent.Register<Effect> e){
		CRPotions.registerEffects(e.getRegistry());
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<Potion> e){
		CRPotions.registerPotions(e.getRegistry());
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	@OnlyIn(Dist.CLIENT)
	public static void onTextureStitch(TextureStitchEvent.Pre event){
		//Add textures used in TESRs
		CRRenderTypes.stitchTextures(event);
	}
}