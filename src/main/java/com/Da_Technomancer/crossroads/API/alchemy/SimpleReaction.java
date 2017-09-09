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
	private final Triple<IReagentType, Double, MatterPhase> ingrOne;
	private final Triple<IReagentType, Double, MatterPhase> ingrTwo;
	private final Triple<IReagentType, Double, MatterPhase> ingrThree;
	private final Pair<IReagentType, Double> outOne;
	private final Pair<IReagentType, Double> outTwo;
	private final Pair<IReagentType, Double> outThree;

	/**
	 * @param heatChange Change in heat per reaction (negative to increase). 
	 * @param minTemp Minimum temperature this reaction can occur at. Set below absolute zero to have no minimum. 
	 * @param maxTemp Maximum temperature this reaction can occur at. Set below absolute zero to have no maximum. 
	 * @param cat The required catalyst. 
	 * @param charged If this needs to be in a charged chamber. 
	 * @param ingrOne First ingredient. ReagentType, amount per reaction (partial reactions are allowed), required phase (null to ignore). 
	 * @param ingrTwo Second ingredient, null for none. ReagentType, amount per reaction (partial reactions are allowed), required phase (null to ignore). 
	 * @param ingrThree Third ingredient, null for none. ReagentType, amount per reaction (partial reactions are allowed), required phase (null to ignore). 
	 * @param outOne First output. ReagentType, amount per reaction. 
	 * @param outTwo Second output, null for none. ReagentType, amount per reaction. 
	 * @param outThree Third output, null for none. ReagentType, amount per reaction. 
	 */
	public SimpleReaction(double heatChange, double minTemp, double maxTemp, @Nullable IReagentType cat, boolean charged, @Nonnull Triple<IReagentType, Double, MatterPhase> ingrOne, @Nullable Triple<IReagentType, Double, MatterPhase> ingrTwo, @Nullable Triple<IReagentType, Double, MatterPhase> ingrThree, @Nonnull Pair<IReagentType, Double> outOne, @Nullable Pair<IReagentType, Double> outTwo, @Nullable Pair<IReagentType, Double> outThree){
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
	}

	@Override
	public double performReaction(IReactionChamber chamb){
		if((charged && !chamb.isCharged()) || (cat != null && (chamb.getCatalyst() == null || chamb.getCatalyst().getType() != cat))){
			return 0;
		}
		Reagent[] reags = chamb.getReagants();
		int indF = ingrOne.getLeft().getIndex();
		int indS = ingrTwo == null ? -1 : ingrTwo.getLeft().getIndex();
		int indT = ingrThree == null ? -1 : ingrThree.getLeft().getIndex();
		if(reags[indF] == null || (indS != -1 && reags[indS] == null) || (indT != -1 && reags[indT] == null)){
			return 0;
		}
		double temp = reags[indF].getTemp();
		if(temp < minTemp || (maxTemp > -273 && temp > maxTemp)){
			return 0;
		}
		if((ingrOne.getRight() != null && reags[indF].getPhase() != ingrOne.getRight()) || (ingrTwo != null && ingrTwo.getRight() != null && reags[indS].getPhase() != ingrTwo.getRight()) || (ingrThree != null && ingrThree.getRight() != null && reags[indT].getPhase() != ingrThree.getRight())){
			return 0;
		}
		double amount = Math.min(Math.min(reags[indF].getAmount() / ingrOne.getMiddle(), indS == -1 ? 500D : reags[indS].getAmount() / ingrTwo.getMiddle()), indT == -1 ? 500D : reags[indT].getAmount() / ingrThree.getMiddle());

		if(reags[indF].increaseAmount(-amount * ingrOne.getMiddle(), temp) <= 0){
			reags[indF] = null;
		}
		if(ingrTwo != null && reags[indS].increaseAmount(-amount * ingrTwo.getMiddle(), temp) <= 0){
			reags[indS] = null;
		}
		if(ingrThree != null && reags[indT].increaseAmount(-amount * ingrThree.getMiddle(), temp) <= 0){
			reags[indT] = null;
		}

		int outOneInd = outOne.getLeft().getIndex();
		int outTwoInd = outTwo == null ? -1 : outTwo.getLeft().getIndex();
		int outThreeInd = outThree == null ? -1 : outThree.getLeft().getIndex();

		if(reags[outOneInd] == null){
			reags[outOneInd] = new Reagent(outOne.getLeft(), temp, outOne.getRight() * amount);
		}else{
			reags[outOneInd].increaseAmount(outOne.getRight() * amount, temp);
		}
		if(outTwoInd != -1){
			if(reags[outTwoInd] == null){
				reags[outTwoInd] = new Reagent(outTwo.getLeft(), temp, outTwo.getRight() * amount);
			}else{
				reags[outTwoInd].increaseAmount(outTwo.getRight() * amount, temp);
			}
		}
		if(outThreeInd != -1){
			if(reags[outThreeInd] == null){
				reags[outThreeInd] = new Reagent(outThree.getLeft(), temp, outThree.getRight() * amount);
			}else{
				reags[outThreeInd].increaseAmount(outThree.getRight() * amount, temp);
			}
		}

		return heatChange * amount * 15D;
	}
}