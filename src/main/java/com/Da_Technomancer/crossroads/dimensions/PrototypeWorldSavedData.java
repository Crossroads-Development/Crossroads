package com.Da_Technomancer.crossroads.dimensions;

import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;

public class PrototypeWorldSavedData extends WorldSavedData{

	//Everything related to the prototype system should be setup in such a way that should things go very wrong,
	//then if the folder for the prototype dimension is deleted, everything should work and all existing prototypes
	//will cleanly self-destruct. 

	private static final String PROTOTYPE_ID = Crossroads.MODID + "_prototype";
	private static PrototypeWorldSavedData instance;

	public PrototypeWorldSavedData(){
		super(PROTOTYPE_ID);
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

		ServerWorld world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(world == null){
			DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		}

		DimensionSavedDataManager storage = world.getSavedData();
		PrototypeWorldSavedData data = (PrototypeWorldSavedData) storage.getOrCreate(PrototypeWorldSavedData::new, PROTOTYPE_ID);
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

		ServerWorld world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(world == null){
			DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		}

		DimensionSavedDataManager storage = world.getSavedData();
		storage.set(this);
	}

	@Override
	public void read(CompoundNBT nbt){
		int length = nbt.getInt("length");
		for(int i = 0; i < length; i++){
			prototypes.add(nbt.contains("pro" + i) ? PrototypeInfo.readFromNBT(nbt.getCompound("pro" + i)) : null);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		nbt.putInt("length", prototypes.size());
		int i = 0;
		for(PrototypeInfo entry : prototypes){
			if(entry != null){
				nbt.put("pro" + i, entry.write(new CompoundNBT()));
			}
			i++;
		}
		return nbt;
	}
}
