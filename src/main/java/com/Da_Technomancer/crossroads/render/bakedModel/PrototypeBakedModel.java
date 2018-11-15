package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
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

public class PrototypeBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "prototype");

	protected PrototypeBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;
		this.format = format;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		if(side != null || state == null){
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

		if(extendedBlockState.getValue(Properties.PORT_TYPE) == null){
			return Collections.emptyList();
		}

		TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
		for(int i = 0; i < 6; i++){
			sprite[i] = extendedBlockState.getValue(Properties.PORT_TYPE)[i] == null ? bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/prototype/blank")) : bakedTextureGetter.apply(PrototypePortTypes.values()[extendedBlockState.getValue(Properties.PORT_TYPE)[i]].getTexture());
		}

		quads.add(ModelUtil.createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), EnumFacing.UP, sprite[1], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), EnumFacing.DOWN, sprite[0], format));
		quads.add(ModelUtil.createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), EnumFacing.EAST, sprite[5], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), EnumFacing.WEST, sprite[4], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, 0), EnumFacing.NORTH, sprite[2], format));
		quads.add(ModelUtil.createQuad(new Vec3d(0, 0, 1), new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), EnumFacing.SOUTH, sprite[3], format));

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
		return bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/prototype/blank"));
	}
}
