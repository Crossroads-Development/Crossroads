package com.Da_Technomancer.crossroads.world;

import java.util.Random;

import com.Da_Technomancer.crossroads.ModConfig;

import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class ModWorldGen implements IWorldGenerator{

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider){

		switch(world.provider.getDimension()){
			case 0: // Overworld
				if(ModConfig.genCopperOre.getBoolean())
					this.runGenerator(new WorldGenMinable(Block.getBlockFromName("crossroads:oreCopper").getDefaultState(), 8), world, random, chunkX, chunkZ, 8, 2, 30);

				if(ModConfig.genTinOre.getBoolean())
					this.runGenerator(new WorldGenMinable(Block.getBlockFromName("crossroads:oreTin").getDefaultState(), 4), world, random, chunkX, chunkZ, 5, 2, 30);

				if(ModConfig.genNativeCopperOre.getBoolean())
					this.runGenerator(new WorldGenMinable(Block.getBlockFromName("crossroads:oreNativeCopper").getDefaultState(), 6), world, random, chunkX, chunkZ, 8, 50, 70);

				break;
			case -1: // Nether
				if(ModConfig.genRubyOre.getBoolean())
					// The reason the spawn attempts is so high for rubies is
					// that it can only generate in quartz ore. The average
					// number of quartz ore per chunk divided by the number of
					// blockspaces in the given height range (heights nether
					// quartz spawns at) is about 1/350, so 1000 tries will give
					// an average of about 1 ruby per chunk. Happy Mining!
					this.runGenerator(new SingleBlockGen(Block.getBlockFromName("crossroads:oreRuby").getDefaultState(), BlockMatcher.forBlock(Blocks.QUARTZ_ORE)), world, random, chunkX, chunkZ, 1000, 8, 116);

				break;
			case 1: // End
				break;
		}
	}

	private void runGenerator(WorldGenerator generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight){
		if(minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
			throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");

		int heightDiff = maxHeight - minHeight + 1;
		for(int i = 0; i < chancesToSpawn; i++){
			int x = chunk_X * 16 + rand.nextInt(16);
			int y = minHeight + rand.nextInt(heightDiff);
			int z = chunk_Z * 16 + rand.nextInt(16);
			generator.generate(world, rand, new BlockPos(x, y, z));
		}
	}
}
