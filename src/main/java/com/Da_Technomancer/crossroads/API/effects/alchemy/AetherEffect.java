package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class AetherEffect implements IAlchEffect{

	private static final Tag<Block> SOIL_GROUP = BlockTags.bind(Crossroads.MODID + ":alchemy_soil");
	private static final Tag<Block> ROCK_GROUP = BlockTags.bind(Crossroads.MODID + ":alchemy_rock");
	private static final Tag<Block> FLUD_GROUP = BlockTags.bind(Crossroads.MODID + ":alchemy_fluid");//Was going to be named FLUID_GROUP, but the other two fields had the same name lengths and I couldn't resist
	private static final Tag<Block> CRYS_GROUP = BlockTags.bind(Crossroads.MODID + ":alchemy_crystal");
	private static final Tag<Block> WOOD_GROUP = BlockTags.bind(Crossroads.MODID + ":alchemy_wood");
	private static final Tag<Block> FOLI_GROUP = BlockTags.bind(Crossroads.MODID + ":alchemy_foliage");
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

	protected Block woodBlock(){
		return Blocks.OAK_LOG;
	}

	protected Block foliageBlock(){
		return Blocks.OAK_LEAVES;
	}

	protected ResourceKey<Biome> biome(){
		return Biomes.PLAINS;
	}

	@Nullable
	public static Biome lookupBiome(ResourceKey<Biome> biomeKey, CommonLevelAccessor world){
		//Gets the biome associated with a key
		return world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(biomeKey);
	}

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents){
		BlockState oldState = world.getBlockState(pos);

		//quicksilver makes it create a block instead of transmuting blocks
		if(contents.getQty(EnumReagents.QUICKSILVER.id()) != 0 && oldState.isAir()){
			world.setBlockAndUpdate(pos, soilBlock().defaultBlockState());
			return;
		}

		//sulfur dioxide prevents biome changing
		if(contents.getQty(EnumReagents.SULFUR_DIOXIDE.id()) == 0){
			ResourceKey<Biome> biomeKey = biome();
			Biome biome = lookupBiome(biomeKey, world);
			if(biome != null && world.getBiome(pos) != biome){
				setBiomeAtPos(world, pos, biome);
				CRPackets.sendPacketToDimension(world, new SendBiomeUpdateToClient(pos, biomeKey.location()));
			}
		}

		//cavorite prevents block transmutation
		if(oldState.isAir() || oldState.getDestroySpeed(world, pos) < 0 || contents.getQty(EnumReagents.CAVORITE.id()) != 0){
			return;
		}

		if(CRYS_GROUP.contains(oldState.getBlock())){
			if(oldState != crystalBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, crystalBlock().defaultBlockState());
			}
		}else if(FLUD_GROUP.contains(oldState.getBlock())){
			if(oldState != fluidBlock().defaultBlockState() && oldState.getBlock() != CRBlocks.reactiveSpot){
				world.setBlockAndUpdate(pos, CRBlocks.reactiveSpot.defaultBlockState());
				BlockEntity te = world.getBlockEntity(pos);
				if(te instanceof ReactiveSpotTileEntity){
					((ReactiveSpotTileEntity) te).setTarget(fluidBlock().defaultBlockState());
				}
			}
		}else if(ROCK_GROUP.contains(oldState.getBlock())){
			if(oldState != rockBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, rockBlock().defaultBlockState());
			}
		}else if(SOIL_GROUP.contains(oldState.getBlock())){
			//Special case for grass vs dirt
			BlockPos upPos = pos.above();
			if((soilBlock() == Blocks.GRASS_BLOCK || soilBlock() == Blocks.MYCELIUM) && !world.getBlockState(upPos).isAir()){
				if(oldState != Blocks.DIRT.defaultBlockState()){
					world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
				}
			}else if(oldState != soilBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, soilBlock().defaultBlockState());
			}
		}else if(WOOD_GROUP.contains(oldState.getBlock())){
			if(oldState != woodBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, woodBlock().defaultBlockState());
			}
		}else if(FOLI_GROUP.contains(oldState.getBlock())){
			if(oldState != foliageBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, foliageBlock().defaultBlockState());
			}
		}
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_plains");
	}

	/**
	 * Sets the biome at a position in a way that will be saved to disk
	 * Does not handle packets, should be called on both sides
	 * @param world The world
	 * @param pos The position to set the position of. Y-coord is irrelevant, will set the biome in an entire column
	 * @param biome The biome to set it to
	 */
	public static void setBiomeAtPos(Level world, BlockPos pos, Biome biome){
		if(biome == null){
			return;
		}

		//As of MC1.15, we have to reflect in as the biome array is private and the int array won't save to disk
		ChunkBiomeContainer bc = world.getChunk(pos).getBiomes();
		if(biomeField != null && bc != null){
			Object o;
			try{
				o = biomeField.get(bc);
				Biome[] biomeArray = (Biome[]) o;
				if(CRConfig.verticalBiomes.get()){
					int y = 0;
					do{
						//We set the biome in a column from bedrock to world height
						biomeArray[getBiomeIndex(world, pos.getX(), y, pos.getZ())] = biome;
					}while(!world.isOutsideBuildHeight(++y));
				}else{
					biomeArray[getBiomeIndex(world, pos.getX(), pos.getY(), pos.getZ())] = biome;
				}
			}catch(IllegalAccessException | NullPointerException | IndexOutOfBoundsException e){
				e.printStackTrace();
				Crossroads.logger.error(String.format("Failed to set biome at pos: %s; to biome: %s", pos, biome), e);
			}
		}
	}

	//Copied from BiomeContainer to copy its biome array ordering
	private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;

	/**
	 * Vanilla biomes are packed into a one-dimensional array which stores all biome information for a chunk
	 * This method gets the index corresponding to a world position in the chunk's biome array
	 * Multiple positions will share an index
	 * @param x The x position
	 * @param y The y position- does actually matter
	 * @param z The z position
	 * @return The biome index at this position
	 */
	private static int getBiomeIndex(Level world, int x, int y, int z){
		//Based off of ChunkBiomeContainer::getNoiseBiome
		final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
		final int quartMinY = QuartPos.fromBlock(world.getMinBuildHeight());
		final int quartHeight = QuartPos.fromBlock(world.getHeight()) - 1;

		int pX = QuartPos.fromBlock(x);
		int pY = QuartPos.fromBlock(y);
		int pZ = QuartPos.fromBlock(z);
		int i = pX & HORIZONTAL_MASK;
		int j = Mth.clamp(pY - quartMinY, 0, quartHeight);
		int k = pZ & HORIZONTAL_MASK;
		return j << WIDTH_BITS + WIDTH_BITS | k << WIDTH_BITS | i;
	}
}
