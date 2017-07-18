package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.HashMap;
import java.util.Map.Entry;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class FieldWorldSavedData extends WorldSavedData{

	public static final String FIELDS_ID = Main.MODID + "_fields";
	
	public FieldWorldSavedData(){
		super(FIELDS_ID);
	}
	
	public FieldWorldSavedData(String name){
		super(name);
	}

	public static FieldWorldSavedData get(World world){
		MapStorage storage = world.getPerWorldStorage();
		FieldWorldSavedData data = (FieldWorldSavedData) storage.getOrLoadData(FieldWorldSavedData.class, FIELDS_ID);
		
		if (data == null) {
			data = new FieldWorldSavedData();
			storage.setData(FIELDS_ID, data);
		}
		data.setDirty(true);
		return data;
	}
	
	public static byte[][][] getDefaultChunkFlux(){
		byte[][][] out = new byte[2][8][];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 8; j++){
				out[i][j] = new byte[] {7, 7, 7, 7, 7, 7, 7, 7};
			}
		}
		return out;
	}
	
	public static short[][][] getDefaultChunkForce(){
		short[][][] out = new short[2][8][8];
		return out;
	}
	
	/**
	 * Key: Chunk coordinates in long form
	 * Value: Size 2 array of 8x8 array. In order, dimensions represent: type (0: FLUX, 1: RATE), node X, node Z, flux - 1 (so as to fit 0-128 range into byte)
	 */
	public final HashMap<Long, byte[][][]> fieldNodes = new HashMap<Long, byte[][][]>();
	
	/**
	 * Key: Chunk coordinates in long form
	 * Value: Size 2 array of 8x8 array. In order, dimensions represent: type (0:FLUX, 1: RATE), node X, node Z, net force
	 * 
	 * NOTE THAT THIS IS NOT SAVED ON WORLD RELOAD
	 * Any long that is in the fieldNodes map should also be correspond to something in the nodeForces map. Anything that causes this to not be the case is broken.
	 */
	public final HashMap<Long, short[][][]> nodeForces = new HashMap<Long, short[][][]>();
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		int i = 0;
		while(nbt.hasKey("chu" + i)){
			byte[][][] bytes = new byte[2][8][8];
			for(int j = 0; j < 2; j++){
				for(int k = 0; k < 8; k++){
					bytes[j][k] = nbt.getByteArray("byt" + i + '_' + j + '_' + k);
				}
			}
			fieldNodes.put(nbt.getLong("chu" + i), bytes);
			nodeForces.put(nbt.getLong("chu" + i), getDefaultChunkForce());
			i++;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		int i = 0;
		for(Entry<Long, byte[][][]> mapping : fieldNodes.entrySet()){
			nbt.setLong("chu" + i, mapping.getKey());
			for(int j = 0; j < 2; j++){
				for(int k = 0; k < 8; k++){
					nbt.setByteArray("byt" + i + '_' + j + '_' + k, mapping.getValue()[j][k]);
				}
			}
			i++;
		}
		return nbt;
	}
}
