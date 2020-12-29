package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnInventorySavedData extends WorldSavedData{

	/*
	 * Stores the hotbar of the player when the die with a toolbelt; used to restore when they respawn
	 * This would be unneeded except for the possibility of the player hitting 'quit to main menu' instead of respawn
	 */

	public static final String ID = Crossroads.MODID + "_spawn_inv";

	private RespawnInventorySavedData(){
		super(ID);
	}

	public static HashMap<UUID, ItemStack[]> getMap(ServerWorld w){
		return get(w).savedInventories;
	}

	public static void markDirty(ServerWorld w){
		RespawnInventorySavedData data = get(w);
		data.markDirty();
	}

	private static RespawnInventorySavedData get(ServerWorld world){
		//We want all dimensions to share the same saved data,
		//So we always reference the overworld instance
		DimensionSavedDataManager storage;
		if(world.getDimensionKey().getLocation().equals(DimensionType.OVERWORLD_ID)){
			storage = world.getSavedData();
		}else{
			storage = world.getServer().func_241755_D_().getSavedData();
		}
		RespawnInventorySavedData data;
		try{
			data = storage.getOrCreate(RespawnInventorySavedData::new, ID);
		}catch(NullPointerException e){
			Crossroads.logger.error("Failed RespawnInventorySavedData get due to null DimensionSavedDataManager", e);
			return new RespawnInventorySavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}
		return data;
	}

	private final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>(1);

	@Override
	public void read(CompoundNBT nbt){
		savedInventories.clear();
		int i = 0;
		while(nbt.contains("key_low_" + i)){
			UUID id = new UUID(nbt.getLong("key_high_" + i), nbt.getLong("key_low_" + i));
			ItemStack[] hotbar = new ItemStack[10];
			for(int j = 0; j < hotbar.length; j++){
				hotbar[j] = ItemStack.read(nbt.getCompound("item_" + i + "_" + j));
			}
			savedInventories.put(id, hotbar);
			i++;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		int i = 0;
		for(Map.Entry<UUID, ItemStack[]> entry : savedInventories.entrySet()){
			nbt.putLong("key_high_" + i, entry.getKey().getMostSignificantBits());
			nbt.putLong("key_low_" + i, entry.getKey().getLeastSignificantBits());
			ItemStack[] value = entry.getValue();
			for(int j = 0; j < value.length; j++){
				nbt.put("item_" + i + "_" + j, value[j].write(new CompoundNBT()));
			}
			i++;
		}

		return nbt;
	}
}
