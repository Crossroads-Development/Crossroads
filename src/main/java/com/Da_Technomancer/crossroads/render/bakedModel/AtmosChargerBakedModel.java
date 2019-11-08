package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.Crossroads;
import com.google.common.collect.ImmutableList;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class AtmosChargerBakedModel implements IBakedModel{

	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Crossroads.MODID, "atmos_charger");
	private final ArrayList<BakedQuad> quads = new ArrayList<>();
	private final HashMap<Direction, List<BakedQuad>> inactiveBase = new HashMap<>(6);
	private final HashMap<Direction, List<BakedQuad>> activeBase = new HashMap<>(6);

	protected AtmosChargerBakedModel(VertexFormat vf, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;

		TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/block_cast_iron"));

		quads.add(ModelUtil.createQuad(new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.25D, 4, 0.25D), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.75D, 1, 0.75D), new Vec3d(0.75D, 1, 0.25D), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.25D, 4, 0.25D), new Vec3d(0.25D, 1, 0.25D), new Vec3d(0.25D, 1, 0.75D), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.25D, 4, 0.25D), new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.75D, 1, 0.25D), new Vec3d(0.25D, 1, 0.25D), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.25D, 1, 0.75D), new Vec3d(0.75D, 1, 0.75D), Direction.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.375), new Vec3d(-1, 4.25D, 0.375), new Vec3d(-1, 4.25D, 0.625D), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4, 0.375), new Vec3d(2, 4, 0.625D), new Vec3d(-1, 4, 0.625D), new Vec3d(-1, 4, 0.375), Direction.DOWN, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4, 0.375), new Vec3d(2, 4.25D, 0.375), new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4, 0.625D), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-1, 4, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 4.25D, 0.375), new Vec3d(-1, 4, 0.375), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4.25D, 0.375), new Vec3d(2, 4, 0.375), new Vec3d(-1, 4, 0.375), new Vec3d(-1, 4.25D, 0.375), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 4, 0.625D), new Vec3d(2, 4.25D, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 4, 0.625D), Direction.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(0.375, 4.249D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.375, 4.249D, -1), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.375, 4.001D, 2), new Vec3d(0.375, 4.001D, -1), new Vec3d(0.625D, 4.001D, -1), Direction.DOWN, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.375, 4.249D, 2), new Vec3d(0.375, 4.001D, 2), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375, 4.001D, -1), new Vec3d(0.375, 4.249D, -1), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.625D, 4.001D, -1), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375, 4.001D, 2), new Vec3d(0.375, 4.249D, 2), new Vec3d(0.375, 4.249D, -1), new Vec3d(0.375, 4.001D, -1), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.625D, 4.001D, -1), new Vec3d(0.625D, 4.249D, -1), Direction.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(-1, 6, 0.375D), new Vec3d(-1, 6, 0.625D), new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-0.75D, 6, 0.375D), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-1, 6, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-0.75D, 4.25D, 0.625D), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-1, 6, 0.375D), new Vec3d(-0.75D, 6, 0.375D), new Vec3d(-0.75D, 4.25D, 0.375D), new Vec3d(-1, 4.25D, 0.375D), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-1, 4.25D, 0.375D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 6, 0.625D), new Vec3d(-1, 6, 0.375D), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(-0.75D, 6, 0.375D), new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-0.75D, 4.25D, 0.625D), new Vec3d(-0.75D, 4.25D, 0.375D), Direction.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(1.75D, 6, 0.375D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(2, 6, 0.625D), new Vec3d(2, 6, 0.375D), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 6, 0.625D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(1.75D, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.625D), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(1.75D, 6, 0.375D), new Vec3d(2, 6, 0.375D), new Vec3d(2, 4.25D, 0.375D), new Vec3d(1.75D, 4.25D, 0.375D), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(1.75D, 4.25D, 0.375D), new Vec3d(1.75D, 4.25D, 0.625D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(1.75D, 6, 0.375D), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(2, 6, 0.375D), new Vec3d(2, 6, 0.625D), new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.375D), Direction.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, 2), new Vec3d(0.625D, 6, 2), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.375D, 6, 1.75D), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.249D, 1.75D), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.625D, 6, 2), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, 1.75D), new Vec3d(0.375D, 4.249D, 2), new Vec3d(0.375D, 6, 2), new Vec3d(0.375D, 6, 1.75D), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, 1.75D), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.625D, 4.249D, 1.75D), new Vec3d(0.375D, 4.249D, 1.75D), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 6, 2), new Vec3d(0.375D, 6, 2), Direction.SOUTH, sprite, vf));

		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, -0.75D), new Vec3d(0.625D, 6, -0.75D), new Vec3d(0.625D, 6, -1), new Vec3d(0.375D, 6, -1), Direction.UP, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.625D, 4.249D, -0.75D), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.625D, 6, -1), new Vec3d(0.625D, 6, -0.75D), Direction.EAST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, -1), new Vec3d(0.375D, 4.249D, -0.75D), new Vec3d(0.375D, 6, -0.75D), new Vec3d(0.375D, 6, -1), Direction.WEST, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 6, -1), new Vec3d(0.625D, 6, -1), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.375D, 4.249D, -1), Direction.NORTH, sprite, vf));
		quads.add(ModelUtil.createQuad(new Vec3d(0.375D, 4.249D, -0.75D), new Vec3d(0.625D, 4.249D, -0.75D), new Vec3d(0.625D, 6, -0.75D), new Vec3d(0.375D, 6, -0.75D), Direction.SOUTH, sprite, vf));

		TextureAtlasSprite spriteBase = bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/tesla_in"));

		inactiveBase.put(Direction.UP, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), Direction.UP, spriteBase, vf)));
		inactiveBase.put(Direction.DOWN, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), Direction.DOWN, spriteBase, vf)));
		inactiveBase.put(Direction.EAST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), Direction.EAST, spriteBase, vf)));
		inactiveBase.put(Direction.WEST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), Direction.WEST, spriteBase, vf)));
		inactiveBase.put(Direction.NORTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), Direction.NORTH, spriteBase, vf)));
		inactiveBase.put(Direction.SOUTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), Direction.SOUTH, spriteBase, vf)));

		spriteBase = bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/tesla_out"));

		activeBase.put(Direction.UP, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), Direction.UP, spriteBase, vf)));
		activeBase.put(Direction.DOWN, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), Direction.DOWN, spriteBase, vf)));
		activeBase.put(Direction.EAST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), Direction.EAST, spriteBase, vf)));
		activeBase.put(Direction.WEST, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), Direction.WEST, spriteBase, vf)));
		activeBase.put(Direction.NORTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), Direction.NORTH, spriteBase, vf)));
		activeBase.put(Direction.SOUTH, ImmutableList.of(ModelUtil.createQuad(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), Direction.SOUTH, spriteBase, vf)));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand){
		if(state == null){
			return Collections.emptyList();
		}
		return side == null ? quads : state.get(CRProperties.ACTIVE) ? activeBase.get(side) : inactiveBase.get(side);
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
		return bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/block_cast_iron"));
	}
}
