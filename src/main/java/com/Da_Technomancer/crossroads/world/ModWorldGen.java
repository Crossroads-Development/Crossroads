package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import javax.annotation.Nullable;
import java.util.Random;

public class ModWorldGen implements IWorldGenerator{

	/**
	 * Two arguments are marked @Nullable not due to being null in normal use, but because they are null when called during retrogen
	 */
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, @Nullable IChunkGenerator chunkGenerator, @Nullable IChunkProvider chunkProvider){

		switch(world.provider.getDimension()){
			case 0: // Overworld
				if(ModConfig.genCopperOre.getBoolean()){
					runGenerator(new WorldGenMinable(OreSetup.oreCopper.getDefaultState(), 18), world, random, chunkX, chunkZ, 2, 0, 30);
				}
				if(ModConfig.genTinOre.getBoolean()){
					runGenerator(new WorldGenMinable(OreSetup.oreTin.getDefaultState(), 1), world, random, chunkX, chunkZ, 20, 0, 30);
				}
				break;
			case -1: // Nether
				if(ModConfig.genRubyOre.getBoolean()){
					// The reason the spawn attempts is so high for rubies is that it can only generate in quartz ore.
					// The average number of quartz ore per chunk divided by the number of blockspaces in the given height range (heights nether quartz spawns at) is about 1/350, so 1000 tries will give an average of about 1 ruby per chunk.
					// Happy Mining!
					runGenerator(new GemGen(OreSetup.oreRuby.getDefaultState(), BlockMatcher.forBlock(Blocks.QUARTZ_ORE)), world, random, chunkX, chunkZ, 1000, 8, 116);
				}
				break;
			case 1://End
				if(ModConfig.genVoidOre.getBoolean() && (chunkX > 40 || chunkX < -40 || chunkZ > 40 || chunkZ < -40)){
					runGenerator(new GemGen(OreSetup.oreVoid.getDefaultState(), BlockMatcher.forBlock(Blocks.END_STONE)), world, random, chunkX, chunkZ, 10, 5, 80);
				}
				break;
		}
	}

	private static void runGenerator(WorldGenerator generator, World world, Random rand, int chunkX, int chunkZ, int chancesToSpawn, int minHeight, int maxHeight){
		if(minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
			throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");

		int heightDiff = maxHeight - minHeight + 1;
		for(int i = 0; i < chancesToSpawn; i++){
			generator.generate(world, rand, new BlockPos(chunkX * 16 + rand.nextInt(16), minHeight + rand.nextInt(heightDiff), chunkZ * 16 + rand.nextInt(16)));
		}
	}
}
