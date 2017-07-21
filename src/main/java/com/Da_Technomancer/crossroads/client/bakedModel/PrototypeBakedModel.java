package com.Da_Technomancer.crossroads.client.bakedModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;

public class PrototypeBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "prototype");

	protected PrototypeBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;
		this.format = format;
	}

	private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite){
		for(int e = 0; e < format.getElementCount(); e++){
			switch(format.getElement(e).getUsage()){
				case POSITION:
					builder.put(e, (float) x, (float) y, (float) z, 1.0f);
					break;
				case COLOR:
					builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
					break;
				case UV:
					if(format.getElement(e).getIndex() == 0){
						u = sprite.getInterpolatedU(u);
						v = sprite.getInterpolatedV(v);
						builder.put(e, u, v, 0f, 1f);
						break;
					}
				case NORMAL:
					builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
					break;
				default:
					builder.put(e);
					break;
			}
		}
	}

	private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, EnumFacing side){
		return createQuad(v1, v2, v3, v4, sprite, 16, 16, side);
	}

	private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int u, int v, EnumFacing side){
		Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2));

		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setQuadOrientation(side);
		builder.setTexture(sprite);
		//Not needed as this is a full block. builder.setApplyDiffuseLighting(false);
		putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite);
		putVertex(builder, normal, v2.x, v2.y, v2.z, 0, v, sprite);
		putVertex(builder, normal, v3.x, v3.y, v3.z, u, v, sprite);
		putVertex(builder, normal, v4.x, v4.y, v4.z, u, 0, sprite);
		return builder.build();
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

		quads.add(createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), sprite[1], EnumFacing.UP));
		quads.add(createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), sprite[0], EnumFacing.DOWN));
		quads.add(createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), sprite[5], EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), sprite[4], EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, 0), sprite[2], EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(0, 0, 1), new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), sprite[3], EnumFacing.SOUTH));

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

	@Override
	public ItemCameraTransforms getItemCameraTransforms(){
		return ItemCameraTransforms.DEFAULT;
	}
}
