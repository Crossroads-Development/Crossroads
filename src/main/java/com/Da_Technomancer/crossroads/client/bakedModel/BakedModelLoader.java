package com.Da_Technomancer.crossroads.client.bakedModel;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.HashMap;

public class BakedModelLoader implements ICustomModelLoader{

	protected static final HashMap<ResourceLocation, IModel> MODEL_MAP = new HashMap<>();

	static{
		MODEL_MAP.put(ConduitBakedModel.BAKED_MODEL, new ConduitModel());
		MODEL_MAP.put(PrototypeBakedModel.BAKED_MODEL, new PrototypeModel());
		MODEL_MAP.put(AdvConduitBakedModel.BAKED_MODEL, new AdvConduitModel());
		MODEL_MAP.put(AtmosChargerBakedModel.BAKED_MODEL, new AtmosChargerModel());
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager){
		
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation){
		return MODEL_MAP.containsKey(modelLocation);
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation){
		return MODEL_MAP.get(modelLocation);
	}
}
