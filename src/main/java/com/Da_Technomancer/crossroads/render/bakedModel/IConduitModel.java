package com.Da_Technomancer.crossroads.render.bakedModel;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;

public interface IConduitModel{

	public ResourceLocation getTexture();

	public default ResourceLocation getTexture(BlockState state){
		return getTexture();
	}
	
	public double getSize();

}
