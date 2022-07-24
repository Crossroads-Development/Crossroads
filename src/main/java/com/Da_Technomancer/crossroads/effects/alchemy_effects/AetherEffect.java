package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReactiveSpotTileEntity;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class AetherEffect implements IAlchEffect{

	private static final TagKey<Block> SOIL_GROUP = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "alchemy_soil"));
	private static final TagKey<Block> ROCK_GROUP = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "alchemy_rock"));
	private static final TagKey<Block> FLUD_GROUP = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "alchemy_fluid"));//Was going to be named FLUID_GROUP, but the other two fields had the same name lengths and I couldn't resist
	private static final TagKey<Block> CRYS_GROUP = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "alchemy_crystal"));
	private static final TagKey<Block> WOOD_GROUP = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "alchemy_wood"));
	private static final TagKey<Block> FOLI_GROUP = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "alchemy_foliage"));

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

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents){
		BlockState oldState = world.getBlockState(pos);

		//sulfur dioxide prevents biome changing
		if(contents.getQty(EnumReagents.SULFUR_DIOXIDE.id()) == 0){
			ResourceKey<Biome> biomeKey = biome();
			if(!biomeKey.location().equals(MiscUtil.getRegistryName(world.getBiome(pos).value(), ForgeRegistries.BIOMES))){
				setBiomeAtPos(world, pos, getBiomeHolder(biomeKey.location()));
				CRPackets.sendPacketToDimension(world, new SendBiomeUpdateToClient(pos, biomeKey.location()));
			}
		}

		//quicksilver makes it create a block instead of transmuting blocks
		if(contents.getQty(EnumReagents.QUICKSILVER.id()) != 0 && oldState.isAir()){
			world.setBlockAndUpdate(pos, soilBlock().defaultBlockState());
			return;
		}

		//cavorite prevents block transmutation
		if(oldState.isAir() || oldState.getDestroySpeed(world, pos) < 0 || contents.getQty(EnumReagents.CAVORITE.id()) != 0){
			return;
		}

		if(CraftingUtil.tagContains(CRYS_GROUP, oldState.getBlock())){
			if(oldState != crystalBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, crystalBlock().defaultBlockState());
			}
		}else if(CraftingUtil.tagContains(FLUD_GROUP, oldState.getBlock())){
			if(oldState != fluidBlock().defaultBlockState() && oldState.getBlock() != CRBlocks.reactiveSpot){
				world.setBlockAndUpdate(pos, CRBlocks.reactiveSpot.defaultBlockState());
				BlockEntity te = world.getBlockEntity(pos);
				if(te instanceof ReactiveSpotTileEntity){
					((ReactiveSpotTileEntity) te).setTarget(fluidBlock().defaultBlockState());
				}
			}
		}else if(CraftingUtil.tagContains(ROCK_GROUP, oldState.getBlock())){
			if(oldState != rockBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, rockBlock().defaultBlockState());
			}
		}else if(CraftingUtil.tagContains(SOIL_GROUP, oldState.getBlock())){
			//Special case for grass vs dirt
			BlockPos upPos = pos.above();
			if((soilBlock() == Blocks.GRASS_BLOCK || soilBlock() == Blocks.MYCELIUM) && !world.getBlockState(upPos).isAir()){
				if(oldState != Blocks.DIRT.defaultBlockState()){
					world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
				}
			}else if(oldState != soilBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, soilBlock().defaultBlockState());
			}
		}else if(CraftingUtil.tagContains(WOOD_GROUP, oldState.getBlock())){
			if(oldState != woodBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, woodBlock().defaultBlockState());
			}
		}else if(CraftingUtil.tagContains(FOLI_GROUP, oldState.getBlock())){
			if(oldState != foliageBlock().defaultBlockState()){
				world.setBlockAndUpdate(pos, foliageBlock().defaultBlockState());
			}
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.terraform_plains");
	}

	@Nullable
	public static Holder<Biome> getBiomeHolder(ResourceLocation registryID){
		return ForgeRegistries.BIOMES.getHolder(registryID).orElse(null);
	}

	/**
	 * Sets the biome at a position in a way that will be saved to disk
	 * Does not handle packets, should be called on both sides
	 *
	 * @param world The world
	 * @param pos The world position to set the biome at
	 * @param biome The biome to set it to. Will do nothing if null.
	 */
	public static void setBiomeAtPos(Level world, BlockPos pos, Holder<Biome> biome){
		if(biome == null){
			return;
		}

		try{
			ChunkAccess chunk = world.getChunk(pos);
			int[] noisePos = noiseBiomeCoords(world, chunk, pos.getX(), pos.getY(), pos.getZ());
			LevelChunkSection chunkSection = chunk.getSection(noisePos[3]);
			PalettedContainerRO<Holder<Biome>> paletteContainerRO = chunkSection.getBiomes();
			if(paletteContainerRO instanceof PalettedContainer<Holder<Biome>> pcont){
				pcont.set(noisePos[0], noisePos[1], noisePos[2], biome);
			}else{
				Crossroads.logger.error(String.format("Failed to set biome at pos: %s; invalid palette container type %s", pos, paletteContainerRO));
			}
		}catch(Exception e){
			e.printStackTrace();
			Crossroads.logger.error(String.format("Failed to set biome at pos: %s; to biome: %s", pos, biome), e);
		}
	}

	private static final Field BIOME_SEED = ReflectionUtil.reflectField(CRReflection.BIOME_SEED);

	private static int[] noiseBiomeCoords(Level world, ChunkAccess chunk, int x, int y, int z){
		//Return values are: biome x, biome y, biome z, section index
		//There's a weird formula to convert between world coordinates and the coordinates used to store biomes
		//Based on BiomeManager::getBiome

		long biomeZoomSeed = 0;
		if(BIOME_SEED != null){
			try{
				biomeZoomSeed = (long) BIOME_SEED.get(world.getBiomeManager());
			}catch(IllegalAccessException | ClassCastException e){
				e.printStackTrace();
				//We can proceed without the seed- however, all biome placement positions will be slightly inaccurate
			}
		}

		int i = x - 2;
		int j = y - 2;
		int k = z - 2;
		int l = i >> 2;
		int i1 = j >> 2;
		int j1 = k >> 2;
		double d0 = (double) (i & 3) / 4.0D;
		double d1 = (double) (j & 3) / 4.0D;
		double d2 = (double) (k & 3) / 4.0D;
		int k1 = 0;
		double d3 = Double.POSITIVE_INFINITY;

		for(int l1 = 0; l1 < 8; ++l1){
			boolean flag = (l1 & 4) == 0;
			boolean flag1 = (l1 & 2) == 0;
			boolean flag2 = (l1 & 1) == 0;
			int i2 = flag ? l : l + 1;
			int j2 = flag1 ? i1 : i1 + 1;
			int k2 = flag2 ? j1 : j1 + 1;
			double d4 = flag ? d0 : d0 - 1.0D;
			double d5 = flag1 ? d1 : d1 - 1.0D;
			double d6 = flag2 ? d2 : d2 - 1.0D;
			double d7 = getFiddledDistance(biomeZoomSeed, i2, j2, k2, d4, d5, d6);
			if(d3 > d7){
				k1 = l1;
				d3 = d7;
			}
		}

		int adjustedX = (k1 & 4) == 0 ? l : l + 1;
		int adjustedY = (k1 & 2) == 0 ? i1 : i1 + 1;
		int adjustedZ = (k1 & 1) == 0 ? j1 : j1 + 1;

		//This last set of operations is based on ChunkAccess::getNoiseBiome

		int lowerClamp = QuartPos.fromBlock(chunk.getMinBuildHeight());
		int upperClamp = lowerClamp + QuartPos.fromBlock(chunk.getHeight()) - 1;
		int biomeY = Mth.clamp(adjustedY, lowerClamp, upperClamp);
		int sectionIndex = chunk.getSectionIndex(QuartPos.toBlock(biomeY));

		return new int[] {adjustedX & 3, biomeY & 3, adjustedZ & 3, sectionIndex};
	}

	private static double getFiddledDistance(long p_186680_, int p_186681_, int p_186682_, int p_186683_, double p_186684_, double p_186685_, double p_186686_){
		//Copied from BiomeManager (original is private)
		long $$7 = LinearCongruentialGenerator.next(p_186680_, (long) p_186681_);
		$$7 = LinearCongruentialGenerator.next($$7, (long) p_186682_);
		$$7 = LinearCongruentialGenerator.next($$7, (long) p_186683_);
		$$7 = LinearCongruentialGenerator.next($$7, (long) p_186681_);
		$$7 = LinearCongruentialGenerator.next($$7, (long) p_186682_);
		$$7 = LinearCongruentialGenerator.next($$7, (long) p_186683_);
		double d0 = getFiddle($$7);
		$$7 = LinearCongruentialGenerator.next($$7, p_186680_);
		double d1 = getFiddle($$7);
		$$7 = LinearCongruentialGenerator.next($$7, p_186680_);
		double d2 = getFiddle($$7);
		return Mth.square(p_186686_ + d2) + Mth.square(p_186685_ + d1) + Mth.square(p_186684_ + d0);
	}

	private static double getFiddle(long p_186690_){
		//Copied from BiomeManager (original is private)
		double d0 = (double) Math.floorMod(p_186690_ >> 24, 1024) / 1024.0D;
		return (d0 - 0.5D) * 0.9D;
	}
}
