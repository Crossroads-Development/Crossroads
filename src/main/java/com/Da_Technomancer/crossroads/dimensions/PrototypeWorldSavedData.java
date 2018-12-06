package com.Da_Technomancer.crossroads.dimensions;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class PrototypeWorldSavedData extends WorldSavedData{

	//Everything related to the prototype system should be setup in such a way that should things go very wrong,
	//then if the folder for the prototype dimension is deleted, everything should work and all existing prototypes
	//will cleanly self-destruct. 

	public static final String PROTOTYPE_ID = Main.MODID + "_prototype";
	private static PrototypeWorldSavedData instance;

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
	 * @param forceInitWorld If true, this will initHeat (load) the prototype world even if this doesn't have to.
	 * @return The PrototypeWorldSavedData instance.
	 */
	public static PrototypeWorldSavedData get(boolean forceInitWorld){
		if(instance != null){
			if(forceInitWorld && DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID) == null){
				DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			}
			return instance;
		}

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
			data.setDirty(true);
		}
		instance = data;
		return data;
	}

	/**
	 * A null value represents a wiped prototype. Elements should never be removed from this list, but may be replaced. 
	 */
	public final ArrayList<PrototypeInfo> prototypes = new ArrayList<PrototypeInfo>();


	/**
	 * Also causes the dimension to load in order to force the data to save. Use instead of setDirty(true).
	 */
	@Override
	public void markDirty(){
		setDirty(true);

		WorldServer world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(world == null){
			DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		}

		MapStorage storage = world.getPerWorldStorage();
		storage.setData(PROTOTYPE_ID, this);
	}

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
