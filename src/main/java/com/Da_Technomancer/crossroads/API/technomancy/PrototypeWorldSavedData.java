package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class PrototypeWorldSavedData extends WorldSavedData{
	
	//Everything related to the prototype system should be setup in such a way that should things go very wrong,
	//then if the folder for the prototype dimension is deleted, everything should work and all existing prototypes
	//will cleanly self-destruct. 
	
	public static final String PROTOTYPE_ID = Main.MODID + "_prototype";
	public static Ticket loadingTicket;
	
	public PrototypeWorldSavedData(){
		super(PROTOTYPE_ID);
	}
	
	public PrototypeWorldSavedData(String name){
		super(name);
	}

	/**
	 * DOES NOT call markDirty() by default unless just being created, modifiers should call it manually.
	 * @param world The world for the Prototype Dimension (ID 27)
	 * @return The PrototypeWorldSavedData instance. Returns null if, and only if, the world is NOT the world for the Prototype Dimension
	 */
	@Nullable
	public static PrototypeWorldSavedData get(World world){
		if(world == null || world.provider.getDimension() != ModDimensions.PROTOTYPE_DIM_ID){
			return null;
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
