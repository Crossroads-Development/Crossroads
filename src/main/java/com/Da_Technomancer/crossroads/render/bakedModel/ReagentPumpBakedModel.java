package com.Da_Technomancer.crossroads.render.bakedModel;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
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

public class ReagentPumpBakedModel implements IBakedModel{

	public static final ModelResourceLocation BAKED_MODEL = new ModelResourceLocation(Main.MODID, "reagent_pump");

	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	//Cached quad lists
	private final ArrayList<BakedQuad> coreUp = new ArrayList<>(6);
	private final ArrayList<BakedQuad> coreDown = new ArrayList<>(6);

	private final double SIZE_CORE = 4D / 16D;
	private final double SIZE = 5D / 16D;

	protected ReagentPumpBakedModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		this.bakedTextureGetter = bakedTextureGetter;
		this.format = format;

		TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/block_bronze"));

		coreUp.add(ModelUtil.createQuad(new Vec3d(1 - SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, SIZE_CORE, SIZE_CORE), EnumFacing.DOWN, sprite, format));
		coreUp.add(ModelUtil.createQuad(new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), EnumFacing.UP, sprite, format));
		//Top and bottom faces are shared
		coreDown.addAll(coreUp);

		sprite = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/reag_pump_middle"));
		coreUp.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), EnumFacing.NORTH, 0, 16, 16, 0, sprite, format));
		coreUp.add(ModelUtil.createQuad(new Vec3d(1 - SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), EnumFacing.SOUTH, 0, 16, 16, 0, sprite, format));
		coreUp.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), new Vec3d(SIZE_CORE, SIZE_CORE, SIZE_CORE), EnumFacing.WEST, 0, 16, 16, 0, sprite, format));
		coreUp.add(ModelUtil.createQuad(new Vec3d(1 - SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), EnumFacing.EAST, 0, 16, 16, 0, sprite, format));

		coreDown.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), EnumFacing.NORTH, 0, 0, 16, 16, sprite, format));
		coreDown.add(ModelUtil.createQuad(new Vec3d(1 - SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), EnumFacing.SOUTH, 0, 0, 16, 16, sprite, format));
		coreDown.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), new Vec3d(SIZE_CORE, SIZE_CORE, SIZE_CORE), EnumFacing.WEST, 0, 0, 16, 16, sprite, format));
		coreDown.add(ModelUtil.createQuad(new Vec3d(1 - SIZE_CORE, SIZE_CORE, SIZE_CORE), new Vec3d(1 - SIZE_CORE, SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, 1 - SIZE_CORE), new Vec3d(1 - SIZE_CORE, 1 - SIZE_CORE, SIZE_CORE), EnumFacing.EAST, 0, 0, 16, 16, sprite, format));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		if(side != null || state == null){
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Boolean[] connectMode = extendedBlockState.getValue(Properties.CONNECT);
		boolean crystal = state.getBlock() == ModBlocks.reagentPumpCrystal;
		boolean up = state.getValue(Properties.ACTIVE);

		if(up){
			quads.addAll(coreUp);
		}else{
			quads.addAll(coreDown);
		}

		if(connectMode == null){
			return quads;
		}

		//TODO

		String mat = crystal ? "cryst_" : "glass_";
		
		TextureAtlasSprite spriteIn = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/alch_tube/" + mat + "tube_in"));
		TextureAtlasSprite spriteOut = bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/alch_tube/" + mat + "tube_out"));

		TextureAtlasSprite active = spriteIn;
		
		if(connectMode[1]){
			if(up){
				active = spriteOut;
			}
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, 1, 1 - SIZE), new Vec3d(1 - SIZE, 1 - SIZE_CORE, 1 - SIZE), new Vec3d(1 - SIZE, 1 - SIZE_CORE, SIZE), new Vec3d(1 - SIZE, 1, SIZE), EnumFacing.EAST, 0, 0, 16, 16, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, 1, SIZE), new Vec3d(SIZE, 1 - SIZE_CORE, SIZE), new Vec3d(SIZE, 1 - SIZE_CORE, 1 - SIZE), new Vec3d(SIZE, 1, 1 - SIZE), EnumFacing.WEST, 0, 0, 16, 16, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, 1 - SIZE_CORE, SIZE), new Vec3d(SIZE, 1, SIZE), new Vec3d(1 - SIZE, 1, SIZE), new Vec3d(1 - SIZE, 1 - SIZE_CORE, SIZE), EnumFacing.NORTH, 16, 16, 0, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, 1 - SIZE_CORE, 1 - SIZE), new Vec3d(1 - SIZE, 1, 1 - SIZE), new Vec3d(SIZE, 1, 1 - SIZE), new Vec3d(SIZE, 1 - SIZE_CORE, 1 - SIZE), EnumFacing.SOUTH, 16, 16, 0, 0, active, format));
			active = spriteIn;
		}

		if(connectMode[0]){
			if(!up){
				active = spriteOut;
			}
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, SIZE_CORE, 1 - SIZE), new Vec3d(1 - SIZE, 0, 1 - SIZE), new Vec3d(1 - SIZE, 0, SIZE), new Vec3d(1 - SIZE, SIZE_CORE, SIZE), EnumFacing.EAST, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, SIZE_CORE, SIZE), new Vec3d(SIZE, 0, SIZE), new Vec3d(SIZE, 0, 1 - SIZE), new Vec3d(SIZE, SIZE_CORE, 1 - SIZE), EnumFacing.WEST, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, SIZE_CORE, SIZE), new Vec3d(1 - SIZE, 0, SIZE), new Vec3d(SIZE, 0, SIZE), new Vec3d(SIZE, SIZE_CORE, SIZE), EnumFacing.NORTH, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, SIZE_CORE, 1 - SIZE), new Vec3d(SIZE, 0, 1 - SIZE), new Vec3d(1 - SIZE, 0, 1 - SIZE), new Vec3d(1 - SIZE, SIZE_CORE, 1 - SIZE), EnumFacing.SOUTH, 0, 16, 16, 0, active, format));
			active = spriteIn;
		}

		if(connectMode[5]){
			quads.add(ModelUtil.createQuad(new Vec3d(1, 1 - SIZE, SIZE), new Vec3d(1 - SIZE_CORE, 1 - SIZE, SIZE), new Vec3d(1 - SIZE_CORE, 1 - SIZE, 1 - SIZE), new Vec3d(1, 1 - SIZE, 1 - SIZE), EnumFacing.UP, 0, 0, 16, 16, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1, SIZE, 1 - SIZE), new Vec3d(1 - SIZE, SIZE, 1 - SIZE), new Vec3d(1 - SIZE, SIZE, SIZE), new Vec3d(1, SIZE, SIZE), EnumFacing.DOWN, 0, 0, 16, 16, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1, SIZE, SIZE), new Vec3d(1 - SIZE_CORE, SIZE, SIZE), new Vec3d(1 - SIZE_CORE, 1 - SIZE, SIZE), new Vec3d(1, 1 - SIZE, SIZE), EnumFacing.NORTH, 0, 0, 16, 16, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1, 1 - SIZE, 1 - SIZE), new Vec3d(1 - SIZE_CORE, 1 - SIZE, 1 - SIZE), new Vec3d(1 - SIZE_CORE, SIZE, 1 - SIZE), new Vec3d(1, SIZE, 1 - SIZE), EnumFacing.SOUTH, 0, 0, 16, 16, active, format));
		}

		if(connectMode[4]){
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, 1 - SIZE, SIZE), new Vec3d(0, 1 - SIZE, SIZE), new Vec3d(0, 1 - SIZE, 1 - SIZE), new Vec3d(SIZE_CORE, 1 - SIZE, 1 - SIZE), EnumFacing.UP, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, SIZE, 1 - SIZE), new Vec3d(0, SIZE, 1 - SIZE), new Vec3d(0, SIZE, SIZE), new Vec3d(SIZE_CORE, SIZE, SIZE), EnumFacing.DOWN, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, SIZE, SIZE), new Vec3d(0, SIZE, SIZE), new Vec3d(0, 1 - SIZE, SIZE), new Vec3d(SIZE_CORE, 1 - SIZE, SIZE), EnumFacing.NORTH, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE_CORE, 1 - SIZE, 1 - SIZE), new Vec3d(0, 1 - SIZE, 1 - SIZE), new Vec3d(0, SIZE, 1 - SIZE), new Vec3d(SIZE_CORE, SIZE, 1 - SIZE), EnumFacing.SOUTH, 0, 16, 16, 0, active, format));
		}

		if(connectMode[2]){
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, 1 - SIZE, SIZE_CORE), new Vec3d(1 - SIZE, 1 - SIZE, 0), new Vec3d(SIZE, 1 - SIZE, 0), new Vec3d(SIZE, 1 - SIZE, SIZE_CORE), EnumFacing.UP, 16, 16, 0, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, SIZE, SIZE_CORE), new Vec3d(SIZE, SIZE, 0), new Vec3d(1 - SIZE, SIZE, 0), new Vec3d(1 - SIZE, SIZE, SIZE_CORE), EnumFacing.DOWN, 16, 16, 0, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, SIZE, SIZE_CORE), new Vec3d(1 - SIZE, SIZE, 0), new Vec3d(1 - SIZE, 1 - SIZE, 0), new Vec3d(1 - SIZE, 1 - SIZE, SIZE_CORE), EnumFacing.EAST, 16, 16, 0, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, 1 - SIZE, SIZE_CORE), new Vec3d(SIZE, 1 - SIZE, 0), new Vec3d(SIZE, SIZE, 0), new Vec3d(SIZE, SIZE, SIZE_CORE), EnumFacing.WEST, 16, 16, 0, 0, active, format));
		}
		
		if(connectMode[3]){
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, 1 - SIZE, 1 - SIZE_CORE), new Vec3d(SIZE, 1 - SIZE, 1), new Vec3d(1 - SIZE, 1 - SIZE, 1), new Vec3d(1 - SIZE, 1 - SIZE, 1 - SIZE_CORE), EnumFacing.UP, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, SIZE, 1 - SIZE_CORE), new Vec3d(1 - SIZE, SIZE, 1), new Vec3d(SIZE, SIZE, 1), new Vec3d(SIZE, SIZE, 1 - SIZE_CORE), EnumFacing.DOWN, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(1 - SIZE, 1 - SIZE_CORE, 1 - SIZE), new Vec3d(1 - SIZE, 1 - SIZE, 1), new Vec3d(1 - SIZE, SIZE, 1), new Vec3d(1 - SIZE, SIZE, 1 - SIZE_CORE), EnumFacing.EAST, 0, 16, 16, 0, active, format));
			quads.add(ModelUtil.createQuad(new Vec3d(SIZE, SIZE, 1 - SIZE_CORE), new Vec3d(SIZE, SIZE, 1), new Vec3d(SIZE, 1 - SIZE, 1), new Vec3d(SIZE, 1 - SIZE, 1 - SIZE_CORE), EnumFacing.WEST, 0, 16, 16, 0, active, format));
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
		return bakedTextureGetter.apply(new ResourceLocation(Main.MODID, "blocks/reag_pump_middle"));
	}
}
