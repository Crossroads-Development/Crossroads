package com.Da_Technomancer.crossroads.API.technomancy;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class PrototypeWorldSavedData extends WorldSavedData{

	public static final String PROTOTYPE_ID = Main.MODID + "_prototype";
	
	public PrototypeWorldSavedData(){
		super(PROTOTYPE_ID);
	}
	
	public PrototypeWorldSavedData(String name){
		super(name);
	}

	/**
	 * 
	 * @param world The world for the Prototype Dimension (ID 27)
	 * @return The PrototypeWorldSavedData instance. Returns null if, and only if, the world is NOT the world for the Prototype Dimension
	 */
	@Nullable
	public static PrototypeWorldSavedData get(World world){
		if(world == null || world.provider.getDimension() != 27){
			return null;
		}
		MapStorage storage = world.getPerWorldStorage();
		PrototypeWorldSavedData data = (PrototypeWorldSavedData) storage.getOrLoadData(PrototypeWorldSavedData.class, PROTOTYPE_ID);
		
		if (data == null) {
			data = new PrototypeWorldSavedData();
			storage.setData(PROTOTYPE_ID, data);
		}
		data.setDirty(true);
		return data;
	}
	
	/**
	 * Key: Chunk coordinates in long form <br>
	 * Value: PrototypeInfo for that chunk <br>
	 * 
	 * ONLY USE chunks where both the X and Z chunk coordinate are odd numbers in order to allow for barrier walls.
	 */
	public final HashMap<Long, PrototypeInfo> prototypeInfo = new HashMap<Long, PrototypeInfo>();
	
	public final HashMap<Long, WeakReference<IPrototypeOwner>> prototypeOwner = new HashMap<Long, WeakReference<IPrototypeOwner>>();
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		int i = 0;
		while(nbt.hasKey("chu" + i)){
			prototypeInfo.put(nbt.getLong("chu" + i), PrototypeInfo.readFromNBT(nbt.getCompoundTag("info" + i)));
			i++;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		int i = 0;
		for(Entry<Long, PrototypeInfo> mapping : prototypeInfo.entrySet()){
			nbt.setLong("chu" + i, mapping.getKey());
			nbt.setTag("info" + i, mapping.getValue().writeToNBT(new NBTTagCompound()));
			i++;
		}
		return nbt;
	}
}
