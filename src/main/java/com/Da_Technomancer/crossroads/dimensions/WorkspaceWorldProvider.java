package com.Da_Technomancer.crossroads.dimensions;

import java.util.List;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorkspaceWorldProvider extends Dimension{

	@Override
	public void init(){
		biomeProvider = new SingleBiomeProvider(Biomes.VOID);
		hasSkyLight = false;
	}

	@Override
	public DimensionType getDimensionType(){
		return ModDimensions.workspaceDimType;
	}

	@Override
	public boolean canRespawnHere(){
		return false;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks){
		return 0;
	}

	@Override
	public boolean canDoLightning(Chunk chunk){
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk){
		return false;
	}

	@Override
	public boolean isSurfaceWorld(){
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSkyColored(){
		return false;
	}

	@Override
	public ChunkGenerator createChunkGenerator(){
		return new EmptyGenerator(world);
	}

	private static class EmptyGenerator implements ChunkGenerator{

		private final World world;

		private EmptyGenerator(World world){
			this.world = world;
		}

		@Override
		public Chunk generateChunk(int x, int z){
			ChunkPrimer primer = new ChunkPrimer();

			if(x == 0 && z == 0){
				primer.setBlockState(0, 30, 0, Blocks.STONEBRICK.getDefaultState());
			}

			Chunk chunk = new Chunk(world, primer, x, z);
			chunk.generateSkylightMap();
			return chunk;
		}

		@Override
		public void populate(int x, int z){

		}

		@Override
		public boolean generateStructures(Chunk chunkIn, int x, int z){
			return false;
		}

		@Override
		public List<SpawnListEntry> getPossibleCreatures(EntityClassification creatureType, BlockPos pos){
			return null;
		}

		@Override
		public void recreateStructures(Chunk chunkIn, int x, int z){

		}

		@Override
		public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored){
			return null;
		}

		@Override
		public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos){
			return false;
		}
	}
}
