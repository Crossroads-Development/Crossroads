package com.Da_Technomancer.crossroads.dimensions;

import com.Da_Technomancer.crossroads.API.FlexibleGameProfile;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDimLoadToClient;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import java.util.HashMap;
import java.util.List;

public class ModDimensions{

	public static DimensionType workspaceDimType;
	public static DimensionType prototypeDimType;

	public static final int PROTOTYPE_DIM_ID = 27;
	public static final int PROTOTYPE_LIMIT = 500;

	public static void init(){
		workspaceDimType = DimensionType.register(Crossroads.MODID, "_workspace", 567, WorkspaceWorldProvider.class, false);
		prototypeDimType = DimensionType.register(Crossroads.MODID, "_prototype", 568, PrototypeWorldProvider.class, false);
		DimensionManager.registerDimension(PROTOTYPE_DIM_ID, prototypeDimType);
		ForgeChunkManager.setForcedChunkLoadingCallback(Crossroads.instance, new LoadingCallback(){

			@Override
			public void ticketsLoaded(List<Ticket> tickets, World world){

			}
		});
	}

	public static void loadDims(){
//		for(int i : DimensionManager.getDimensions(workspaceDimType)){
//			DimensionManager.unregisterDimension(i);
//		}
		
		PlayerDimensionMapSavedData data = PlayerDimensionMapSavedData.get(DimensionManager.getWorld(0), DimensionManager.getWorld(0).getMinecraftServer() == null ? null : DimensionManager.getWorld(0).getMinecraftServer().getPlayerProfileCache());
		HashMap<FlexibleGameProfile, Integer> playerDim = null;
		try{
			playerDim = data.playerDim;
			for(int id : playerDim.values()){
				DimensionManager.registerDimension(id, workspaceDimType);
			}
			CrossroadsPackets.network.sendToAll(new SendDimLoadToClient(playerDim.values().toArray(new Integer[0])));
		}catch(Exception ex){
			if(CRConfig.wipeInvalidMappings.getBoolean()){
				if(playerDim != null){
					playerDim.clear();
					data.markDirty();
				}
				Crossroads.logger.fatal("Something went wrong while loading the player dimension mappings. Attempting to wipe the mappings. Shutting down. It should work if you restart now.", ex);
			}else{
				Crossroads.logger.fatal("Something went wrong while loading the player dimension mappings. Shutting down. If you would like to wipe the mappings completely, there is a config option.", ex);
			}
			throw ex;
		}
	}

	/** This does not initialize the dimension. If needed, run
	 * {@link DimensionManager#initDimension(int)} on the dimension if {@link DimensionManager#getWorld(int)} returns null for that dimension. */
	public static int getDimForPlayer(ServerPlayerEntity play){
		return getDimForPlayer(new FlexibleGameProfile(play.getGameProfile()));
	}

	/** This does not initialize the dimension. If needed, run
	 * {@link DimensionManager#initDimension(int)} on the dimension if {@link DimensionManager#getWorld(int)} returns null for that dimension. */
	public static int getDimForPlayer(FlexibleGameProfile play){
		PlayerDimensionMapSavedData data = PlayerDimensionMapSavedData.get(DimensionManager.getWorld(0), null);
		HashMap<FlexibleGameProfile, Integer> playerDim = data.playerDim;
		
		if(playerDim.containsKey(play)){
			return playerDim.get(play);
		}

		int dim = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dim, workspaceDimType);
		playerDim.put(play, dim);
		data.markDirty();
		CrossroadsPackets.network.sendToAll(new SendDimLoadToClient(new int[] {dim}));
		return dim;
	}

	/** Sets up the next available prototype dimension chunk, reserves the chunk, and returns the index. Returns -1 if it failed.
	 * portPos should be chunk relative when passed to this method. The method adjusts accordingly.
	 */
	public static int nextFreePrototypeChunk(PrototypePortTypes[] ports, BlockPos[] portPos){
		// This method assumes that all chunks saved in PrototypeWorldSavedData are in the layout this would create.
		// It creates a grid layout of chunks containing only barriers & air, with the remaining chunks being used for prototypes. The grid is centered on chunk 0,0 (non prototype).
		// The grid created starts at (-100, -100) and goes to (100, -80).

		PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);

		if(data.prototypes.contains(null)){
			// Recycles deleted prototypes if possible.
			int available = data.prototypes.indexOf(null);
			// Due to the grid, one row has capacity for 100 chunks
			int x = ((available % 100) * 2) - 99;
			int z = (available / 50) - 99;

			data.prototypes.set(available, new PrototypeInfo(ports, portPos, new ChunkPos(x, z)));
			data.markDirty();
			return available;
		}else{
			int used = data.prototypes.size();
			if(used > PROTOTYPE_LIMIT){
				// Too many prototypes in existence.
				return -1;
			}

			// Do to the grid, one row has capacity for 100 chunks
			int x = ((used % 100) * 2) - 99;
			int z = (used / 50) - 99;

			//TODO This part will redundantly block already blocked chunks. This is a possible optimization point if it ends up mattering.
			WorldServer worldDim = DimensionManager.getWorld(PROTOTYPE_DIM_ID);

			blockChunk(new ChunkPos(x - 1, z - 1), worldDim);
			blockChunk(new ChunkPos(x - 1, z), worldDim);
			blockChunk(new ChunkPos(x, z - 1), worldDim);
			blockChunk(new ChunkPos(x + 1, z), worldDim);
			blockChunk(new ChunkPos(x, z + 1), worldDim);
			blockChunk(new ChunkPos(x + 1, z + 1), worldDim);

			data.prototypes.add(used, new PrototypeInfo(ports, portPos, new ChunkPos(x, z)));
			data.markDirty();
			return used;
		}
	}

	private static void blockChunk(ChunkPos pos, WorldServer dimWorld){
		for(int x = pos.getXStart(); x <= pos.getXEnd(); x++){
			for(int z = pos.getZStart(); z <= pos.getZEnd(); z++){
				for(int y = 0; y < 256; y++){
					if(x == pos.getXStart() || x == pos.getXEnd() || z == pos.getZStart() || z == pos.getZEnd()){
						dimWorld.setBlockState(new BlockPos(x, y, z), Blocks.BARRIER.getDefaultState(), 0);
					}
				}
			}
		}
	}
}
