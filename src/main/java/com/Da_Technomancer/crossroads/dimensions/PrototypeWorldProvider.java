package com.Da_Technomancer.crossroads.dimensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrototypeWorldProvider extends WorldProvider{

	@Override
	public void init(){
		biomeProvider = new BiomeProviderSingle(Biomes.VOID);
		hasSkyLight = false;
	}

	@Override
	public DimensionType getDimensionType(){
		return ModDimensions.prototypeDimType;
	}

	private static final ArrayList<TileEntity> tickingTE = new ArrayList<TileEntity>();

	/**
	 * Ticks the TileEntities in the chunk at the specified chunk coordinated in the prototype dimension. They will not be ticked otherwise. Based off the vanilla TileEntity ticking method. 
	 * @param chunk The ChunkPos to tick all the TileEntities in. 
	 * Call on the virtual server side only. 
	 */
	public static void tickChunk(ChunkPos chunk){
		int chunkX = chunk.x;
		int chunkZ = chunk.z;
		WorldServer protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(protWorld == null){
			DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		}
		if(tickingTE.isEmpty() && !protWorld.tickableTileEntities.isEmpty()){
			DimensionManager.createProviderFor(ModDimensions.PROTOTYPE_DIM_ID).onWorldUpdateEntities();
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
					protWorld.profiler.startSection(ticking.getClass()); // Fix for MC-117087
					((ITickable) ticking).update();
					protWorld.profiler.endSection();
				}catch(Throwable throwable){
					CrashReport crash = CrashReport.makeCrashReport(throwable, "Ticking block entity");
					CrashReportCategory crashCateg = crash.makeCategory("Block entity being ticked");
					ticking.addInfoToCrashReport(crashCateg);
					if(ForgeModContainer.removeErroringTileEntities){
						Main.logger.error(crash.getCompleteReport());
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
					Chunk chunkWorld = protWorld.getChunkFromChunkCoords(chunkX, chunkZ);
					if(chunkWorld.getTileEntity(ticking.getPos(), Chunk.EnumCreateEntityType.CHECK) == ticking){
						chunkWorld.removeTileEntity(ticking.getPos());
					}
				}
			}
		}
	}

	@Override
	public void onWorldUpdateEntities(){
		world.profiler.startSection(Main.MODNAME + "-Prototype TileEntity Sorting");
		tickingTE.addAll(world.tickableTileEntities);
		world.tickableTileEntities.clear();
		tickingTE.removeIf((TileEntity te) -> {
			return !world.loadedTileEntityList.contains(te);
		});
		world.profiler.endSection();
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
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored(){
		return false;
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
		public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos){
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
