package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.ForgeConfigSpec;

public class AtmosChargeSavedData extends WorldSavedData{

	public static final String ID = Crossroads.MODID + "_atmos";

	public static int getCapacity(){
		return ((ForgeConfigSpec.IntValue) CrossroadsConfig.atmosCap).get();
	}

	public AtmosChargeSavedData(){
		super(ID);
	}

	public AtmosChargeSavedData(String name){
		super(name);
	}

	public static int getCharge(World w){
		return get(w).atmosCharge;
	}

	public static void setCharge(World w, int newCharge){
		AtmosChargeSavedData data = get(w);
		if(newCharge != data.atmosCharge){
			data.atmosCharge = newCharge;
			data.markDirty();
		}
	}

	private static AtmosChargeSavedData get(World world){
		MapStorage storage = world.getMapStorage();
		AtmosChargeSavedData data;
		try{
			data = (AtmosChargeSavedData) storage.getOrLoadData(AtmosChargeSavedData.class, ID);
		}catch(NullPointerException e){
			Crossroads.logger.error("Failed AtmosChargeSavedData get due to null MapStorage", e);
			return new AtmosChargeSavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}
		if (data == null) {
			data = new AtmosChargeSavedData();
			storage.setData(ID, data);
		}
		return data;
	}

	private int atmosCharge;

	@Override
	public void readFromNBT(CompoundNBT nbt){
		atmosCharge = nbt.getInteger("atmos_charge");
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		nbt.setInteger("atmos_charge", atmosCharge);
		return nbt;
	}
}
