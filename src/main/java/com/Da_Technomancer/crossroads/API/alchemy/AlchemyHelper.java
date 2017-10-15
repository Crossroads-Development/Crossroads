package com.Da_Technomancer.crossroads.API.alchemy;

public class AlchemyHelper{

	public static final double MIN_QUANTITY = 0.05D;

	public static void updateContents(IReactionChamber chamber, double extraHeat){
		updateContents(chamber, extraHeat, extraHeat < 0 ? -273D : Short.MAX_VALUE);
	}
	
	public static void updateContents(IReactionChamber chamber, double extraHeat, double goalTemp){
		double totalHeat = 0;
		double totalAmount = 0D;
		Reagent[] reagents = chamber.getReagants();
		boolean glassChamber = chamber.isGlass();

		for(Reagent reag : reagents){
			if(reag == null){
				continue;
			}

			totalHeat += (273D + reag.getTemp()) * reag.getAmount();
			totalAmount += reag.getAmount();
		}

		if(totalAmount <= 0){
			return;
		}

		double endTemp = (totalHeat / totalAmount) - 273D;
		if(extraHeat >= 0){
			totalHeat += Math.max(0, Math.min(extraHeat, (goalTemp - endTemp) * totalAmount));
			endTemp = (totalHeat / totalAmount) - 273D;
		}else{
			totalHeat += Math.min(0, Math.max(extraHeat, (goalTemp - endTemp) * totalAmount));
			endTemp = (totalHeat / totalAmount) - 273D;
		}

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

		for(Reagent reag : reagents){
			if(reag != null && reag.getAmount() >= MIN_QUANTITY){
				IReagentType type = reag.getType();
				if(type == AlchemyCraftingManager.REAGENTS[11]){
					hasAquaRegia = true;
				}
				if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp){
					SolventType solv = type.solventType();
					hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
					hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
					hasAquaRegia |= solv == SolventType.AQUA_REGIA;
				}
				reag.setTemp(endTemp);
			}
		}
		
		hasAquaRegia &= hasPolar;

		for(int i = 0; i < reagents.length; i++){
			Reagent reag = reagents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(hasPolar, hasNonPolar, hasAquaRegia);
			if(glassChamber && !reag.getType().canGlassContain()){
				if(reag.getType().destroysBadContainer()){
					chamber.destroyChamber();
					return;
				}
				reagents[i] = null;
			}
		}
	}
}
