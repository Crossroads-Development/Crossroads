package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.entity.ModEntities;
import com.Da_Technomancer.crossroads.fluids.CrossroadsFluids;
import com.Da_Technomancer.crossroads.gui.*;
import com.Da_Technomancer.crossroads.gui.container.*;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipeGenerator;
import com.Da_Technomancer.crossroads.items.crafting.recipes.*;
import com.Da_Technomancer.crossroads.items.itemSets.ItemSets;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.Da_Technomancer.crossroads.render.TESR.AAModTESR;
import com.Da_Technomancer.crossroads.render.bakedModel.BakedModelLoader;
import com.Da_Technomancer.crossroads.tileentities.CrossroadsTileEntity;
import com.Da_Technomancer.crossroads.world.ModWorldGen;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
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
		bus.addListener(this::gatherData);

		CRConfig.init();

		MinecraftForge.EVENT_BUS.register(this);

		CRConfig.load();
	}


	protected static final ModWorldGen WORLD_GEN = new ModWorldGen();

	private void commonInit(@SuppressWarnings("unused") FMLCommonSetupEvent e){
		//Pre
		CrossroadsPackets.preInit();
		Capabilities.register();
//		CrossroadsTileEntity.init();
		CrossroadsPackets.preInit();
		ModDimensions.init();
		ModEntities.init();
		//Main
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		//TODO
//		NetworkRegistry.INSTANCE.registerGuiHandler(Crossroads.instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		if(CRConfig.retrogen.get().isEmpty()){
			//TODO
//			GameRegistry.registerWorldGenerator(WORLD_GEN, 0);
		}
//		ModIntegration.init();

//		CrossroadsConfig.config.save();

//		ModIntegration.preInit();
	}

	private void clientInit(@SuppressWarnings("unused") FMLClientSetupEvent e){
//		TESRRegistry.init();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
		//TODO entity renderer registration
		ModelLoaderRegistry.registerLoader(new BakedModelLoader());
		ModEntities.clientInit();
		ItemSets.clientInit();
		CRItems.clientInit();
		AAModTESR.registerBlockRenderer();
		Keys.init();
		ModParticles.clientInit();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	private void gatherData(GatherDataEvent e){
		if(e.includeServer()){
			DataGenerator gen = e.getGenerator();
			gen.addProvider(new CRItemTags(gen));
			gen.addProvider(new CRRecipeGenerator(gen));
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e){
		IForgeRegistry<Block> registry = e.getRegistry();
		CrossroadsBlocks.init();
		CrossroadsFluids.init();
		ItemSets.init();
		for(Block block : CrossroadsBlocks.toRegister){
			registry.register(block);
		}
		CrossroadsBlocks.toRegister.clear();
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
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e){
		IForgeRegistry<IRecipeSerializer<?>> reg = e.getRegistry();
		reg.register(new SingleRecipeSerializer<>(StampMillRec::new).setRegistryName("stamp_mill"));
		reg.register(new MillRec.Serializer().setRegistryName("mill"));
		reg.register(new SingleRecipeSerializer<>(OreCleanserRec::new).setRegistryName("ore_cleanser"));
		reg.register(new BeamExtractRec.Serializer().setRegistryName("beam_extract"));
		reg.register(new IceboxRec.Serializer().setRegistryName("cooling"));
		reg.register(new DirtyWaterRec.Serializer().setRegistryName("dirty_water"));
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerEnts(RegistryEvent.Register<EntityType<?>> e){
		IForgeRegistry<EntityType<?>> registry = e.getRegistry();
		//TODO register entities
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> e){
		IForgeRegistry<TileEntityType<?>> reg = e.getRegistry();
		CrossroadsTileEntity.init(reg);
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	private static void registerModels(ModelRegistryEvent e){
		CrossroadsBlocks.initModels();
		CRItems.initModels();
		//CrossroadsFluids.registerRenderers();
		ItemSets.modelInit();
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	@OnlyIn(Dist.CLIENT)
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> e){
		registerCon(FireboxContainer::new, FireboxScreen::new, "firebox", e);
		registerCon(IceboxContainer::new, IceboxScreen::new, "icebox", e);
		registerCon(FluidCoolerContainer::new, FluidCoolerScreen::new, "fluid_cooler", e);
		registerCon(CrucibleContainer::new, CrucibleScreen::new, "crucible", e);
		registerCon(SaltReactorContainer::new, SaltReactorScreen::new, "salt_reactor", e);
		registerCon(SmelterContainer::new, SmelterScreen::new, "smelter", e);
		registerCon(BlastFurnaceContainer::new, BlastFurnaceScreen::new, "ind_blast_furnace", e);


		//TODO register containers
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> e){
		registerConType(FireboxContainer::new, "firebox", e);
		registerConType(IceboxContainer::new, "icebox", e);
		registerConType(FluidCoolerContainer::new, "fluid_cooler", e);
		registerConType(CrucibleContainer::new, "crucible", e);
		registerConType(SaltReactorContainer::new, "salt_reactor", e);
		registerConType(SmelterContainer::new, "smelter", e);
		registerConType(BlastFurnaceContainer::new, "ind_blast_furnace", e);

		//TODO register container types
	}

	/**
	 * Creates and registers a container type
	 * @param cons Container factory
	 * @param id The ID to use
	 * @param reg Registery event
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
	 * @param reg Registery event
	 * @param <T> Container subclass
	 */
	@OnlyIn(Dist.CLIENT)
	private static <T extends Container> void registerCon(IContainerFactory<T> cons, ScreenManager.IScreenFactory<T, ContainerScreen<T>> screenFactory, String id, RegistryEvent.Register<ContainerType<?>> reg){
		ContainerType<T> contType = registerConType(cons, id, reg);
		ScreenManager.registerFactory(contType, screenFactory);
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void registerFluids(RegistryEvent.Register<Fluid> e){
		IForgeRegistry<Fluid> registry = e.getRegistry();
		for(Fluid f : CrossroadsFluids.toRegister){
			registry.register(f);
		}
		CrossroadsFluids.toRegister.clear();
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void registerWorldgen(RegistryEvent.Register<Feature<?>> e){
		ModWorldGen.register(e.getRegistry());
	}

	@SubscribeEvent
	public void serverLoading(FMLServerStartingEvent e){
		//TODO
//		e.registerServerCommand(new WorkspaceDimTeleport());
//		e.registerServerCommand(new ResetPathCommand());
//		e.registerServerCommand(new DiscoverElementCommand());
//		e.registerServerCommand(new SpawnReagentCommand());
//		e.registerServerCommand(new FluxCommand());
	}

	@SubscribeEvent
	public void serverStarted(FMLServerStartedEvent e){
		//TODO
//		ModDimensions.loadDims();
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
	}

	@SubscribeEvent
	public void serverEnded(FMLServerStoppingEvent e){
		//TODO
//		ForgeChunkManager.releaseTicket(EventHandlerCommon.loadingTicket);
//		EventHandlerCommon.loadingTicket = null;
//		for(int i : DimensionManager.getDimensions(ModDimensions.workspaceDimType)){
//			DimensionManager.unregisterDimension(i);
//		}
	}
}