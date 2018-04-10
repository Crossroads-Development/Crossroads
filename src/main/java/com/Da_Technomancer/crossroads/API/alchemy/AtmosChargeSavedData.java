package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.Main;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class AtmosChargeSavedData extends WorldSavedData{

	public static final String ID = Main.MODID + "_atmos";
	public static final int CAPACITY = 1_000_000_000;

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
		AtmosChargeSavedData data = (AtmosChargeSavedData) storage.getOrLoadData(AtmosChargeSavedData.class, ID);

		if (data == null) {
			data = new AtmosChargeSavedData();
			storage.setData(ID, data);
		}
		return data;
	}

	private int atmosCharge;

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		atmosCharge = nbt.getInteger("atmos_charge");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("atmos_charge", atmosCharge);
		return nbt;
	}
}
