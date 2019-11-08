package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
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

public class PrototypeBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Crossroads.MODID, "prototype");

	protected PrototypeBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
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

		if(extendedBlockState.get(CRProperties.PORT_TYPE) == null){
			return Collections.emptyList();
		}

		TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
		for(int i = 0; i < 6; i++){
			sprite[i] = extendedBlockState.get(CRProperties.PORT_TYPE)[i] == null ? bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/prototype/blank")) : bakedTextureGetter.apply(PrototypePortTypes.values()[extendedBlockState.get(CRProperties.PORT_TYPE)[i]].getTexture());
		}

		quads.add(ModelUtil.createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), Direction.UP, sprite[1], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), Direction.DOWN, sprite[0], format));
		quads.add(ModelUtil.createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), Direction.EAST, sprite[5], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), Direction.WEST, sprite[4], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, 0), Direction.NORTH, sprite[2], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), Direction.SOUTH, sprite[3], format));

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
		return bakedTextureGetter.apply(new ResourceLocation(Crossroads.MODID, "blocks/prototype/blank"));
	}
}
