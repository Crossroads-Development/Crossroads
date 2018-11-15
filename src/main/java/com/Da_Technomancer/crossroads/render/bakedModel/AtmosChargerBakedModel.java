package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.ImmutableList;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class AtmosChargerBakedModel implements IBakedModel{

	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "atmos_charger");
	private final ArrayList<BakedQuad> quads = new ArrayList<>();
	private final HashMap<EnumFacing, List<BakedQuad>> inactiveBase = new HashMap<>(6);
	private final HashMap<EnumFacing, List<BakedQuad>> activeBase = new HashMap<>(6);

	protected AtmosChargerBakedModel(VertexFormat vf, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;

		TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/block_cast_iron"));

		quads.add(ModelUtil.createQuad(new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.25D, 4, 0.25D), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.75D, 1, 0.75D), new Vec3d(0.75D, 1, 0.25D), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.25D, 4, 0.25D), new Vec3d(0.25D, 1, 0.25D), new Vec3d(0.25D, 1, 0.75D), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.25D, 4, 0.25D), new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.75D, 1, 0.25D), new Vec3d(0.25D, 1, 0.25D), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.25D, 1, 0.75D), new Vec3d(0.75D, 1, 0.75D), EnumFacing.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.375), new Vec3d(-1, 4.25D, 0.375), new Vec3d(-1, 4.25D, 0.625D), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4, 0.375), new Vec3d(2, 4, 0.625D), new Vec3d(-1, 4, 0.625D), new Vec3d(-1, 4, 0.375), EnumFacing.DOWN, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4, 0.375), new Vec3d(2, 4.25D, 0.375), new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4, 0.625D), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-1, 4, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 4.25D, 0.375), new Vec3d(-1, 4, 0.375), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4.25D, 0.375), new Vec3d(2, 4, 0.375), new Vec3d(-1, 4, 0.375), new Vec3d(-1, 4.25D, 0.375), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4, 0.625D), new Vec3d(2, 4.25D, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 4, 0.625D), EnumFacing.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(0.375, 4.249D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.375, 4.249D, -1), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.375, 4.001D, 2), new Vec3d(0.375, 4.001D, -1), new Vec3d(0.625D, 4.001D, -1), EnumFacing.DOWN, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.375, 4.249D, 2), new Vec3d(0.375, 4.001D, 2), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375, 4.001D, -1), new Vec3d(0.375, 4.249D, -1), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.625D, 4.001D, -1), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375, 4.001D, 2), new Vec3d(0.375, 4.249D, 2), new Vec3d(0.375, 4.249D, -1), new Vec3d(0.375, 4.001D, -1), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.625D, 4.001D, -1), new Vec3d(0.625D, 4.249D, -1), EnumFacing.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(-1, 6, 0.375D), new Vec3d(-1, 6, 0.625D), new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-0.75D, 6, 0.375D), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-1, 6, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-0.75D, 4.25D, 0.625D), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-1, 6, 0.375D), new Vec3d(-0.75D, 6, 0.375D), new Vec3d(-0.75D, 4.25D, 0.375D), new Vec3d(-1, 4.25D, 0.375D), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-1, 4.25D, 0.375D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 6, 0.625D), new Vec3d(-1, 6, 0.375D), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-0.75D, 6, 0.375D), new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-0.75D, 4.25D, 0.625D), new Vec3d(-0.75D, 4.25D, 0.375D), EnumFacing.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(1.75D, 6, 0.375D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(2, 6, 0.625D), new Vec3d(2, 6, 0.375D), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 6, 0.625D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(1.75D, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.625D), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(1.75D, 6, 0.375D), new Vec3d(2, 6, 0.375D), new Vec3d(2, 4.25D, 0.375D), new Vec3d(1.75D, 4.25D, 0.375D), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(1.75D, 4.25D, 0.375D), new Vec3d(1.75D, 4.25D, 0.625D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(1.75D, 6, 0.375D), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 6, 0.375D), new Vec3d(2, 6, 0.625D), new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.375D), EnumFacing.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, 2), new Vec3d(0.625D, 6, 2), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.375D, 6, 1.75D), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.249D, 1.75D), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.625D, 6, 2), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, 1.75D), new Vec3d(0.375D, 4.249D, 2), new Vec3d(0.375D, 6, 2), new Vec3d(0.375D, 6, 1.75D), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, 1.75D), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.625D, 4.249D, 1.75D), new Vec3d(0.375D, 4.249D, 1.75D), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 6, 2), new Vec3d(0.375D, 6, 2), EnumFacing.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, -0.75D), new Vec3d(0.625D, 6, -0.75D), new Vec3d(0.625D, 6, -1), new Vec3d(0.375D, 6, -1), EnumFacing.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.249D, -0.75D), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.625D, 6, -1), new Vec3d(0.625D, 6, -0.75D), EnumFacing.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, -1), new Vec3d(0.375D, 4.249D, -0.75D), new Vec3d(0.375D, 6, -0.75D), new Vec3d(0.375D, 6, -1), EnumFacing.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, -1), new Vec3d(0.625D, 6, -1), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.375D, 4.249D, -1), EnumFacing.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, -0.75D), new Vec3d(0.625D, 4.249D, -0.75D), new Vec3d(0.625D, 6, -0.75D), new Vec3d(0.375D, 6, -0.75D), EnumFacing.SOUTH, sprite, vf));

		TextureAtlasSprite spriteBase = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/tesla_in"));

		inactiveBase.put(EnumFacing.UP, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), EnumFacing.UP, spriteBase, vf)));
		inactiveBase.put(EnumFacing.DOWN, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), EnumFacing.DOWN, spriteBase, vf)));
		inactiveBase.put(EnumFacing.EAST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), EnumFacing.EAST, spriteBase, vf)));
		inactiveBase.put(EnumFacing.WEST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), EnumFacing.WEST, spriteBase, vf)));
		inactiveBase.put(EnumFacing.NORTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), EnumFacing.NORTH, spriteBase, vf)));
		inactiveBase.put(EnumFacing.SOUTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), EnumFacing.SOUTH, spriteBase, vf)));

		spriteBase = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/tesla_out"));

		activeBase.put(EnumFacing.UP, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), EnumFacing.UP, spriteBase, vf)));
		activeBase.put(EnumFacing.DOWN, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), EnumFacing.DOWN, spriteBase, vf)));
		activeBase.put(EnumFacing.EAST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), EnumFacing.EAST, spriteBase, vf)));
		activeBase.put(EnumFacing.WEST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), EnumFacing.WEST, spriteBase, vf)));
		activeBase.put(EnumFacing.NORTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), EnumFacing.NORTH, spriteBase, vf)));
		activeBase.put(EnumFacing.SOUTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), EnumFacing.SOUTH, spriteBase, vf)));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		if(state == null){
			return Collections.emptyList();
		}
		return side == null ? quads : state.getValue(Properties.ACTIVE) ? activeBase.get(side) : inactiveBase.get(side);
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
		return bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/block_cast_iron"));
	}
}
