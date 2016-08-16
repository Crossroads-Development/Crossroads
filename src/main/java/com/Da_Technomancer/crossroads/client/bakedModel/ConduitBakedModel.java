package com.Da_Technomancer.crossroads.client.bakedModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.IConduitModel;
import com.Da_Technomancer.crossroads.API.Properties;
import com.google.common.base.Function;

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

	private TextureAtlasSprite sprite;
	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID + ":conduit");

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
					builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
					break;
				default:
					builder.put(e);
					break;
			}
		}
	}

	private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite){
		Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));

		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setTexture(sprite);
		putVertex(builder, normal, v1.xCoord, v1.yCoord, v1.zCoord, 0, 0, sprite);
		putVertex(builder, normal, v2.xCoord, v2.yCoord, v2.zCoord, 0, 16, sprite);
		putVertex(builder, normal, v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, sprite);
		putVertex(builder, normal, v4.xCoord, v4.yCoord, v4.zCoord, 16, 0, sprite);
		return builder.build();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){

		if(side != null || state == null){
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();

		sprite = bakedTextureGetter.apply(((IConduitModel) state.getBlock()).getTexture());

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

		if(up){
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1, size), new Vec3d(1 - size, 1, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), sprite));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1, 1 - size), new Vec3d(size, 1, size), new Vec3d(size, 1 - size, size), sprite));
			quads.add(createQuad(new Vec3d(size, 1, size), new Vec3d(1 - size, 1, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(size, 1 - size, size), sprite));
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1, 1 - size), new Vec3d(size, 1, 1 - size), sprite));
		}else{
			quads.add(createQuad(new Vec3d(size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, size), new Vec3d(size, 1 - size, size), sprite));
		}

		if(down){
			quads.add(createQuad(new Vec3d(1 - size, 0, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 0, 1 - size), sprite));
			quads.add(createQuad(new Vec3d(size, 0, 1 - size), new Vec3d(size, size, 1 - size), new Vec3d(size, size, size), new Vec3d(size, 0, size), sprite));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, 0, size), new Vec3d(size, 0, size), sprite));
			quads.add(createQuad(new Vec3d(size, 0, 1 - size), new Vec3d(1 - size, 0, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(size, size, 1 - size), sprite));
		}else{
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(1 - size, size, size), new Vec3d(1 - size, size, 1 - size), new Vec3d(size, size, 1 - size), sprite));
		}

		if(east){
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1, 1 - size, 1 - size), new Vec3d(1, 1 - size, size), new Vec3d(1 - size, 1 - size, size), sprite));
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(1, size, size), new Vec3d(1, size, 1 - size), new Vec3d(1 - size, size, 1 - size), sprite));
			quads.add(createQuad(new Vec3d(1 - size, 1 - size, size), new Vec3d(1, 1 - size, size), new Vec3d(1, size, size), new Vec3d(1 - size, size, size), sprite));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(1, size, 1 - size), new Vec3d(1, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), sprite));
		}else{
			quads.add(createQuad(new Vec3d(1 - size, size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, size, 1 - size), sprite));
		}

		if(west){
			quads.add(createQuad(new Vec3d(0, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, size), new Vec3d(0, 1 - size, size), sprite));
			quads.add(createQuad(new Vec3d(0, size, size), new Vec3d(size, size, size), new Vec3d(size, size, 1 - size), new Vec3d(0, size, 1 - size), sprite));
			quads.add(createQuad(new Vec3d(0, 1 - size, size), new Vec3d(size, 1 - size, size), new Vec3d(size, size, size), new Vec3d(0, size, size), sprite));
			quads.add(createQuad(new Vec3d(0, size, 1 - size), new Vec3d(size, size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(0, 1 - size, 1 - size), sprite));
		}else{
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, 1 - size, size), new Vec3d(size, size, size), sprite));
		}

		if(north){
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, 1 - size, 0), new Vec3d(size, 1 - size, 0), sprite));
			quads.add(createQuad(new Vec3d(size, size, 0), new Vec3d(1 - size, size, 0), new Vec3d(1 - size, size, size), new Vec3d(size, size, size), sprite));
			quads.add(createQuad(new Vec3d(1 - size, size, 0), new Vec3d(1 - size, 1 - size, 0), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, size, size), sprite));
			quads.add(createQuad(new Vec3d(size, size, size), new Vec3d(size, 1 - size, size), new Vec3d(size, 1 - size, 0), new Vec3d(size, size, 0), sprite));
		}else{
			quads.add(createQuad(new Vec3d(size, 1 - size, size), new Vec3d(1 - size, 1 - size, size), new Vec3d(1 - size, size, size), new Vec3d(size, size, size), sprite));
		}
		if(south){
			quads.add(createQuad(new Vec3d(size, 1 - size, 1), new Vec3d(1 - size, 1 - size, 1), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), sprite));
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, size, 1), new Vec3d(size, size, 1), sprite));
			quads.add(createQuad(new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(1 - size, 1 - size, 1), new Vec3d(1 - size, size, 1), sprite));
			quads.add(createQuad(new Vec3d(size, size, 1), new Vec3d(size, 1 - size, 1), new Vec3d(size, 1 - size, 1 - size), new Vec3d(size, size, 1 - size), sprite));
		}else{
			quads.add(createQuad(new Vec3d(size, size, 1 - size), new Vec3d(1 - size, size, 1 - size), new Vec3d(1 - size, 1 - size, 1 - size), new Vec3d(size, 1 - size, 1 - size), sprite));
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
		return sprite;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms(){
		return ItemCameraTransforms.DEFAULT;
	}
}
