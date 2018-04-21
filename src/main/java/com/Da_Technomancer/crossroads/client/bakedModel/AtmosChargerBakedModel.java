package com.Da_Technomancer.crossroads.client.bakedModel;

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
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AtmosChargerBakedModel implements IBakedModel{

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "atmos_charger");

	protected AtmosChargerBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
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

		TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/block_cast_iron"));
		TextureAtlasSprite spriteBase = bakedTextureGetter.apply(state.getValue(Properties.ACTIVE) ? new ResourceLocation(Main.MODID, "blocks/tesla_out") : new ResourceLocation(Main.MODID, "blocks/tesla_in"));

		quads.add(createQuad(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0), spriteBase, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), spriteBase, EnumFacing.DOWN));
		quads.add(createQuad(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), spriteBase, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), spriteBase, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), spriteBase, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), spriteBase, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.25D, 4, 0.25D), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.75D, 1, 0.75D), new Vec3d(0.75D, 1, 0.25D), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.25D, 4, 0.25D), new Vec3d(0.25D, 1, 0.25D), new Vec3d(0.25D, 1, 0.75D), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(0.25D, 4, 0.25D), new Vec3d(0.75D, 4, 0.25D), new Vec3d(0.75D, 1, 0.25D), new Vec3d(0.25D, 1, 0.25D), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(0.75D, 4, 0.75D), new Vec3d(0.25D, 4, 0.75D), new Vec3d(0.25D, 1, 0.75D), new Vec3d(0.75D, 1, 0.75D), sprite, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.375), new Vec3d(-1, 4.25D, 0.375), new Vec3d(-1, 4.25D, 0.625D), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(2, 4, 0.375), new Vec3d(2, 4, 0.625D), new Vec3d(-1, 4, 0.625D), new Vec3d(-1, 4, 0.375), sprite, EnumFacing.DOWN));
		quads.add(createQuad(new Vec3d(2, 4, 0.375), new Vec3d(2, 4.25D, 0.375), new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4, 0.625D), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(-1, 4, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 4.25D, 0.375), new Vec3d(-1, 4, 0.375), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(2, 4.25D, 0.375), new Vec3d(2, 4, 0.375), new Vec3d(-1, 4, 0.375), new Vec3d(-1, 4.25D, 0.375), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(2, 4, 0.625D), new Vec3d(2, 4.25D, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 4, 0.625D), sprite, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(0.375, 4.249D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.375, 4.249D, -1), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.375, 4.001D, 2), new Vec3d(0.375, 4.001D, -1), new Vec3d(0.625D, 4.001D, -1), sprite, EnumFacing.DOWN));
		quads.add(createQuad(new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.375, 4.249D, 2), new Vec3d(0.375, 4.001D, 2), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(0.375, 4.001D, -1), new Vec3d(0.375, 4.249D, -1), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.625D, 4.001D, -1), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(0.375, 4.001D, 2), new Vec3d(0.375, 4.249D, 2), new Vec3d(0.375, 4.249D, -1), new Vec3d(0.375, 4.001D, -1), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.001D, 2), new Vec3d(0.625D, 4.001D, -1), new Vec3d(0.625D, 4.249D, -1), sprite, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(-1, 6, 0.375D), new Vec3d(-1, 6, 0.625D), new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-0.75D, 6, 0.375D), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-1, 6, 0.625D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-0.75D, 4.25D, 0.625D), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(-1, 6, 0.375D), new Vec3d(-0.75D, 6, 0.375D), new Vec3d(-0.75D, 4.25D, 0.375D), new Vec3d(-1, 4.25D, 0.375D), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(-1, 4.25D, 0.375D), new Vec3d(-1, 4.25D, 0.625D), new Vec3d(-1, 6, 0.625D), new Vec3d(-1, 6, 0.375D), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(-0.75D, 6, 0.375D), new Vec3d(-0.75D, 6, 0.625D), new Vec3d(-0.75D, 4.25D, 0.625D), new Vec3d(-0.75D, 4.25D, 0.375D), sprite, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(1.75D, 6, 0.375D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(2, 6, 0.625D), new Vec3d(2, 6, 0.375D), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(2, 6, 0.625D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(1.75D, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.625D), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(1.75D, 6, 0.375D), new Vec3d(2, 6, 0.375D), new Vec3d(2, 4.25D, 0.375D), new Vec3d(1.75D, 4.25D, 0.375D), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(1.75D, 4.25D, 0.375D), new Vec3d(1.75D, 4.25D, 0.625D), new Vec3d(1.75D, 6, 0.625D), new Vec3d(1.75D, 6, 0.375D), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(2, 6, 0.375D), new Vec3d(2, 6, 0.625D), new Vec3d(2, 4.25D, 0.625D), new Vec3d(2, 4.25D, 0.375D), sprite, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(0.375D, 6, 2), new Vec3d(0.625D, 6, 2), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.375D, 6, 1.75D), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 4.249D, 1.75D), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.625D, 6, 2), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(0.375D, 4.249D, 1.75D), new Vec3d(0.375D, 4.249D, 2), new Vec3d(0.375D, 6, 2), new Vec3d(0.375D, 6, 1.75D), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(0.375D, 6, 1.75D), new Vec3d(0.625D, 6, 1.75D), new Vec3d(0.625D, 4.249D, 1.75D), new Vec3d(0.375D, 4.249D, 1.75D), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(0.375D, 4.249D, 2), new Vec3d(0.625D, 4.249D, 2), new Vec3d(0.625D, 6, 2), new Vec3d(0.375D, 6, 2), sprite, EnumFacing.SOUTH));

		quads.add(createQuad(new Vec3d(0.375D, 6, -0.75D), new Vec3d(0.625D, 6, -0.75D), new Vec3d(0.625D, 6, -1), new Vec3d(0.375D, 6, -1), sprite, EnumFacing.UP));
		quads.add(createQuad(new Vec3d(0.625D, 4.249D, -0.75D), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.625D, 6, -1), new Vec3d(0.625D, 6, -0.75D), sprite, EnumFacing.EAST));
		quads.add(createQuad(new Vec3d(0.375D, 4.249D, -1), new Vec3d(0.375D, 4.249D, -0.75D), new Vec3d(0.375D, 6, -0.75D), new Vec3d(0.375D, 6, -1), sprite, EnumFacing.WEST));
		quads.add(createQuad(new Vec3d(0.375D, 6, -1), new Vec3d(0.625D, 6, -1), new Vec3d(0.625D, 4.249D, -1), new Vec3d(0.375D, 4.249D, -1), sprite, EnumFacing.NORTH));
		quads.add(createQuad(new Vec3d(0.375D, 4.249D, -0.75D), new Vec3d(0.625D, 4.249D, -0.75D), new Vec3d(0.625D, 6, -0.75D), new Vec3d(0.375D, 6, -0.75D), sprite, EnumFacing.SOUTH));

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
		return bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/block_cast_iron"));
	}
}
