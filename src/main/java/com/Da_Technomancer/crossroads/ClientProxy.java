package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.RegisterBlockRenderer;
import com.Da_Technomancer.crossroads.client.bakedModel.BakedModelLoader;
import com.Da_Technomancer.crossroads.client.bakedModel.CustomModelLocationMapper;
import com.Da_Technomancer.crossroads.fluids.ModFluids;
import com.Da_Technomancer.crossroads.items.ModItems;

import amerifrance.guideapi.api.GuideAPI;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	protected void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);

		ModelLoaderRegistry.registerLoader(new BakedModelLoader());

		ModFluids.registerRenderers();
		ModBlocks.preInitModels();
		ModItems.initModels();

		if (Loader.isModLoaded("guideapi")){
			GuideBooks.mainGuide(e, true);
			GuideAPI.setModel(GuideBooks.main);
		}

		CustomModelLocationMapper.preInit();
	}

	@Override
	protected void init(FMLInitializationEvent e){
		super.init(e);
		RegisterBlockRenderer.registerBlockRenderer();
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

}
