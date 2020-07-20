package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;

import java.lang.reflect.Field;

public class AetherEffect implements IAlchEffect{

	private static final ITag<Block> SOIL_GROUP = BlockTags.makeWrapperTag(Crossroads.MODID + ":alchemy_soil");
	private static final ITag<Block> ROCK_GROUP = BlockTags.makeWrapperTag(Crossroads.MODID + ":alchemy_rock");
	private static final ITag<Block> FLUD_GROUP = BlockTags.makeWrapperTag(Crossroads.MODID + ":alchemy_fluid");//Was going to be named FLUID_GROUP, but the other two fields had the same name lengths and I couldn't resist
	private static final ITag<Block> CRYS_GROUP = BlockTags.makeWrapperTag(Crossroads.MODID + ":alchemy_crystal");
	private static final Field biomeField = ReflectionUtil.reflectField(CRReflection.BIOME_ARRAY);

	protected Block soilBlock(){
		return Blocks.GRASS_BLOCK;
	}

	protected Block rockBlock(){
		return Blocks.STONE;
	}

	protected Block fluidBlock(){
		return Blocks.WATER;
	}

	protected Block crystalBlock(){
		return Blocks.GLASS;
	}

	protected Biome biome(){
		return Biomes.PLAINS;
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents){
		BlockState oldState = world.getBlockState(pos);

		//quicksilver makes it create a block instead of transmuting blocks
		if(contents.getQty(EnumReagents.QUICKSILVER.id()) != 0 && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, soilBlock().getDefaultState());
			return;
		}

		Biome biome = biome();
		if(world.getBiome(pos) != biome){
			setBiomeAtPos(world, pos, biome);
			CRPackets.sendPacketToDimension(world, new SendBiomeUpdateToClient(pos, biome.getRegistryName()));
		}

		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		if(CRYS_GROUP.contains(oldState.getBlock())){
			if(oldState != crystalBlock().getDefaultState()){
				world.setBlockState(pos, crystalBlock().getDefaultState());
			}
		}else if(FLUD_GROUP.contains(oldState.getBlock())){
			if(oldState != fluidBlock().getDefaultState() && oldState.getBlock() != CRBlocks.reactiveSpot){
				world.setBlockState(pos, CRBlocks.reactiveSpot.getDefaultState());
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof ReactiveSpotTileEntity){
					((ReactiveSpotTileEntity) te).setTarget(fluidBlock().getDefaultState());
				}
			}
		}else if(ROCK_GROUP.contains(oldState.getBlock())){
			if(oldState != rockBlock().getDefaultState()){
				world.setBlockState(pos, rockBlock().getDefaultState());
			}
		}else if(SOIL_GROUP.contains(oldState.getBlock())){
			//Special case for grass vs dirt
			BlockPos upPos = pos.up();
			if(soilBlock() == Blocks.GRASS_BLOCK && !world.getBlockState(upPos).isAir(world, upPos)){
				if(oldState != Blocks.DIRT.getDefaultState()){
					world.setBlockState(pos, Blocks.DIRT.getDefaultState());
				}
			}else if(oldState != soilBlock().getDefaultState()){
				world.setBlockState(pos, soilBlock().getDefaultState());
			}
		}
	}

	/**
	 * Sets the biome at a position in a way that will be saved to disk
	 * Does not handle packets, should be called on both sides
	 * @param world The world
	 * @param pos The position to set the position of. Y-coord is irrelevant
	 * @param biome The biome to set it to
	 */
	public static void setBiomeAtPos(World world, BlockPos pos, Biome biome){
		//As of MC1.15, we have to reflect in as the biome array is private and the int array won't save to disk
		BiomeContainer bc = world.getChunk(pos).getBiomes();
		int arrayIndex = (pos.getZ() & 15) << 4 | (pos.getX() & 15);
		if(biomeField != null){
			Object o;
			try{
				o = biomeField.get(bc);
				Biome[] biomeArray = (Biome[]) o;
				biomeArray[arrayIndex] = biome;
			}catch(IllegalAccessException | NullPointerException | IndexOutOfBoundsException e){
				e.printStackTrace();
				Crossroads.logger.error(String.format("Failed to set biome at pos: %s; to biome: %s", pos, biome), e);
			}
		}
	}
}
