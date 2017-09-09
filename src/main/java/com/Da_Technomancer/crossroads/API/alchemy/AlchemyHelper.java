package com.Da_Technomancer.crossroads.API.alchemy;

public class AlchemyHelper{

	public static final double MIN_QUANTITY = 0.05D;

	public static void updateContents(IReactionChamber chamber, double extraHeat){
		double totalHeat = extraHeat;
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

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;

		for(Reagent reag : reagents){
			if(reag != null && reag.getAmount() >= MIN_QUANTITY){
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

		for(int i = 0; i < reagents.length; i++){
			Reagent reag = reagents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(hasPolar, hasNonPolar, hasAquaRegia);
			if(glassChamber && !reag.getType().canGlassContain()){
				if(reag.getType().destroysBadContainer()){
					chamber.destroyChamber();
					reag.getType().onRelease(chamber.getWorld(), chamber.getPos(), reag.getAmount(), reag.getPhase());
				}
				reagents[i] = null;
			}
		}
	}
}
