package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.HashMap;

public class BakedModelLoader implements ICustomModelLoader{

	public static final HashMap<ResourceLocation, IUnbakedModel> MODEL_MAP = new HashMap<>();

	static{
		MODEL_MAP.put(PrototypeBakedModel.BAKED_MODEL, new PrototypeModel());
		MODEL_MAP.put(AdvConduitBakedModel.BAKED_MODEL, new AdvConduitModel());
		MODEL_MAP.put(AtmosChargerBakedModel.BAKED_MODEL, new AtmosChargerModel());
		MODEL_MAP.put(ReagentPumpBakedModel.BAKED_MODEL, new ReagentPumpModel());
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager){

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation){
		if(MODEL_MAP.containsKey(modelLocation)){
			return true;
		}

		//The molten metals for ore processing are special-cased to re-route to a dynamic forge fluid model
		return modelLocation.getNamespace().equals(Crossroads.MODID) && modelLocation.getPath().startsWith("fluids#molten_metal_");
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation){
		return MODEL_MAP.get(modelLocation);
	}
}
