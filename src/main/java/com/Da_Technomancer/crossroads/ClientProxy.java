package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.AAModTESR;
import com.Da_Technomancer.crossroads.client.bakedModel.BakedModelLoader;
import com.Da_Technomancer.crossroads.entity.EntityBullet;
import com.Da_Technomancer.crossroads.fluids.ModFluids;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{
	
	@Override
	protected void preInit(FMLPreInitializationEvent e){
		super.preInit(e);
		ModelLoaderRegistry.registerLoader(new BakedModelLoader());
		ModFluids.registerRenderers();
		ModBlocks.preInitModels();
		ModItems.initModels();
		HeatCableFactory.clientInit();
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (RenderManager manager) -> (new RenderSnowball<EntityBullet>(manager, Items.field_191525_da, Minecraft.getMinecraft().getRenderItem())));
	}

	@Override
	protected void init(FMLInitializationEvent e){
		super.init(e);
		GearFactory.clientInit();
		AAModTESR.registerBlockRenderer();
		Keys.init();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}
}
