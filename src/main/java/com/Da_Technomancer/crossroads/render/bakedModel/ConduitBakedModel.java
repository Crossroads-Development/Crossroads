package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ConduitBakedModel implements IBakedModel{

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Crossroads.MODID, "conduit");

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	protected ConduitBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;
		this.format = format;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand){
		if(side != null || state == null){
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();
		TextureAtlasSprite sprite = bakedTextureGetter.apply(((IConduitModel) state.getBlock()).getTexture(state));
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

		if(extendedBlockState.get(CRProperties.CONNECT) == null){
			return Collections.emptyList();
		}

		double size = ((IConduitModel) state.getBlock()).getSize();
		double sizeEnd = 1 - size;
		int tSize = (int) (16D * size / (1D - (2D * size)));
		
		if(extendedBlockState.get(CRProperties.CONNECT)[1]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, 1, size), new Vec3d(sizeEnd, 1, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), Direction.EAST, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, 1, sizeEnd), new Vec3d(size, 1, size), new Vec3d(size, sizeEnd, size), Direction.WEST, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(size, sizeEnd, size), new Vec3d(size, 1, size), new Vec3d(sizeEnd, 1, size), Direction.NORTH, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, 1, sizeEnd), new Vec3d(size, 1, sizeEnd), Direction.SOUTH, 0, 16, tSize, 0, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(size, sizeEnd, size), Direction.UP, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.get(CRProperties.CONNECT)[0]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, 0, sizeEnd), new Vec3d(sizeEnd, 0, size), new Vec3d(sizeEnd, size, size), Direction.EAST, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(size, 0, size), new Vec3d(size, 0, sizeEnd), new Vec3d(size, size, sizeEnd), Direction.WEST, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, 0, size), new Vec3d(size, 0, size), Direction.NORTH, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(size, size, sizeEnd), new Vec3d(size, 0, sizeEnd), new Vec3d(sizeEnd, 0, sizeEnd), Direction.SOUTH, 0, 0, tSize, 16, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(size, size, sizeEnd), Direction.DOWN, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.get(CRProperties.CONNECT)[5]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(1, sizeEnd, sizeEnd), new Vec3d(1, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), Direction.UP, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(1, size, size), new Vec3d(1, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), Direction.DOWN, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(1, sizeEnd, size), new Vec3d(1, size, size), new Vec3d(sizeEnd, size, size), Direction.NORTH, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(1, size, sizeEnd), new Vec3d(1, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), Direction.SOUTH, 16, 0, 0, tSize, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), Direction.EAST, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.get(CRProperties.CONNECT)[4]){
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(0, sizeEnd, size), new Vec3d(0, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), Direction.UP, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(0, size, sizeEnd), new Vec3d(0, size, size), new Vec3d(size, size, size), Direction.DOWN, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(0, size, size), new Vec3d(0, sizeEnd, size), new Vec3d(size, sizeEnd, size), Direction.NORTH, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(0, sizeEnd, sizeEnd), new Vec3d(0, size, sizeEnd), new Vec3d(size, size, sizeEnd), Direction.SOUTH, 0, 0, 16, tSize, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, size), new Vec3d(size, size, size), Direction.WEST, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.get(CRProperties.CONNECT)[2]){
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, 0), new Vec3d(size, sizeEnd, 0), Direction.UP, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(size, size, size), new Vec3d(size, size, 0), new Vec3d(sizeEnd, size, 0), Direction.DOWN, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, size, 0), new Vec3d(sizeEnd, sizeEnd, 0), Direction.EAST, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(size, sizeEnd, size), new Vec3d(size, sizeEnd, 0), new Vec3d(size, size, 0), Direction.WEST, 0, 16, tSize, 0, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, size, size), new Vec3d(size, size, size), Direction.NORTH, 0, 0, 16, 16, sprite, format));
		}
		if(extendedBlockState.get(CRProperties.CONNECT)[3]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, 1), new Vec3d(sizeEnd, sizeEnd, 1), Direction.UP, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, size, 1), new Vec3d(size, size, 1), Direction.DOWN, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, 1), new Vec3d(sizeEnd, size, 1), Direction.EAST, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, size, sizeEnd), new Vec3d(size, size, 1), new Vec3d(size, sizeEnd, 1), Direction.WEST, 0, 0, tSize, 16, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), Direction.SOUTH, 0, 0, 16, 16, sprite, format));
		}

		return quads;
	}

	@Override
	public ItemOverrideList getOverrides(){
		return null;
	}

	@Override
	public boolean isAmbientOcclusion(){
		return false;
	}

	@Override
	public boolean isGui3d(){
		return false;
	}

	@Override
	public boolean isBuiltInRenderer(){
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture(){
		//Due to shared conduit baked model, the breaking particles have to be shared within a chunk.
		//They all use the same texture for breaking particles. This could be fixed by giving each variant its own model, but RAM usage would greatly increase.
		return bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/heatcable/wool-copper"));
	}
}
