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

public class AdvConduitBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "adv_conduit");

	protected AdvConduitBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
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

		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

		Integer[] connectMode = extendedBlockState.getValue(Properties.CONNECT_MODE);

		if(connectMode == null){
			return Collections.emptyList();
		}

		TextureAtlasSprite spriteCap = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, 0));
		
		double size = ((IAdvConduitModel) state.getBlock()).getSize();
		int tSize = (int) (16D * size / (1D - (2D * size)));

		if(connectMode[1] != 0){
			TextureAtlasSprite spriteUp = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[1]));
			quads.add(createQuad(new Vec3d(1 - size, 1, size), new Vec3d(1 - size, 1, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, size), spriteUp, tSize, 0, 0, 16, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, 1, 1 - size), new Vec3d(size, 1, size), new Vec3d(size, 1 - size, size), new Vec3d(size, 1 - size, 1 - size), spriteUp, tSize, 0, 0, 16, EnumFacing.WEST));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(size, 1 - size, size), new Vec3d(size, 1, size), new Vec3d(1 - size, 1, size), spriteUp, 0, 16, tSize, 0, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1, 1 - size), new Vec3d(size, 1, 1 - size), spriteUp, 0, 16, tSize, 0, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, size), new Vec3d(size, 1 - size, size), spriteCap, EnumFacing.UP));
		}

		if(connectMode[0] != 0){
			TextureAtlasSprite spriteDown = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[0]));
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 0, 1 - size), new Vec3d(1 - size, 0, size), spriteDown, 0, 0, tSize, 16, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(size, size, size), new Vec3d(size, 0, size), new Vec3d(size, 0, 1 - size), spriteDown, 0, 0, tSize, 16, EnumFacing.WEST));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, 0, size), new Vec3d(size, 0, size), spriteDown, 0, 0, tSize, 16, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(size, size, 1 - size), new Vec3d(size, 0, 1 - size), new Vec3d(1 - size, 0, 1 - size), spriteDown, 0, 0, tSize, 16, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 1 - size), new Vec3d(size, size, 1 - size), spriteCap, EnumFacing.DOWN));
		}

		if(connectMode[5] != 0){
			TextureAtlasSprite spriteEast = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[5]));
			quads.add(createQuad(new Vec3d(1, 1 - size, 1 - size), new Vec3d(1, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 1 - size), spriteEast, tSize, 0, 0, 16, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(1, size, size), new Vec3d(1, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, size, size), spriteEast, tSize, 0, 0, 16, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(1, 1 - size, size), new Vec3d(1, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, 1 - size, size), spriteEast, tSize, 0, 0, 16, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(1, size, 1 - size), new Vec3d(1, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, size, 1 - size), spriteEast, tSize, 0, 0, 16, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, size, 1 - size), spriteCap, EnumFacing.EAST));
		}

		if(connectMode[4] != 0){
			TextureAtlasSprite spriteWest = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[4]));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, size), new Vec3d(0, 1 - size, size), new Vec3d(0, 1 - size, 1 - size), spriteWest, tSize, 16, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(size, size, 1 - size), new Vec3d(0, size, 1 - size), new Vec3d(0, size, size), spriteWest, tSize, 16, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(size, size, size), new Vec3d(0, size, size), new Vec3d(0, 1 - size, size), spriteWest, tSize, 16, EnumFacing.NORTH));
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(0, 1 - size, 1 - size), new Vec3d(0, size, 1 - size), spriteWest, tSize, 16, EnumFacing.SOUTH));
		}else{
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, size), new Vec3d(size, size, size), spriteCap, EnumFacing.WEST));
		}

		if(connectMode[2] != 0){
			TextureAtlasSprite spriteNorth = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[2]));
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 0), new Vec3d(size, 1 - size, 0), spriteNorth, 0, 16, tSize, 0, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(size, size, size), new Vec3d(size, size, 0), new Vec3d(1 - size, size, 0), spriteNorth, 0, 16, tSize, 0, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 0), new Vec3d(1 - size, 1 - size, 0), spriteNorth, 0, 16, tSize, 0, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(size, 1 - size, size), new Vec3d(size, 1 - size, 0), new Vec3d(size, size, 0), spriteNorth, 0, 16, tSize, 0, EnumFacing.WEST));
		}else{
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, size, size), new Vec3d(size, size, size), spriteCap, EnumFacing.NORTH));
		}
		if(connectMode[3] != 0){
			TextureAtlasSprite spriteSouth = bakedTextureGetter.apply(((IAdvConduitModel) state.getBlock()).getTexture(state, connectMode[3]));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1), new Vec3d(1 - size, 1 - size, 1), spriteSouth, tSize, 16, EnumFacing.UP));
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, size, 1), new Vec3d(size, size, 1), spriteSouth, tSize, 16, EnumFacing.DOWN));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1), new Vec3d(1 - size, size, 1), spriteSouth, tSize, 16, EnumFacing.EAST));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, size, 1 - size), new Vec3d(size, size, 1), new Vec3d(size, 1 - size, 1), spriteSouth, tSize, 16, EnumFacing.WEST));
		}else{
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), spriteCap, EnumFacing.SOUTH));
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
		return bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/alch_tube/glass_tube_cap"));
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms(){
		return ItemCameraTransforms.DEFAULT;
	}
}
