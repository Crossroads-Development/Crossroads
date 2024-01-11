package com.Da_Technomancer.crossroads.api.alchemy;

import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores Alchemy reagents, along with temperature. Avoid using HashMap methods that aren't overwritten to minimize unintended behaviour
 */
public class ReagentMap extends HashMap<String, Integer>{

	private double heat = 0;
	private int totalQty;
	private Set<IReagent> keySetCache;

	public ReagentMap(){
		super(ReagentManager.getRegisteredReags().size());
	}

	public void transferReagent(String id, int amount, ReagentMap srcMap){
		amount = Math.min(amount, srcMap.getQty(id));

		addReagent(id, amount, srcMap.getTempC());
		srcMap.removeReagent(id, amount);
	}

	public void transferReagent(IReagent reag, int amount, ReagentMap srcMap){
		transferReagent(reag.getID(), amount, srcMap);
	}

	public int addReagent(String id, int amount, double srcTemp){
		if(id == null){
			return 0;
		}

		int current = getQty(id);
		current += amount;
		if(current < 0){
			current = 0;
		}
		heat += HeatUtil.toKelvin(srcTemp) * amount;
		put(id, current);
		return current;
	}

	public int addReagent(IReagent reag, int amount, double srcTemp){
		return addReagent(reag.getID(), amount, srcTemp);
	}

	public int removeReagent(String id, int amount){
		if(id == null){
			return 0;
		}

		int current = getQty(id);
		if(amount >= current){
			amount = current;
		}
		heat -= getTempK() * amount;
		current -= amount;
		put(id, current);
		if(heat < 0){
			heat = 0;
		}
		if(totalQty <= 0){
			heat = 0;
			totalQty = 0;
		}
		return current;
	}

	public int removeReagent(IReagent reag, int amount){
		return removeReagent(reag.getID(), amount);
	}

	public ReagentStack getStack(String id){
		return new ReagentStack(id, getQty(id));
	}

	public ReagentStack getStack(IReagent reag){
		return new ReagentStack(reag, getQty(reag));
	}

	public void setTemp(double tempC){
		heat = totalQty * HeatUtil.toKelvin(tempC);
	}

	@Override
	public Integer get(Object key){
		if(key instanceof IReagent){
			key = ((IReagent) key).getID();
		}
		return super.get(key);
	}

	@Override
	public Integer put(String key, Integer value){
		if(key == null || value == null || value < 0){
			return 0;
		}
		int prevQty = getQty(key);
		totalQty -= prevQty;
		totalQty += value;
		if(keySetCache != null){
			if(prevQty != 0 && value == 0){
				keySetCache.remove(ReagentManager.getReagent(key));
			}else if(prevQty == 0 && value != 0){
				keySetCache.add(ReagentManager.getReagent(key));
			}
		}
		return super.put(key, value);
	}

	public Integer put(IReagent key, Integer value){
		return put(key.getID(), value);
	}

	@Override
	public Integer remove(Object key){
		IReagent reag = null;
		String strKey = null;
		if(key instanceof IReagent){
			reag = (IReagent) key;
		}else if(key instanceof String){
			strKey = (String) key;
		}
		Integer qty = super.remove(key);
		if(qty != null){
			heat -= getTempK() * qty;
			totalQty -= qty;
			if(keySetCache != null){
				keySetCache.remove(reag == null ? ReagentManager.getReagent(strKey) : reag);
			}
		}
		return qty;
	}

	@Override
	public void clear(){
		totalQty = 0;
		heat = 0;
		keySetCache = null;
		super.clear();
	}

	@Override
	public boolean isEmpty(){
		return totalQty == 0;
	}

	public int getQty(String id){
		Integer raw = get(id);
		return raw == null ? 0 : raw;
	}

	public int getQty(IReagent reag){
		return getQty(reag.getID());
	}

	public int getTotalQty(){
		return totalQty;
	}

	public double getHeat(){
		return heat;
	}

	public double getTempC(){
		return HeatUtil.toCelcius(getTempK());
	}

	public double getTempK(){
		return totalQty == 0 ? 0 : heat / totalQty;
	}

	/**
	 * Wipes the caches and recalcutes
	 * Should only be necessary if modified using a non-overriden method
	 */
	public void refresh(){
		totalQty = 0;
		keySetCache = null;
		for(Integer qty : values()){
			totalQty += qty;
		}
		if(totalQty <= 0){
			heat = 0;
		}
	}

	public CompoundTag write(CompoundTag nbt){
		nbt.putDouble("he", heat);
		for(String key : keySet()){
			int qty = get(key);
			if(qty > 0){//No point saving the empty entries
				nbt.putInt("qty_" + key, qty);
			}
		}

		return nbt;
	}

	public static ReagentMap readFromNBT(CompoundTag nbt){
		ReagentMap map = new ReagentMap();
		if(nbt == null){
			return map;
		}
		map.heat = nbt.getDouble("he");
		for(String key : nbt.getAllKeys()){
			if(!key.startsWith("qty_")){
				continue;
			}
			map.put(key.substring(4), nbt.getInt(key));
		}
		map.refresh();

		return map;
	}

	@Override
	public boolean containsKey(Object key){
		if(key instanceof IReagent){
			key = ((IReagent) key).getID();
		}
		return super.containsKey(key);
	}

	/**
	 *
	 * @return Set of all reagents present in non-zero quantity. Do not modify the returned value
	 */
	public Set<IReagent> keySetReag(){
		if(keySetCache == null){
			keySetCache = entrySet().stream().filter((entry) -> entry.getValue() > 0).map(Entry::getKey).map(ReagentManager::getReagent).filter(Objects::nonNull).collect(Collectors.toSet());
		}
		return keySetCache;
	}
}
