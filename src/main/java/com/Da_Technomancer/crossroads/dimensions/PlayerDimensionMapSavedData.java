package com.Da_Technomancer.crossroads.dimensions;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.API.FlexibleGameProfile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerDimensionMapSavedData extends WorldSavedData{

	public static final String PLAYER_DIM_ID = Crossroads.MODID + "_player_dim";

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

		cache = null;
		return data;
	}

	protected final HashMap<FlexibleGameProfile, Integer> playerDim = new HashMap<FlexibleGameProfile, Integer>();

	@Override
	public void read(CompoundNBT nbt){
		for(int i = 0; i < nbt.getInt("length"); i++){
			FlexibleGameProfile profile = FlexibleGameProfile.readFromNBT(nbt, "" + i, cache);
			if(profile.isNewlyCompleted()){
				markDirty();
			}
			playerDim.put(profile, nbt.getInt(i + "_dim"));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		int counter = 0;
		nbt.putInt("length", playerDim.size());
		for(Entry<FlexibleGameProfile, Integer> dim : playerDim.entrySet()){
			dim.getKey().writeToNBT(nbt, "" + counter);
			nbt.putInt(counter + "_dim", dim.get());
			counter++;
		}
		return nbt;
	}
}
