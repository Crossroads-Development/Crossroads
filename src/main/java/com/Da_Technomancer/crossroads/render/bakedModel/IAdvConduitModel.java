package com.Da_Technomancer.crossroads.render.bakedModel;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;

public interface IAdvConduitModel{

	public ResourceLocation getTexture(BlockState state, int mode);
	
	public double getSize();

}
