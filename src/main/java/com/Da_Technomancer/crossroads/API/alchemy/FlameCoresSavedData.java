package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FlameCoresSavedData extends SavedData{

	public static final String ID = Crossroads.MODID + "_flame_cores";

	private FlameCoresSavedData(){
		super();
	}

	/**
	 * Gets all flame cores in the world. Note that it's not guaranteed to find flame cores in unloaded chunks.
	 * @param w The server world.
	 * @return List of all flame cores.
	 */
	public static List<EntityFlameCore> getFlameCores(ServerLevel w){
		List<UUID> idLists = get(w).flameCoreIDs;
		Iterator<UUID> ids = idLists.iterator();
		ArrayList<EntityFlameCore> cores = new ArrayList<>(idLists.size());
		while(ids.hasNext()){
			UUID id = ids.next();
			Entity ent = w.getEntity(id);
			if(ent instanceof EntityFlameCore core && core.isAlive()){
				cores.add(core);
			}else{
				//Remove expired entries from the list
				ids.remove();
			}
		}
		return cores;
	}

	public static void addFlameCore(ServerLevel w, EntityFlameCore newCore){
		UUID newID = newCore.getUUID();
		FlameCoresSavedData data = get(w);
		if(!data.flameCoreIDs.contains(newID)){
			data.flameCoreIDs.add(newID);
			data.setDirty();
		}
	}

	private static FlameCoresSavedData get(ServerLevel world){
		//Data is per-dimension
		DimensionDataStorage storage;
		storage = world.getDataStorage();
		return storage.computeIfAbsent(FlameCoresSavedData::load, FlameCoresSavedData::new, ID);
	}

	private final ArrayList<UUID> flameCoreIDs = new ArrayList<>();

	public static FlameCoresSavedData load(CompoundTag nbt){
		FlameCoresSavedData data = new FlameCoresSavedData();
		int i = 0;
		while(nbt.contains("id_" + i)){
			data.flameCoreIDs.add(nbt.getUUID("id_" + i));
			i++;
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		for(int i = 0; i < flameCoreIDs.size(); i++){
			nbt.putUUID("id_" + i, flameCoreIDs.get(i));
		}
		return nbt;
	}
}
