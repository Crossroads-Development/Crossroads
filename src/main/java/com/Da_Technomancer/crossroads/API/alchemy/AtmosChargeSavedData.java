package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class AtmosChargeSavedData extends SavedData{

	public static final String ID = Crossroads.MODID + "_atmos";

	public static int getCapacity(){
		return CRConfig.atmosCap.get();
	}

	private AtmosChargeSavedData(){
		super();
	}

	public static int getCharge(ServerLevel w){
		return get(w).atmosCharge;
	}

	public static void setCharge(ServerLevel w, int newCharge){
		AtmosChargeSavedData data = get(w);
		if(newCharge != data.atmosCharge){
			data.atmosCharge = newCharge;
			data.setDirty();
		}
	}

	private static AtmosChargeSavedData get(ServerLevel world){
		//We want all dimensions to share the same saved data,
		//So we always reference the overworld instance
		DimensionDataStorage storage;
		if(world.dimension().location().equals(DimensionType.OVERWORLD_EFFECTS)){
			storage = world.getDataStorage();
		}else{
			storage = world.getServer().overworld().getDataStorage();
		}
		return storage.computeIfAbsent(AtmosChargeSavedData::load, AtmosChargeSavedData::new, ID);
	}

	private int atmosCharge;

	public static AtmosChargeSavedData load(CompoundTag nbt){
		AtmosChargeSavedData data = new AtmosChargeSavedData();
		data.atmosCharge = nbt.getInt("atmos_charge");
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		nbt.putInt("atmos_charge", atmosCharge);
		return nbt;
	}
}
