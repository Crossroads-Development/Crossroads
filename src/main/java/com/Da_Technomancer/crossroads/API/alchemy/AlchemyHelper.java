package com.Da_Technomancer.crossroads.API.alchemy;

public class AlchemyHelper{

	public static final double MIN_QUANTITY = 0.005D;

	public static void updatePhase(IReactionChamber chamber){
		ReagentStack[] reagents = chamber.getReagants();
		boolean glassChamber = chamber.isGlass();

		double endTemp = chamber.getTemp();

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = reagents[i];
			if(reag != null){
				if(reag.getAmount() >= MIN_QUANTITY){
					IReagent type = reag.getType();
					hasAquaRegia |= i == 11;
					
					if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp){
						SolventType solv = type.solventType();
						hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
						hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
						hasAquaRegia |= solv == SolventType.AQUA_REGIA;
					}
				}else{
					chamber.addHeat(-((endTemp + 273D) * reag.getAmount()));
					reagents[i] = null;
				}
			}
		}

		hasAquaRegia &= hasPolar;

		boolean destroy = false;

		for(int i = 0; i < reagents.length; i++){
			ReagentStack reag = reagents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(endTemp, hasPolar, hasNonPolar, hasAquaRegia);
			if(glassChamber && !reag.getType().canGlassContain()){
				destroy |= reag.getType().destroysBadContainer();
				reagents[i] = null;
			}
		}

		if(destroy || chamber.getIntegrityCapacity() < chamber.getContent()){
			chamber.destroyChamber();
			return;
		}
	}

	/** Assumes the chamber has had {@link AlchemyHelper#updateContents(IReactionChamber, double)} called to fix the contents first. This calls it every time it changes the contents.
	 * 
	 * @param chamber
	 * @param passes
	 * The maximum number of reactions to do.
	 */
	public static void performReaction(IReactionChamber chamber, int passes){
		for(int pass = 0; pass < passes; passes++){
			boolean operated = false;
			for(IReaction react : AlchemyCore.REACTIONS){
				if(react.performReaction(chamber)){
					updatePhase(chamber);
					operated = true;
					break;
				}
			}
			if(!operated){
				break;
			}
		}
	}
}
