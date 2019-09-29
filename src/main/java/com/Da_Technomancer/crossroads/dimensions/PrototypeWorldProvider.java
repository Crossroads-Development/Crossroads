package com.Da_Technomancer.crossroads.dimensions;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PrototypeWorldProvider extends Dimension{

	@Override
	public void init(){
		biomeProvider = new SingleBiomeProvider(Biomes.VOID);
		hasSkyLight = false;
	}

	@Override
	public DimensionType getDimensionType(){
		return ModDimensions.prototypeDimType;
	}

	private static final ArrayList<TileEntity> tickingTE = new ArrayList<TileEntity>();

	/**
	 * Ticks the TileEntities in the chunk at the specified chunk coordinated in the prototype dimension. They will not be ticked otherwise. Based off the vanilla TileEntity ticking method. 
	 * Call on the virtual server side only.
	 */
	public static void tickChunk(int chunkX, int chunkZ){
		ServerWorld protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(protWorld == null){
			DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		}
		if(tickingTE.isEmpty() && !protWorld.tickableTileEntities.isEmpty()){
			PrototypeWorldProvider provider = (PrototypeWorldProvider) DimensionManager.createProviderFor(ModDimensions.PROTOTYPE_DIM_ID);
			if(provider.world == null){
				return;
			}
			provider.onWorldUpdateEntities();
		}

		Iterator<TileEntity> iter = tickingTE.iterator();
		while(iter.hasNext()){
			TileEntity ticking = iter.next();

			if(!ticking.isInvalid() && ticking.hasWorld()){
				BlockPos blockpos = ticking.getPos();
				if(chunkX != blockpos.getX() >> 4 || chunkZ != blockpos.getZ() >> 4){
					continue;
				}

				try{
					protWorld.getProfiler().startSection(ticking.getClass());
//					Swap over to the commented out method once the deprecated one is removed. The deprecated one is still used here for backward compatability. 
//										protWorld.getProfiler().func_194340_a(() ->
//										{
//											return String.valueOf(TileEntity.getKey(ticking.getClass()));
//										});
					((ITickableTileEntity) ticking).update();
					protWorld.getProfiler().endSection();
				}catch(Throwable throwable){
					CrashReport crash = CrashReport.makeCrashReport(throwable, "Ticking block entity");
					CrashReportCategory crashCateg = crash.makeCategory("Block entity being ticked");
					ticking.addInfoToCrashReport(crashCateg);
					if(ForgeModContainer.removeErroringTileEntities){
						Crossroads.logger.error(crash.getCompleteReport());
						ticking.invalidate();
						protWorld.removeTileEntity(ticking.getPos());
					}else
						throw new ReportedException(crash);
				}
			}

			if(ticking.isInvalid()){
				iter.remove();
				protWorld.loadedTileEntityList.remove(ticking);

				if(protWorld.isBlockLoaded(ticking.getPos())){
					// Forge: Bugfix: If we set the tile entity it immediately sets it in the chunk, so we could be desynced
					Chunk chunkWorld = protWorld.getChunk(chunkX, chunkZ);
					if(chunkWorld.getTileEntity(ticking.getPos(), Chunk.EnumCreateEntityType.CHECK) == ticking){
						chunkWorld.removeTileEntity(ticking.getPos());
					}
				}
			}
		}
	}

	@Override
	public void onWorldUpdateEntities(){
		world.getProfiler().startSection(Crossroads.MODNAME + "-Prototype TileEntity Sorting");
		tickingTE.addAll(world.tickableTileEntities);
		world.tickableTileEntities.clear();
		tickingTE.removeIf((TileEntity te) -> {
			return !world.loadedTileEntityList.contains(te);
		});
		world.getProfiler().endSection();
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
			return new Chunk(world, x, z);
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
