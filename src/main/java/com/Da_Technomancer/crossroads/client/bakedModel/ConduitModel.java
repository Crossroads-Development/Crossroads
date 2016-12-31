package com.Da_Technomancer.crossroads.client.bakedModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class ConduitModel implements IModel{

	@Override
	public Collection<ResourceLocation> getDependencies(){
		return Collections.emptySet();
	}

	@Override
	public Collection<ResourceLocation> getTextures(){
		ArrayList<ResourceLocation> textures = new ArrayList<>();
		for(HeatInsulators insul : HeatInsulators.values()){
			for(HeatConductors cond : HeatConductors.values()){
				textures.add(new ResourceLocation(Main.MODID, "blocks/heatcable/" + insul.name().toLowerCase() + '-' + cond.name().toLowerCase()));
				textures.add(new ResourceLocation(Main.MODID, "blocks/heatcable/" + insul.name().toLowerCase() + '-' + cond.name().toLowerCase() + "-redstone"));
			}
		}
		textures.add(new ResourceLocation(Main.MODID, "blocks/blockBronze"));
		return ImmutableSet.copyOf(textures);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		return new ConduitBakedModel(format, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState(){
		return TRSRTransformation.identity();
	}

}
