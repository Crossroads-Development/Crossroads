package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

public class ElementalReaction implements IReaction{

	private static final int COLOR_BOUND = 30;
	
	private final IElementReagent product;
	
	public ElementalReaction(IElementReagent product){
		this.product = product;
	}
	
	@Override
	public boolean performReaction(IReactionChamber chamber, boolean[] solvents){
		if(chamber.isCharged()){
			ReagentStack[] reags = chamber.getReagants();
			Color unit;
			if(product.getSecondaryBase() != null){
				if(reags[product.getSecondaryBase().getIndex()] == null){
					return false;
				}
				double secondAmount = reags[product.getSecondaryBase().getIndex()].getAmount();
				Color baseCol = product.getSecondaryBase().getAlignment().getTrueRGB();
				unit = new MagicUnit((int) (baseCol.getRed() * secondAmount + 255 * (reags[0] == null ? 0 : reags[0].getAmount())), (int) (baseCol.getGreen() * secondAmount + 255 * (reags[1] == null ? 0 : reags[1].getAmount())), (int) (baseCol.getBlue() * secondAmount + 255 * (reags[2] == null ? 0 : reags[2].getAmount())), 0).getTrueRGB();
			}else{
				unit = new MagicUnit((int) (255 * (reags[0] == null ? 0 : reags[0].getAmount())), (int) (255 * (reags[1] == null ? 0 : reags[1].getAmount())), (int) (255 * (reags[2] == null ? 0 : reags[2].getAmount())), 0).getTrueRGB();
			}
			
			Color goal = product.getAlignment().getTrueRGB();
			if(goal == null || unit == null){
				return false;
			}
			if(Math.abs(goal.getRed() - unit.getRed()) <= COLOR_BOUND && Math.abs(goal.getGreen() - unit.getGreen()) <= COLOR_BOUND && Math.abs(goal.getBlue() - unit.getBlue()) <= COLOR_BOUND){
				double created = 0;
				created += reags[0] == null ? 0 : reags[0].getAmount();
				created += reags[1] == null ? 0 : reags[1].getAmount();
				created += reags[2] == null ? 0 : reags[2].getAmount();
				if(product.getSecondaryBase() != null){
					created += reags[product.getSecondaryBase().getIndex()].getAmount();
					reags[product.getSecondaryBase().getIndex()] = null;
				}
				reags[0] = null;
				reags[1] = null;
				reags[2] = null;
				if(reags[product.getIndex()] == null){
					reags[product.getIndex()] = new ReagentStack(product, created);
				}else{
					reags[product.getIndex()].increaseAmount(created);
				}
			}
		}
		
		return false;
	}

}
