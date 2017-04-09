package com.Da_Technomancer.crossroads.dimensions;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorkspaceWorldProvider extends WorldProvider{

	@Override
	public DimensionType getDimensionType(){
		return world != null && world.provider.getDimension() == ModDimensions.PROTOTYPE_DIM_ID ? ModDimensions.prototypeDimType : ModDimensions.workspaceDimType;
	}

	@Override
	public boolean canRespawnHere(){
		return false;
	}
	
	@Override
	public boolean hasSkyLight(){
		return false;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks){
		return 0;
	}
	
	@Override
	public boolean hasNoSky(){
		return true;
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
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored(){
		return false;
	}

	@Override
	public BiomeProvider getBiomeProvider(){
		return new BiomeProviderSingle(Biomes.VOID);
	}

	@Override
	public IChunkGenerator createChunkGenerator(){
		return new EmptyGenerator(world);
	}

	private static class EmptyGenerator implements IChunkGenerator{

		private final World world;

		private EmptyGenerator(World world){
			this.world = world;
		}

		@Override
		public Chunk provideChunk(int x, int z){
			Chunk chunk = new Chunk(world, x, z);
			if(x == 0 && z == 0){
				chunk.setBlockState(new BlockPos(0, 30, 0), Blocks.STONEBRICK.getDefaultState());
			}
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
		public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos){
			return null;
		}

		@Override
		public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_){
			return null;
		}

		@Override
		public void recreateStructures(Chunk chunkIn, int x, int z){

		}
	}
}