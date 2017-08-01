package com.Da_Technomancer.crossroads.client.bakedModel;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public interface IConduitModel{

	public ResourceLocation getTexture();

	public default ResourceLocation getTexture(IBlockState state){
		return getTexture();
	}
	
	public double getSize();

}
