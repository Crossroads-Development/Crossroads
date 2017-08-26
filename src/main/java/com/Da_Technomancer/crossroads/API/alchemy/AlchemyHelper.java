package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.HashMap;
import java.util.List;

public class AlchemyHelper{

	public static void adjustTemperature(IReactionChamber chamber, double extraHeat){
		double totalHeat = extraHeat;
		double totalAmount = 0D;

		IReagent solid = chamber.getSolid();
		totalHeat += solid == null ? 0 : (273D + solid.getTemp()) * solid.getAmount();
		totalAmount += solid == null ? 0 : solid.getAmount();

		for(IReagent reagent : chamber.getNonSolidReagants()){
			totalHeat += reagent == null ? 0 : (273D + reagent.getTemp()) * reagent.getAmount();
			totalAmount += reagent == null ? 0 : reagent.getAmount();
		}

		if(totalAmount == 0){
			return;
		}
		
		double endTemp = totalHeat / totalAmount;
		endTemp -= 273D;

		if(solid != null){
			solid.setTemp(endTemp);
		}

		for(IReagent reagent : chamber.getNonSolidReagants()){
			if(reagent != null){
				reagent.setTemp(endTemp);
			}
		}
	}
	
	public static final double MIN_QUANTITY = 0.05D;
	
	/**
	 * Does not call adjustTemperature. That should be called before-hand. 
	 * @param reagents
	 */
	public static void mergeReagents(List<IReagent> reagents){
		HashMap<IReagentType, IReagent> merged = new HashMap<IReagentType, IReagent>();
		for(IReagent reag : reagents){
			if(reag == null){
				continue;
			}
			if(merged.containsKey(reag.getType())){
				merged.get(reag.getType()).increaseAmount(reag.getAmount());
			}else{
				merged.put(reag.getType(), reag);
			}
		}
		
		reagents.clear();
		
		for(IReagent reag : merged.values()){
			if(reag.getAmount() >= MIN_QUANTITY){
				reagents.add(reag);
			}
		}
	}
	
	/**
	 * Does not call adjustTemperature and mergeReagents. They should be called before hand. 
	 * 
	 * @param reagents
	 * @param type
	 * @param phase
	 * @return
	 */
	public static double reagentContent(List<IReagent> reagents, IReagentType type, MatterPhase phase){
		for(IReagent reag : reagents){
			if(reag != null && reag.getType() == type && reag.getPhase() == phase){
				return reag.getAmount();
			}
		}
		
		return 0D;
	}
}
