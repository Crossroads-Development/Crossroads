package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnInventorySavedData extends SavedData{

	/*
	 * Stores the hotbar of the player when the die with a toolbelt; used to restore when they respawn
	 * This would be unneeded except for the possibility of the player hitting 'quit to main menu' instead of respawn
	 */

	public static final String ID = Crossroads.MODID + "_spawn_inv";

	private RespawnInventorySavedData(){
		super(ID);
	}

	public static HashMap<UUID, ItemStack[]> getMap(ServerLevel w){
		return get(w).savedInventories;
	}

	public static void markDirty(ServerLevel w){
		RespawnInventorySavedData data = get(w);
		data.setDirty();
	}

	private static RespawnInventorySavedData get(ServerLevel world){
		//We want all dimensions to share the same saved data,
		//So we always reference the overworld instance
		DimensionDataStorage storage;
		if(world.dimension().location().equals(DimensionType.OVERWORLD_EFFECTS)){
			storage = world.getDataStorage();
		}else{
			storage = world.getServer().overworld().getDataStorage();
		}
		RespawnInventorySavedData data;
		try{
			data = storage.computeIfAbsent(RespawnInventorySavedData::new, ID);
		}catch(NullPointerException e){
			Crossroads.logger.error("Failed RespawnInventorySavedData get due to null DimensionSavedDataManager", e);
			return new RespawnInventorySavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}
		return data;
	}

	private final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>(1);

	@Override
	public void load(CompoundTag nbt){
		savedInventories.clear();
		int i = 0;
		while(nbt.contains("key_low_" + i)){
			UUID id = new UUID(nbt.getLong("key_high_" + i), nbt.getLong("key_low_" + i));
			ItemStack[] hotbar = new ItemStack[10];
			for(int j = 0; j < hotbar.length; j++){
				hotbar[j] = ItemStack.of(nbt.getCompound("item_" + i + "_" + j));
			}
			savedInventories.put(id, hotbar);
			i++;
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		int i = 0;
		for(Map.Entry<UUID, ItemStack[]> entry : savedInventories.entrySet()){
			nbt.putLong("key_high_" + i, entry.getKey().getMostSignificantBits());
			nbt.putLong("key_low_" + i, entry.getKey().getLeastSignificantBits());
			ItemStack[] value = entry.getValue();
			for(int j = 0; j < value.length; j++){
				nbt.put("item_" + i + "_" + j, value[j].save(new CompoundTag()));
			}
			i++;
		}

		return nbt;
	}
}
