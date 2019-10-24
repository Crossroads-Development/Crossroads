package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class AtmosChargeSavedData extends WorldSavedData{

	public static final String ID = Crossroads.MODID + "_atmos";

	public static long getCapacity(){
		return CRConfig.atmosCap.get();
	}

	private AtmosChargeSavedData(){
		super(ID);
	}

	public static int getCharge(ServerWorld w){
		return get(w).atmosCharge;
	}

	public static void setCharge(ServerWorld w, int newCharge){
		AtmosChargeSavedData data = get(w);
		if(newCharge != data.atmosCharge){
			data.atmosCharge = newCharge;
			data.markDirty();
		}
	}

	private static AtmosChargeSavedData get(ServerWorld world){
		DimensionSavedDataManager storage = world.getSavedData();
		AtmosChargeSavedData data;
		try{
			data = storage.getOrCreate(AtmosChargeSavedData::new, ID);
		}catch(NullPointerException e){
			Crossroads.logger.error("Failed AtmosChargeSavedData get due to null DimensionSavedDataManager", e);
			return new AtmosChargeSavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}
		return data;
	}

	private int atmosCharge;

	@Override
	public void read(CompoundNBT nbt){
		atmosCharge = nbt.getInt("atmos_charge");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		nbt.putInt("atmos_charge", atmosCharge);
		return nbt;
	}
}
