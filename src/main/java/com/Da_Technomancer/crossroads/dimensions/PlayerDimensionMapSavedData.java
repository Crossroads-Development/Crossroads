package com.Da_Technomancer.crossroads.dimensions;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class PlayerDimensionMapSavedData extends WorldSavedData{

	public static final String PLAYER_DIM_ID = Main.MODID + "_player_dim";

	public PlayerDimensionMapSavedData(){
		super(PLAYER_DIM_ID);
	}

	public PlayerDimensionMapSavedData(String name){
		super(name);
	}
	
	private static PlayerProfileCache cache = null;
	
	public static PlayerDimensionMapSavedData get(World world, @Nullable PlayerProfileCache playerCache){
		cache = playerCache;
		MapStorage storage = world.getMapStorage();
		PlayerDimensionMapSavedData data = (PlayerDimensionMapSavedData) storage.getOrLoadData(PlayerDimensionMapSavedData.class, PLAYER_DIM_ID);

		if (data == null) {
			data = new PlayerDimensionMapSavedData();
			storage.setData(PLAYER_DIM_ID, data);
		}
		data.setDirty(true);
		cache = null;
		return data;
	}

	protected final HashMap<GameProfileNonPicky, Integer> playerDim = new HashMap<GameProfileNonPicky, Integer>();

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		for(int i = 0; i < nbt.getInteger("length"); i++){
			GameProfile profile = new GameProfile(nbt.hasKey(i + "_id") ? nbt.getUniqueId(i + "_id") : null, nbt.hasKey(i + "_name") ? nbt.getString(i + "_name") : null);
			if(cache != null && profile.getId() == null){
				Main.logger.info(Main.MODID + ": Attempting to complete player profile in dimension map. This is not an error. Profile: " + profile.toString());
				GameProfile search = cache.getGameProfileForUsername(profile.getName());
				profile = search == null ? profile : search;
			}
			playerDim.put(new GameProfileNonPicky(profile), nbt.getInteger(i + "_dim"));
			
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		int counter = 0;
		nbt.setInteger("length", playerDim.size());
		for(Entry<GameProfileNonPicky, Integer> dim : playerDim.entrySet()){
			
			if(dim.getKey().getName() != null){
				nbt.setString(counter + "_name", dim.getKey().getName());
			}
			if(dim.getKey().getId() != null){
				nbt.setUniqueId(counter + "_id", dim.getKey().getId());
			}
			nbt.setInteger(counter + "_dim", dim.getValue());
			counter++;
		}
		return nbt;
	}
}
