package com.Da_Technomancer.crossroads.client.bakedModel;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class BakedModelLoader implements ICustomModelLoader{

	public static final ConduitModel HEAT_CABLE_MODEL = new ConduitModel();
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return modelLocation.getResourceDomain().equals(Main.MODID) && "heatcable".equals(modelLocation.getResourcePath());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		return HEAT_CABLE_MODEL;
	}

}
