package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.BakedQuad;
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

public class AdvConduitBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Crossroads.MODID, "adv_conduit");

	protected AdvConduitBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;
		this.format = format;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand){
		if(side != null || state == null){
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Integer[] connectMode = extendedBlockState.get(CrossroadsProperties.CONNECT_MODE);

		if(connectMode == null){
			return Collections.emptyList();
		}

		TextureAtlasSprite spriteCap = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, 0));
		
		double size = ((IAdvConduitModel) state.getBlock()).getSize();
		double sizeEnd = 1 - size;
		int tSize = (int) (16D * size / (1D - (2D * size)));

		if(connectMode[1] != 0){
			TextureAtlasSprite spriteUp = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[1]));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, 1, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, 1, size), Direction.EAST, 0, 0, 16, tSize, spriteUp, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, 1, size), new Vec3d(size, sizeEnd, size), new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, 1, sizeEnd), Direction.WEST, 0, 0, 16, tSize, spriteUp, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(size, 1, size), new Vec3d(sizeEnd, 1, size), new Vec3d(sizeEnd, sizeEnd, size), Direction.NORTH, 16, tSize, 0, 0, spriteUp, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, 1, sizeEnd), new Vec3d(size, 1, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), Direction.SOUTH, 16, tSize, 0, 0, spriteUp, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(size, sizeEnd, size), Direction.UP, 0, 0, 16, 16, spriteCap, format));
		}

		if(connectMode[0] != 0){
			TextureAtlasSprite spriteDown = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[0]));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, 0, sizeEnd), new Vec3d(sizeEnd, 0, size), new Vec3d(sizeEnd, size, size), Direction.EAST, 0, tSize, 16, 0, spriteDown, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(size, 0, size), new Vec3d(size, 0, sizeEnd), new Vec3d(size, size, sizeEnd), Direction.WEST, 0, tSize, 16, 0, spriteDown, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, 0, size), new Vec3d(size, 0, size), new Vec3d(size, size, size), Direction.NORTH, 0, tSize, 16, 0, spriteDown, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(size, 0, sizeEnd), new Vec3d(sizeEnd, 0, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), Direction.SOUTH, 0, tSize, 16, 0, spriteDown, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(size, size, sizeEnd), Direction.DOWN, 0, 0, 16, 16, spriteCap, format));
		}

		if(connectMode[5] != 0){
			TextureAtlasSprite spriteEast = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[5]));
			quads.add(ModelUtil.createQuad(new Vec3d(1, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(1, sizeEnd, sizeEnd), Direction.UP, 0, 0, 16, tSize, spriteEast, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, size, size), new Vec3d(1, size, size), Direction.DOWN, 0, 0, 16, tSize, spriteEast, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1, size, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(1, sizeEnd, size), Direction.NORTH, 0, 0, 16, tSize, spriteEast, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(1, size, sizeEnd), Direction.SOUTH, 0, 0, 16, tSize, spriteEast, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), Direction.EAST, 0, 0, 16, 16, spriteCap, format));
		}

		if(connectMode[4] != 0){
			TextureAtlasSprite spriteWest = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[4]));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(0, sizeEnd, size), new Vec3d(0, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), Direction.UP, 0, tSize, 16, 0, spriteWest, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(0, size, sizeEnd), new Vec3d(0, size, size), new Vec3d(size, size, size), Direction.DOWN, 0, tSize, 16, 0, spriteWest, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(0, size, size), new Vec3d(0, sizeEnd, size), new Vec3d(size, sizeEnd, size), Direction.NORTH, 0, tSize, 16, 0, spriteWest, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(0, sizeEnd, sizeEnd), new Vec3d(0, size, sizeEnd), new Vec3d(size, size, sizeEnd), Direction.SOUTH, 0, tSize, 16, 0, spriteWest, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, size), new Vec3d(size, size, size), Direction.WEST, 0, 0, 16, 16, spriteCap, format));
		}

		if(connectMode[2] != 0){
			TextureAtlasSprite spriteNorth = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[2]));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, 0), new Vec3d(size, sizeEnd, 0), new Vec3d(size, sizeEnd, size), Direction.UP, 16, tSize, 0, 0, spriteNorth, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(size, size, 0), new Vec3d(sizeEnd, size, 0), new Vec3d(sizeEnd, size, size), Direction.DOWN, 16, tSize, 0, 0, spriteNorth, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, size, 0), new Vec3d(sizeEnd, sizeEnd, 0), new Vec3d(sizeEnd, sizeEnd, size), Direction.EAST, 16, tSize, 0, 0, spriteNorth, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(size, sizeEnd, 0), new Vec3d(size, size, 0), new Vec3d(size, size, size), Direction.WEST, 16, tSize, 0, 0, spriteNorth, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, size, size), new Vec3d(size, size, size), Direction.NORTH, 0, 0, 16, 16, spriteCap, format));
		}
		if(connectMode[3] != 0){
			TextureAtlasSprite spriteSouth = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[3]));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, 1), new Vec3d(sizeEnd, sizeEnd, 1), new Vec3d(sizeEnd, sizeEnd, sizeEnd), Direction.UP, 0, tSize, 16, 0, spriteSouth, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, size, 1), new Vec3d(size, size, 1), new Vec3d(size, size, sizeEnd), Direction.DOWN, 0, tSize, 16, 0, spriteSouth, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, 1), new Vec3d(sizeEnd, size, 1), new Vec3d(sizeEnd, size, sizeEnd), Direction.EAST, 0, tSize, 16, 0, spriteSouth, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(size, size, 1), new Vec3d(size, sizeEnd, 1), new Vec3d(size, sizeEnd, sizeEnd), Direction.WEST, 0, tSize, 16, 0, spriteSouth, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), Direction.SOUTH, 0, 0, 16, 16, spriteCap, format));
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
		return bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/block_bronze"));
	}
}
