package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.entity.ModEntities;
import com.Da_Technomancer.crossroads.fluids.ModFluids;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.ItemSets;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.Da_Technomancer.crossroads.render.TESR.AAModTESR;
import com.Da_Technomancer.crossroads.render.bakedModel.BakedModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy{
	
	@Override
	protected void preInit(FMLPreInitializationEvent e){
		super.preInit(e);
		ModelLoaderRegistry.registerLoader(new BakedModelLoader());
		ModEntities.clientInit();
	}

	@Override
	protected void init(FMLInitializationEvent e){
		super.init(e);
		ItemSets.clientInit();
		ModItems.clientInit();
		AAModTESR.registerBlockRenderer();
		Keys.init();
		ModParticles.clientInit();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent e){
		ModBlocks.initModels();
		ModItems.initModels();
		ModFluids.registerRenderers();
		ItemSets.modelInit();
	}
}
