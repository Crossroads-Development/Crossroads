package com.Da_Technomancer.crossroads.dimensions;

import com.Da_Technomancer.crossroads.API.FlexibleGameProfile;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map.Entry;

public class PlayerDimensionMapSavedData extends WorldSavedData{

	private static final String PLAYER_DIM_ID = Crossroads.MODID + "_player_dim";
	private static PlayerProfileCache cache = null;//Used when loading from nbt

	protected final HashMap<FlexibleGameProfile, Integer> playerDim = new HashMap<>();

	private PlayerDimensionMapSavedData(){
		super(PLAYER_DIM_ID);
	}

	public static PlayerDimensionMapSavedData get(ServerWorld world, @Nullable PlayerProfileCache playerCache){
		cache = playerCache;
		DimensionSavedDataManager storage = world.getSavedData();
		PlayerDimensionMapSavedData data = storage.getOrCreate(PlayerDimensionMapSavedData::new, PLAYER_DIM_ID);

		cache = null;
		return data;
	}

	@Override
	public void read(CompoundNBT nbt){
		for(int i = 0; i < nbt.getInt("length"); i++){
			FlexibleGameProfile profile = FlexibleGameProfile.readFromNBT(nbt, "" + i, cache);
			if(profile == null){
				continue;
			}
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
			nbt.putInt(counter + "_dim", dim.getValue());
			counter++;
		}
		return nbt;
	}
}
