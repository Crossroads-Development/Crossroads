package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;

public class PrototypeWorldSavedData extends WorldSavedData{
	
	//Everything related to the prototype system should be setup in such a way that should things go very wrong,
	//then if the folder for the prototype dimension is deleted, everything should work and all existing prototypes
	//will cleanly self-destruct. 
	
	public static final String PROTOTYPE_ID = Main.MODID + "_prototype";
	public PrototypeWorldSavedData(){
		super(PROTOTYPE_ID);
	}
	
	public PrototypeWorldSavedData(String name){
		super(name);
	}

	/**
	 * DOES NOT call markDirty() by default unless just being created, modifiers should call it manually.
	 * Do not call until after the overworld has been initialized.
	 * Designed to lose all data if the prototype dimension file is deleted.
	 * @return The PrototypeWorldSavedData instance.
	 */
	public static PrototypeWorldSavedData get(){
		WorldServer world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(world == null){
			DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		}
		
		MapStorage storage = world.getPerWorldStorage();
		PrototypeWorldSavedData data = (PrototypeWorldSavedData) storage.getOrLoadData(PrototypeWorldSavedData.class, PROTOTYPE_ID);
		
		if (data == null) {
			data = new PrototypeWorldSavedData();
			storage.setData(PROTOTYPE_ID, data);
			data.markDirty();
		}
		return data;
	}
	
	/**
	 * A null value represents a wiped prototype. Elements should never be removed from this list, but may be replaced. 
	 */
	public final ArrayList<PrototypeInfo> prototypes = new ArrayList<PrototypeInfo>();
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		int length = nbt.getInteger("length");
		for(int i = 0; i < length; i++){
			prototypes.add(nbt.hasKey("pro" + i) ? PrototypeInfo.readFromNBT(nbt.getCompoundTag("pro" + i)) : null);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("length", prototypes.size());
		int i = 0;
		for(PrototypeInfo entry : prototypes){
			if(entry != null){
				nbt.setTag("pro" + i, entry.writeToNBT(new NBTTagCompound()));
			}
			i++;
		}
		return nbt;
	}
}
