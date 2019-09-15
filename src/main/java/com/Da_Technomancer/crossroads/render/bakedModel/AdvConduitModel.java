package com.Da_Technomancer.crossroads.render.bakedModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import com.Da_Technomancer.crossroads.Crossroads;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class AdvConduitModel implements IModel{

	@Override
	public Collection<ResourceLocation> getDependencies(){
		return Collections.emptySet();
	}

	@Override
	public Collection<ResourceLocation> getTextures(){
		ArrayList<ResourceLocation> textures = new ArrayList<>();
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/cryst_tube_cap"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/cryst_tube_out"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/cryst_tube_in"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/glass_tube_cap"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/glass_tube_out"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/glass_tube_in"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/block_bronze"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/fluid_tube_out"));
		textures.add(new ResourceLocation(Crossroads.MODID, "blocks/fluid_tube_in"));
		return ImmutableSet.copyOf(textures);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		return new AdvConduitBakedModel(format, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState(){
		return TRSRTransformation.identity();
	}

}
