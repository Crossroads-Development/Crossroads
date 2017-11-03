package com.Da_Technomancer.crossroads.client.bakedModel;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class BakedModelLoader implements ICustomModelLoader{

	protected static final ConduitModel CONDUIT_MODEL = new ConduitModel();
	protected static final PrototypeModel PROTOTYPE_MODEL = new PrototypeModel();
	protected static final AdvConduitModel ADV_CONDUIT_MODEL = new AdvConduitModel();

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager){
		
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation){
		return modelLocation == ConduitBakedModel.BAKED_MODEL || modelLocation == PrototypeBakedModel.BAKED_MODEL || modelLocation == AdvConduitBakedModel.BAKED_MODEL;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception{
		return modelLocation == ConduitBakedModel.BAKED_MODEL ? CONDUIT_MODEL : modelLocation == AdvConduitBakedModel.BAKED_MODEL ? ADV_CONDUIT_MODEL : PROTOTYPE_MODEL;
	}
}
