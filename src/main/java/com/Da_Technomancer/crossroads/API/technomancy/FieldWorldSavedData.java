package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.Main;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map.Entry;

public class FieldWorldSavedData extends WorldSavedData{

	private static final String FIELDS_ID = Main.MODID + "_field";

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

	/**
	 * Key: Chunk coordinates in long form
	 * Value: ChunkField
	 */
	public final HashMap<Long, ChunkField> fieldNodes = new HashMap<Long, ChunkField>();

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		int i = 0;
		while(nbt.hasKey("chu" + i)){
			Long key = nbt.getLong("chu" + i);
			ChunkField field = ChunkField.readFromNBT(nbt, key);
			if(field != null){
				fieldNodes.put(key, field);
			}
			i++;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		int i = 0;
		for(Entry<Long, ChunkField> mapping : fieldNodes.entrySet()){
			nbt.setLong("chu" + i, mapping.getKey());
			mapping.getValue().writeToNBT(nbt, mapping.getKey());
			i++;
		}
		return nbt;
	}
}
