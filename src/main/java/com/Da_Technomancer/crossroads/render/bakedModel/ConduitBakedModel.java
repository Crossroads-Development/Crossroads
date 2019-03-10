package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ConduitBakedModel implements IBakedModel{

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "conduit");

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	protected ConduitBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;
		this.format = format;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		if(side != null || state == null){
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();
		TextureAtlasSprite sprite = bakedTextureGetter.apply(((IConduitModel) state.getBlock()).getTexture(state));
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

		if(extendedBlockState.getValue(Properties.CONNECT) == null){
			return Collections.emptyList();
		}

		double size = ((IConduitModel) state.getBlock()).getSize();
		double sizeEnd = 1 - size;
		int tSize = (int) (16D * size / (1D - (2D * size)));
		
		if(extendedBlockState.getValue(Properties.CONNECT)[1]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, 1, size), new Vec3d(sizeEnd, 1, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), EnumFacing.EAST, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, 1, sizeEnd), new Vec3d(size, 1, size), new Vec3d(size, sizeEnd, size), EnumFacing.WEST, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(size, sizeEnd, size), new Vec3d(size, 1, size), new Vec3d(sizeEnd, 1, size), EnumFacing.NORTH, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, 1, sizeEnd), new Vec3d(size, 1, sizeEnd), EnumFacing.SOUTH, 0, 16, tSize, 0, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(size, sizeEnd, size), EnumFacing.UP, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.getValue(Properties.CONNECT)[0]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, 0, sizeEnd), new Vec3d(sizeEnd, 0, size), new Vec3d(sizeEnd, size, size), EnumFacing.EAST, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(size, 0, size), new Vec3d(size, 0, sizeEnd), new Vec3d(size, size, sizeEnd), EnumFacing.WEST, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, 0, size), new Vec3d(size, 0, size), EnumFacing.NORTH, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(size, size, sizeEnd), new Vec3d(size, 0, sizeEnd), new Vec3d(sizeEnd, 0, sizeEnd), EnumFacing.SOUTH, 0, 0, tSize, 16, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(size, size, sizeEnd), EnumFacing.DOWN, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.getValue(Properties.CONNECT)[5]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(1, sizeEnd, sizeEnd), new Vec3d(1, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), EnumFacing.UP, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(1, size, size), new Vec3d(1, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), EnumFacing.DOWN, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(1, sizeEnd, size), new Vec3d(1, size, size), new Vec3d(sizeEnd, size, size), EnumFacing.NORTH, 16, 0, 0, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(1, size, sizeEnd), new Vec3d(1, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), EnumFacing.SOUTH, 16, 0, 0, tSize, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), EnumFacing.EAST, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.getValue(Properties.CONNECT)[4]){
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(0, sizeEnd, size), new Vec3d(0, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), EnumFacing.UP, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(0, size, sizeEnd), new Vec3d(0, size, size), new Vec3d(size, size, size), EnumFacing.DOWN, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(0, size, size), new Vec3d(0, sizeEnd, size), new Vec3d(size, sizeEnd, size), EnumFacing.NORTH, 0, 0, 16, tSize, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(0, sizeEnd, sizeEnd), new Vec3d(0, size, sizeEnd), new Vec3d(size, size, sizeEnd), EnumFacing.SOUTH, 0, 0, 16, tSize, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, size), new Vec3d(size, size, size), EnumFacing.WEST, 0, 0, 16, 16, sprite, format));
		}

		if(extendedBlockState.getValue(Properties.CONNECT)[2]){
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, 0), new Vec3d(size, sizeEnd, 0), EnumFacing.UP, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, size), new Vec3d(size, size, size), new Vec3d(size, size, 0), new Vec3d(sizeEnd, size, 0), EnumFacing.DOWN, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, size, size), new Vec3d(sizeEnd, size, 0), new Vec3d(sizeEnd, sizeEnd, 0), EnumFacing.EAST, 0, 16, tSize, 0, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, size), new Vec3d(size, sizeEnd, size), new Vec3d(size, sizeEnd, 0), new Vec3d(size, size, 0), EnumFacing.WEST, 0, 16, tSize, 0, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, size), new Vec3d(sizeEnd, sizeEnd, size), new Vec3d(sizeEnd, size, size), new Vec3d(size, size, size), EnumFacing.NORTH, 0, 0, 16, 16, sprite, format));
		}
		if(extendedBlockState.getValue(Properties.CONNECT)[3]){
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, 1), new Vec3d(sizeEnd, sizeEnd, 1), EnumFacing.UP, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, size, 1), new Vec3d(size, size, 1), EnumFacing.DOWN, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(sizeEnd, sizeEnd, 1), new Vec3d(sizeEnd, size, 1), EnumFacing.EAST, 0, 0, tSize, 16, sprite, format));
			quads.add(ModelUtil.createQuad(new Vec3d(size, sizeEnd, sizeEnd), new Vec3d(size, size, sizeEnd), new Vec3d(size, size, 1), new Vec3d(size, sizeEnd, 1), EnumFacing.WEST, 0, 0, tSize, 16, sprite, format));
		}else{
			quads.add(ModelUtil.createQuad(new Vec3d(size, size, sizeEnd), new Vec3d(sizeEnd, size, sizeEnd), new Vec3d(sizeEnd, sizeEnd, sizeEnd), new Vec3d(size, sizeEnd, sizeEnd), EnumFacing.SOUTH, 0, 0, 16, 16, sprite, format));
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
		return bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/heatcable/wool-copper"));
	}
}
