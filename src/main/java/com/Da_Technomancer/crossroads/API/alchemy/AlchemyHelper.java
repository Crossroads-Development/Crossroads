package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.HashMap;
import java.util.List;

public class AlchemyHelper{

	public static final double MIN_QUANTITY = 0.05D;
	
	public static void updateContents(IReactionChamber chamber, double extraHeat){
		double totalHeat = extraHeat;
		double totalAmount = 0D;
		List<IReagent> reagents = chamber.getNonSolidReagants();
		HashMap<IReagentType, IReagent> merged = new HashMap<IReagentType, IReagent>();
		boolean glassChamber = chamber.isGlass();

		IReagent solid = chamber.getSolid();
		totalHeat += solid == null ? 0 : (273D + solid.getTemp()) * solid.getAmount();
		totalAmount += solid == null ? 0 : solid.getAmount();

		for(IReagent reag : reagents){
			if(reag == null){
				continue;
			}
			if(merged.containsKey(reag.getType())){
				merged.get(reag.getType()).increaseAmount(reag.getAmount());
			}else{
				merged.put(reag.getType(), reag);
			}

			totalHeat += (273D + reag.getTemp()) * reag.getAmount();
			totalAmount += reag.getAmount();
		}

		reagents.clear();

		if(totalAmount <= 0){
			return;
		}

		double endTemp = (totalHeat / totalAmount) - 273D;

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;

		for(IReagent reag : merged.values()){
			if(reag.getAmount() >= MIN_QUANTITY){
				IReagentType type = reag.getType();
				if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp){
					SolventType solv = type.solventType();
					hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
					hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
					hasAquaRegia |= solv == SolventType.AQUA_REGIA;
				}
				reag.setTemp(endTemp);
			}
		}

		if(solid != null){
			solid.setTemp(endTemp);
			solid.updatePhase(hasPolar, hasNonPolar, hasAquaRegia);
			if(solid.getPhase() != MatterPhase.SOLID){
				chamber.clearSolid();
				if(merged.containsKey(solid.getType())){
					merged.get(solid.getType()).increaseAmount(solid.getAmount());
				}else{
					merged.put(solid.getType(), solid);
				}
				solid = null;
			}
		}
		
		if(glassChamber && solid != null && !solid.getType().canGlassContain()){
			chamber.clearSolid();
			if(solid.getType().destroysBadContainer()){
				chamber.destroyChamber();
				solid.getType().onRelease(chamber.getWorld(), chamber.getPos(), solid.getAmount(), MatterPhase.SOLID);
			}
			solid = null;
		}

		for(IReagent reag : merged.values()){
			reag.updatePhase(hasPolar, hasNonPolar, hasAquaRegia);
			if(glassChamber && !reag.getType().canGlassContain()){
				if(reag.getType().destroysBadContainer()){
					chamber.destroyChamber();
					reag.getType().onRelease(chamber.getWorld(), chamber.getPos(), reag.getAmount(), reag.getPhase());
				}
				continue;
			}
			
			if(reag.getPhase() == MatterPhase.SOLID){
				chamber.addSolid(reag);
			}else{
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
