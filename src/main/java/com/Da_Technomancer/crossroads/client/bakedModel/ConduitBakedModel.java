package com.Da_Technomancer.crossroads.client.bakedModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;

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

public class ConduitBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "conduit");

	protected ConduitBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
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
					builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0F);
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
		return createQuad(v1, v2, v3, v4, sprite, 0, 0, u, v, side);
	}
	
	private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int uStart, int vStart, int uEnd, int vEnd, EnumFacing side){
		Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2));

		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setQuadOrientation(side);
		builder.setTexture(sprite);
		builder.setApplyDiffuseLighting(false);
		putVertex(builder, normal, v1.x, v1.y, v1.z, uStart, vStart, sprite);
		putVertex(builder, normal, v2.x, v2.y, v2.z, uStart, vEnd, sprite);
		putVertex(builder, normal, v3.x, v3.y, v3.z, uEnd, vEnd, sprite);
		putVertex(builder, normal, v4.x, v4.y, v4.z, uEnd, vStart, sprite);
		return builder.build();
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

		Boolean north = extendedBlockState.getValue(Properties.CONNECT)[2];
		Boolean south = extendedBlockState.getValue(Properties.CONNECT)[3];
		Boolean west = extendedBlockState.getValue(Properties.CONNECT)[4];
		Boolean east = extendedBlockState.getValue(Properties.CONNECT)[5];
		Boolean up = extendedBlockState.getValue(Properties.CONNECT)[1];
		Boolean down = extendedBlockState.getValue(Properties.CONNECT)[0];
		double size = ((IConduitModel) state.getBlock()).getSize();
		int tSize = (int) (16D * size / (1D - (2D * size)));
		
		if(up){
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1, size), new Vec3d(1 - size, 1, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), sprite, 16, 0, 0, tSize, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1, 1 - size), new Vec3d(size, 1, size), new Vec3d(size, 1 - size, size), sprite, 16, 0, 0, tSize, EnumFacing.WEST));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(size, 1 - size, size), new Vec3d(size, 1, size), new Vec3d(1 - size, 1, size), sprite, 0, 16, tSize, 0, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1, 1 - size), new Vec3d(size, 1, 1 - size), sprite, 0, 16, tSize, 0, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, size), new Vec3d(size, 1 - size, size), sprite, EnumFacing.UP));
		}

		if(down){
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 0, 1 - size), new Vec3d(1 - size, 0, size), new Vec3d(1 - size, size, size), sprite, 16, tSize, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(size, 0, size), new Vec3d(size, 0, 1 - size), new Vec3d(size, size, 1 - size), sprite, 16, tSize, EnumFacing.WEST));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, 0, size), new Vec3d(size, 0, size), sprite, tSize, 16, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(size, size, 1 - size), new Vec3d(size, 0, 1 - size), new Vec3d(1 - size, 0, 1 - size), sprite, tSize, 16, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 1 - size), new Vec3d(size, size, 1 - size), sprite, EnumFacing.DOWN));
		}

		if(east){
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1, 1 - size, 1 - size), new Vec3d(1, 1 - size, size), new Vec3d(1 - size, 1 - size, size), sprite, 16, 0, 0, tSize, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(1, size, size), new Vec3d(1, size, 1 - size), new Vec3d(1 - size, size, 1 - size), sprite, 16, 0, 0, tSize, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(1, 1 - size, size), new Vec3d(1, size, size), new Vec3d(1 - size, size, size), sprite, 16, 0, 0, tSize, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(1, size, 1 - size), new Vec3d(1, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), sprite, 16, 0, 0, tSize, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, size, 1 - size), sprite, EnumFacing.EAST));
		}

		if(west){
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(0, 1 - size, size), new Vec3d(0, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), sprite, 16, tSize, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(0, size, 1 - size), new Vec3d(0, size, size), new Vec3d(size, size, size), sprite, 16, tSize, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(0, size, size), new Vec3d(0, 1 - size, size), new Vec3d(size, 1 - size, size), sprite, 16, tSize, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(0, 1 - size, 1 - size), new Vec3d(0, size, 1 - size), new Vec3d(size, size, 1 - size), sprite, 16, tSize, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, size), new Vec3d(size, size, size), sprite, EnumFacing.WEST));
		}

		if(north){
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 0), new Vec3d(size, 1 - size, 0), sprite, 0, 16, tSize, 0, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(size, size, size), new Vec3d(size, size, 0), new Vec3d(1 - size, size, 0), sprite, 0, 16, tSize, 0, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 0), new Vec3d(1 - size, 1 - size, 0), sprite, 0, 16, tSize, 0, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(size, 1 - size, size), new Vec3d(size, 1 - size, 0), new Vec3d(size, size, 0), sprite, 0, 16, tSize, 0, EnumFacing.WEST));
		}else{
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, size, size), new Vec3d(size, size, size), sprite, EnumFacing.NORTH));
		}
		if(south){
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1), new Vec3d(1 - size, 1 - size, 1), sprite, tSize, 16, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, size, 1), new Vec3d(size, size, 1), sprite, tSize, 16, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1), new Vec3d(1 - size, size, 1), sprite, tSize, 16, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, size, 1 - size), new Vec3d(size, size, 1), new Vec3d(size, 1 - size, 1), sprite, tSize, 16, EnumFacing.WEST));
		}else{
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), sprite, EnumFacing.SOUTH));
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

	@Override
	public ItemCameraTransforms getItemCameraTransforms(){
		return ItemCameraTransforms.DEFAULT;
	}
}
