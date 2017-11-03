package com.Da_Technomancer.crossroads.client.bakedModel;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public interface IAdvConduitModel{

	public ResourceLocation getTexture(IBlockState state, int mode);
	
	public double getSize();

}
