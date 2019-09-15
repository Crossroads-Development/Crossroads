package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.entity.ModEntities;
import com.Da_Technomancer.crossroads.fluids.CrossroadsFluids;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.items.itemSets.ItemSets;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.Da_Technomancer.crossroads.render.TESR.AAModTESR;
import com.Da_Technomancer.crossroads.render.bakedModel.BakedModelLoader;
import com.Da_Technomancer.crossroads.tileentities.ModTileEntity;
import com.Da_Technomancer.crossroads.world.ModWorldGen;
import com.mojang.datafixers.DSL;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

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

		CrossroadsConfig.init();

		MinecraftForge.EVENT_BUS.register(this);

		CrossroadsConfig.load();
	}


	protected static final ModWorldGen WORLD_GEN = new ModWorldGen();

	private void commonInit(@SuppressWarnings("unused") FMLCommonSetupEvent e){
		//Pre
		ModPackets.preInit();
		Capabilities.register();
		ModTileEntity.init();
		ModPackets.preInit();
		ModDimensions.init();
		ModEntities.init();
		//Main
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		//TODO
//		NetworkRegistry.INSTANCE.registerGuiHandler(Crossroads.instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		if(CrossroadsConfig.retrogen.get().isEmpty()){
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
		CrossroadsItems.clientInit();
		AAModTESR.registerBlockRenderer();
		Keys.init();
		ModParticles.clientInit();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	//TODO BELOW
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
		CrossroadsItems.init();
		for(Item item : CrossroadsItems.toRegister){
			registry.register(item);
		}
		CrossroadsItems.toRegister.clear();
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
		//TODO register TEs
	}

	private static void registerTE(Supplier<? extends TileEntity> cons, String id, IForgeRegistry<TileEntityType<?>> reg, Block... blocks){
		TileEntityType teType = TileEntityType.Builder.create(cons, blocks).build(DSL.nilType());
		teType.setRegistryName(new ResourceLocation(MODID, id));
		reg.register(teType);
	}

	@SubscribeEvent
	private static void registerModels(ModelRegistryEvent e){
		CrossroadsBlocks.initModels();
		CrossroadsItems.initModels();
		//CrossroadsFluids.registerRenderers();
		ItemSets.modelInit();
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	@OnlyIn(Dist.CLIENT)
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> e){
		//TODO register containers
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> e){
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