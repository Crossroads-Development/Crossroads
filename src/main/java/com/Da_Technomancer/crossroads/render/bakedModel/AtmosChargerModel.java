package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class AtmosChargerModel implements IModel{

	@Override
	public Collection<ResourceLocation> getDependencies(){
		return Collections.emptySet();
	}

	@Override
	public Collection<ResourceLocation> getTextures(){
		return ImmutableSet.of(new ResourceLocation(Main.MODID, "blocks/block_cast_iron"), new ResourceLocation(Main.MODID, "blocks/tesla_out"), new ResourceLocation(Main.MODID, "blocks/tesla_in"));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat vf, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		return new AtmosChargerBakedModel(vf, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState(){
		return TRSRTransformation.identity();
	}
}
