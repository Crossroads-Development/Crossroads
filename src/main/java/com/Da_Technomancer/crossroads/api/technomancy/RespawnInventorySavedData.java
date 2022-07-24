package com.Da_Technomancer.crossroads.api.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

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
		super();
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
		if(world.dimension().location().equals(BuiltinDimensionTypes.OVERWORLD_EFFECTS)){
			storage = world.getDataStorage();
		}else{
			storage = world.getServer().overworld().getDataStorage();
		}
		return storage.computeIfAbsent(RespawnInventorySavedData::load, RespawnInventorySavedData::new, ID);
	}

	private final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>(1);

	public static RespawnInventorySavedData load(CompoundTag nbt){
		RespawnInventorySavedData data = new RespawnInventorySavedData();
		data.savedInventories.clear();
		int i = 0;
		while(nbt.contains("key_low_" + i)){
			UUID id = new UUID(nbt.getLong("key_high_" + i), nbt.getLong("key_low_" + i));
			ItemStack[] hotbar = new ItemStack[10];
			for(int j = 0; j < hotbar.length; j++){
				hotbar[j] = ItemStack.of(nbt.getCompound("item_" + i + "_" + j));
			}
			data.savedInventories.put(id, hotbar);
			i++;
		}
		return data;
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
