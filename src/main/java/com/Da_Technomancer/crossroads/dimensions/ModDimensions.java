package com.Da_Technomancer.crossroads.dimensions;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDimLoadToClient;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class ModDimensions{

	public static DimensionType workspaceDimType;
	private static HashMap<GameProfileNonPicky, Integer> playerDim;

	public static void init(){
		workspaceDimType = DimensionType.register(Main.MODID, "_workspace", 567, WorkspaceWorldProvider.class, false);
	}

	public static void loadDims(){
		for(int i : DimensionManager.getDimensions(ModDimensions.workspaceDimType)){
			DimensionManager.unregisterDimension(i);
		}
		//This hasn't broken in testing, but there are far too many ways for this to go wrong. Better safe then sorry.
		try{
			
			playerDim = PlayerDimensionMapSavedData.get(DimensionManager.getWorld(0), DimensionManager.getWorld(0).getMinecraftServer() == null ? null : DimensionManager.getWorld(0).getMinecraftServer().getPlayerProfileCache()).playerDim;
			for(int id : playerDim.values()){
				DimensionManager.registerDimension(id, workspaceDimType);
			}
			ModPackets.network.sendToAll(new SendDimLoadToClient(playerDim.values().toArray(new Integer[playerDim.size()])));
		}catch(Exception ex){
			if(ModConfig.wipeInvalidMappings.getBoolean()){
				if(playerDim != null){
					playerDim.clear();
				}
				Main.logger.fatal(Main.MODID + ": Something went wrong while loading the player dimension mappings. Attempting to wipe the mappings. Shutting down. It should work if you restart now.", ex);
			}else{
				Main.logger.fatal(Main.MODID + ": Something went wrong while loading the player dimension mappings. Shutting down. If you would like to wipe the mappings completely, there is a config option.", ex);
			}
			throw ex;
		}
	}

	/**
	 * This does not initialize the dimension. If needed, run 
	 * {@link DimensionManager#initDimension(int)} on the dimension if {@link DimensionManager#getWorld(int)} returns null for that dimension.
	 */
	public static int getDimForPlayer(EntityPlayerMP play){
		return getDimForPlayer(new GameProfileNonPicky(play.getGameProfile()));
	}
	
	/**
	 * This does not initialize the dimension. If needed, run 
	 * {@link DimensionManager#initDimension(int)} on the dimension if {@link DimensionManager#getWorld(int)} returns null for that dimension.
	 */
	public static int getDimForPlayer(GameProfileNonPicky play){
		if(playerDim.containsKey(play)){
			int dim = playerDim.get(play);
			return dim;
		}
		
		int dim = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dim, workspaceDimType);
		playerDim.put(play, dim);
		PlayerDimensionMapSavedData.get(DimensionManager.getWorld(0), null).markDirty();
		ModPackets.network.sendToAll(new SendDimLoadToClient(new int[] {dim}));
		return dim;
	}
}
