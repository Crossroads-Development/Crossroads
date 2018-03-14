package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendAlchNamesToClient;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class AlchemWorldSavedData extends WorldSavedData{

	public static final String ALCHEM_ID = Main.MODID + "_alchem";
	
	public AlchemWorldSavedData(){
		super(ALCHEM_ID);
	}
	
	public AlchemWorldSavedData(String name){
		super(name);
	}

	/**
	 * Loads custom alchemy names from disk
	 * @param world Any initialized world
	 */
	public static void loadData(World world){
		MapStorage storage = world.getMapStorage();
		AlchemWorldSavedData data = (AlchemWorldSavedData) storage.getOrLoadData(AlchemWorldSavedData.class, ALCHEM_ID);
		
		if(data == null){
			for(int i = 0; i < AlchemyCore.DYNAMIC_REAGENT_COUNT; i++){
				AlchemyCore.CUST_REAG_NAMES[i] = null;
			}
			syncNamesToClient(world);
			return;
		}
		
		for(int i = 0; i < data.loadedNames.length; i++){
			AlchemyCore.CUST_REAG_NAMES[i] = data.loadedNames[i];
		}
		syncNamesToClient(world);
	}
	
	private static void syncNamesToClient(World world){
		SendAlchNamesToClient packet = new SendAlchNamesToClient(AlchemyCore.CUST_REAG_NAMES, false, 0);
		for(EntityPlayerMP play : world.getMinecraftServer().getPlayerList().getPlayers()){
			ModPackets.network.sendTo(packet, play);
		}
	}
	
	/**
	 * Saves custom alchemy names to disk
	 * @param world Any initialized world
	 */
	public static void saveData(World world){
		MapStorage storage = world.getMapStorage();
		AlchemWorldSavedData data = (AlchemWorldSavedData) storage.getOrLoadData(AlchemWorldSavedData.class, ALCHEM_ID);
		
		if (data == null) {
			data = new AlchemWorldSavedData();
			storage.setData(ALCHEM_ID, data);
		}
		data.setDirty(true);
		syncNamesToClient(world);
	}
	
	private final String[] loadedNames = new String[AlchemyCore.DYNAMIC_REAGENT_COUNT];
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		for(int i = 0; i < loadedNames.length; i++){
			if(nbt.hasKey("nam" + i)){
				loadedNames[i] = nbt.getString("nam" + i);
			}else{
				loadedNames[i] = null;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(int i = 0; i < AlchemyCore.DYNAMIC_REAGENT_COUNT; i++){
			if(AlchemyCore.CUST_REAG_NAMES[i] != null){
				nbt.setString("nam" + i, AlchemyCore.CUST_REAG_NAMES[i]);
			}
		}
		return nbt;
	}
}
