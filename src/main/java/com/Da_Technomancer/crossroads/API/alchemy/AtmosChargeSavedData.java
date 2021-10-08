package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;

public class AtmosChargeSavedData extends SavedData{

	public static final String ID = Crossroads.MODID + "_atmos";

	public static int getCapacity(){
		return CRConfig.atmosCap.get();
	}

	private AtmosChargeSavedData(){
		super(ID);
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
		AtmosChargeSavedData data;
		try{
			data = storage.computeIfAbsent(AtmosChargeSavedData::new, ID);
		}catch(NullPointerException e){
			Crossroads.logger.error("Failed AtmosChargeSavedData get due to null DimensionSavedDataManager", e);
			return new AtmosChargeSavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}
		return data;
	}

	private int atmosCharge;

	@Override
	public void load(CompoundTag nbt){
		atmosCharge = nbt.getInt("atmos_charge");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		nbt.putInt("atmos_charge", atmosCharge);
		return nbt;
	}
}
