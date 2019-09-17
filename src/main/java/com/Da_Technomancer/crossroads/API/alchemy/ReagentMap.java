package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;

/**
 * Stores Alchemy reagents, along with temperature. Avoid using HashMap methods that aren't overwritten to minimize unintended behaviour
 */
public class ReagentMap extends HashMap<IReagent, Integer>{

	public ReagentMap(){
		super(AlchemyCore.REAGENT_COUNT);
	}

	private double heat;
	private int totalQty;

	public void transferReagent(String id, int amount, ReagentMap srcMap){
		transferReagent(AlchemyCore.REAGENTS.get(id), amount, srcMap);
	}

	public void transferReagent(IReagent reag, int amount, ReagentMap srcMap){
		amount = Math.min(amount, srcMap.getQty(reag));

		addReagent(reag, amount, srcMap.getTempC());
		srcMap.removeReagent(reag, amount);
	}

	public int addReagent(String id, int amount, double srcTemp){
		return addReagent(AlchemyCore.REAGENTS.get(id), amount, srcTemp);
	}

	public int addReagent(IReagent reag, int amount, double srcTemp){
		if(reag == null){
			return 0;
		}

		int current = getQty(reag);
		current += amount;
		if(current < 0){
			current = 0;
		}
		heat += HeatUtil.toKelvin(srcTemp) * amount;
		put(reag, current);
		return current;
	}

	public int removeReagent(String id, int amount){
		return removeReagent(AlchemyCore.REAGENTS.get(id), amount);
	}

	public int removeReagent(IReagent reag, int amount){
		if(reag == null){
			return 0;
		}

		int current = getQty(reag);
		if(amount > current){
			amount = current;
		}
		heat -= getTempK() * amount;
		current -= amount;
		put(reag, current);
		if(heat < 0){
			heat = 0;
		}
		if(totalQty <= 0){
			heat = 0;
			totalQty = 0;
		}
		return current;
	}

	public ReagentStack getStack(String id){
		return getStack(AlchemyCore.REAGENTS.get(id));
	}

	public void setTemp(double tempC){
		heat = totalQty * HeatUtil.toKelvin(tempC);
	}

	@Override
	public Integer put(IReagent key, Integer value){
		if(key == null || value == null || value < 0){
			return 0;
		}
		totalQty -= getQty(key);
		totalQty += value;
		return super.put(key, value);
	}

	@Override
	public Integer remove(Object key){
		Integer qty = super.remove(key);
		if(qty != null){
			heat -= getTempK() * qty;
			totalQty -= qty;
		}
		return qty;
	}

	@Override
	public void clear(){
		totalQty = 0;
		heat = 0;
		super.clear();
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

	public void refresh(){
		totalQty = 0;
		for(Integer qty : values()){
			totalQty += qty;
		}
	}

	public CompoundNBT write(CompoundNBT nbt){
		nbt.putDouble("he", heat);
		for(IReagent key : keySet()){
			int qty = get(key);
			if(qty > 0){
				nbt.putInt("qty_" + key.getId(), qty);
			}
		}

		return nbt;
	}

	public static ReagentMap readFromNBT(CompoundNBT nbt){
		ReagentMap map = new ReagentMap();
		map.heat = nbt.getDouble("he");
		for(String key : nbt.getKeySet()){
			if(!key.startsWith("qty_")){
				continue;
			}
			map.put(AlchemyCore.REAGENTS.get(key.substring(4)), nbt.getInt(key));
		}

		return map;
	}
}
