package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class SimpleReaction implements IReaction{

	private final double heatChange;
	private final double minTemp;
	private final double maxTemp;
	private final IReagentType cat;
	private final boolean charged;
	private final Triple<IReagentType, Double, Boolean> ingrOne;
	private final Triple<IReagentType, Double, Boolean> ingrTwo;
	private final Triple<IReagentType, Double, Boolean> ingrThree;
	private final Pair<IReagentType, Double> outOne;
	private final Pair<IReagentType, Double> outTwo;
	private final Pair<IReagentType, Double> outThree;
	private final double amountChange;

	/**
	 * @param heatChange Change in heat per reaction (negative to increase). 
	 * @param minTemp Minimum temperature this reaction can occur at. Set below absolute zero to have no minimum. 
	 * @param maxTemp Maximum temperature this reaction can occur at. Set below absolute zero to have no maximum. 
	 * @param cat The required catalyst. 
	 * @param charged If this needs to be in a charged chamber. 
	 * @param ingrOne First ingredient. ReagentType, amount per reaction (partial reactions are allowed), whether it needs to be a solute (Boolean must never be null). 
	 * @param ingrTwo Second ingredient, null for none. ReagentType, amount per reaction (partial reactions are allowed), whether it needs to be a solute (Boolean must never be null). 
	 * @param ingrThree Third ingredient, null for none. ReagentType, amount per reaction (partial reactions are allowed), whether it needs to be a solute (Boolean must never be null). 
	 * @param outOne First output. ReagentType, amount per reaction. 
	 * @param outTwo Second output, null for none. ReagentType, amount per reaction. 
	 * @param outThree Third output, null for none. ReagentType, amount per reaction. 
	 */
	public SimpleReaction(double heatChange, double minTemp, double maxTemp, @Nullable IReagentType cat, boolean charged, @Nonnull Triple<IReagentType, Double, Boolean> ingrOne, @Nullable Triple<IReagentType, Double, Boolean> ingrTwo, @Nullable Triple<IReagentType, Double, Boolean> ingrThree, @Nonnull Pair<IReagentType, Double> outOne, @Nullable Pair<IReagentType, Double> outTwo, @Nullable Pair<IReagentType, Double> outThree){
		this.heatChange = heatChange;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.cat = cat;
		this.charged = charged;
		this.ingrOne = ingrOne;
		this.ingrTwo = ingrTwo;
		this.ingrThree = ingrThree;
		this.outOne = outOne;
		this.outTwo = outTwo;
		this.outThree = outThree;
		this.amountChange = outOne.getRight() + (outTwo == null ? 0 : outTwo.getRight()) + (outThree == null ? 0 : outThree.getRight()) - ingrOne.getMiddle() - (ingrTwo == null ? 0 : ingrTwo.getMiddle()) - (ingrThree == null ? 0 : ingrThree.getMiddle());
	}

	@Override
	public boolean performReaction(IReactionChamber chamb){
		if((charged && !chamb.isCharged()) || (cat != null && (chamb.getCatalyst() == null || chamb.getCatalyst().getType() != cat))){
			return false;
		}
		Reagent[] reags = chamb.getReagants();
		int indF = ingrOne.getLeft().getIndex();
		int indS = ingrTwo == null ? -1 : ingrTwo.getLeft().getIndex();
		int indT = ingrThree == null ? -1 : ingrThree.getLeft().getIndex();
		if(reags[indF] == null || (indS != -1 && reags[indS] == null) || (indT != -1 && reags[indT] == null)){
			return false;
		}
		double temp = chamb.getTemp();
		if(temp < minTemp || (maxTemp > -273 && temp > maxTemp)){
			return false;
		}
		if((ingrOne.getRight() && reags[indF].getPhase(temp) != EnumMatterPhase.SOLUTE) || (ingrTwo != null && ingrTwo.getRight() && reags[indS].getPhase(temp) != EnumMatterPhase.SOLUTE) || (ingrThree != null && ingrThree.getRight() && reags[indT].getPhase(temp) != EnumMatterPhase.SOLUTE)){
			return false;
		}
		double amount = Math.min(Math.min(reags[indF].getAmount() / ingrOne.getMiddle(), indS == -1 ? 500D : reags[indS].getAmount() / ingrTwo.getMiddle()), indT == -1 ? 500D : reags[indT].getAmount() / ingrThree.getMiddle());

		if(reags[indF].increaseAmount(-amount * ingrOne.getMiddle()) <= 0){
			reags[indF] = null;
		}
		if(ingrTwo != null && reags[indS].increaseAmount(-amount * ingrTwo.getMiddle()) <= 0){
			reags[indS] = null;
		}
		if(ingrThree != null && reags[indT].increaseAmount(-amount * ingrThree.getMiddle()) <= 0){
			reags[indT] = null;
		}

		int outOneInd = outOne.getLeft().getIndex();
		if(reags[outOneInd] == null){
			reags[outOneInd] = new Reagent(outOne.getLeft(), outOne.getRight() * amount);
		}else{
			reags[outOneInd].increaseAmount(outOne.getRight() * amount);
		}
		if(outTwo != null){
			int outTwoInd = outTwo.getKey().getIndex();
			if(reags[outTwoInd] == null){
				reags[outTwoInd] = new Reagent(outTwo.getLeft(), outTwo.getRight() * amount);
			}else{
				reags[outTwoInd].increaseAmount(outTwo.getRight() * amount);
			}
		}
		if(outThree != null){
			int outThreeInd = outThree.getLeft().getIndex();
			if(reags[outThreeInd] == null){
				reags[outThreeInd] = new Reagent(outThree.getLeft(), outThree.getRight() * amount);
			}else{
				reags[outThreeInd].increaseAmount(outThree.getRight() * amount);
			}
		}

		chamb.addHeat((amountChange * temp * amount) - (heatChange * amount * 15D));//15 was picked somewhat arbitrarily as a conversion factor from J/mol * mol to *C
		return true;
	}
}