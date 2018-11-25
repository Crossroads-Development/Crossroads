package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class ReagentMap extends HashMap<IReagent, Integer>{

	public ReagentMap(){
		super(AlchemyCore.REAGENT_COUNT);
	}

	public int addReagent(String id, int amount){
		return addReagent(AlchemyCore.REAGENTS.get(id), amount);
	}

	public int addReagent(IReagent reag, int amount){
		if(reag == null){
			return 0;
		}

		int current = getQty(reag);
		current += amount;
		if(current < 0){
			current = 0;
		}
		put(reag, current);
		return current;
	}

	public ReagentStack getStack(String id){
		return getStack(AlchemyCore.REAGENTS.get(id));
	}

	@Override
	public Integer put(IReagent key, Integer value){
		if(key == null || value == null || value < 0){
			return 0;
		}
		return super.put(key, value);
	}

	public ReagentStack getStack(IReagent reag){
		return new ReagentStack(reag, getQty(reag));
	}

	public int getQty(String id){
		return getQty(AlchemyCore.REAGENTS.get(id));
	}

	public int getQty(IReagent reag){
		Integer raw = get(reag);
		return raw == null ? 0 : raw;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(IReagent key : keySet()){
			int qty = get(key);
			if(qty > 0){
				nbt.setInteger("qty_" + key.getId(), qty);
			}
		}

		return nbt;
	}

	public static ReagentMap readFromNBT(NBTTagCompound nbt){
		ReagentMap map = new ReagentMap();
		for(String key : nbt.getKeySet()){
			if(!key.startsWith("qty_")){
				continue;
			}
			map.addReagent(key.substring(4), nbt.getInteger(key));
		}

		return map;
	}
}
