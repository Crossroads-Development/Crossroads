package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

public class ElementalReaction implements IReaction{

	private static final int COLOR_BOUND = 30;
	
	private final IElementReagent product;
	@Nullable
	private final IElementReagent secondaryBase;
	
	public ElementalReaction(IElementReagent product, @Nullable IElementReagent secondaryBase){
		this.product = product;
		this.secondaryBase = secondaryBase;
	}
	
	@Override
	public boolean performReaction(IReactionChamber chamber, boolean[] solvents){
		if(chamber.isCharged()){
			ReagentStack[] reags = chamber.getReagants();
			Color unit;
			if(secondaryBase != null){
				if(reags[secondaryBase.getIndex()] == null){
					return false;
				}
				double secondAmount = reags[secondaryBase.getIndex()].getAmount();
				Color baseCol = secondaryBase.getAlignment().getTrueRGB();
				unit = new MagicUnit((int) (baseCol.getRed() * secondAmount + 255 * (reags[0] == null ? 0 : reags[0].getAmount())), (int) (baseCol.getGreen() * secondAmount + 255 * (reags[1] == null ? 0 : reags[1].getAmount())), (int) (baseCol.getBlue() * secondAmount + 255 * (reags[2] == null ? 0 : reags[2].getAmount())), 0).getTrueRGB();
			}else{
				unit = new MagicUnit((int) (255 * (reags[0] == null ? 0 : reags[0].getAmount())), (int) (255 * (reags[1] == null ? 0 : reags[1].getAmount())), (int) (255 * (reags[2] == null ? 0 : reags[2].getAmount())), 0).getTrueRGB();
			}
			
			Color goal = product.getAlignment().getTrueRGB();
			
			if(Math.abs(goal.getRed() - unit.getRed()) <= COLOR_BOUND && Math.abs(goal.getGreen() - unit.getGreen()) <= COLOR_BOUND && Math.abs(goal.getBlue() - unit.getBlue()) <= COLOR_BOUND){
				double created = 0;
				created += reags[0] == null ? 0 : reags[0].getAmount();
				created += reags[1] == null ? 0 : reags[1].getAmount();
				created += reags[2] == null ? 0 : reags[2].getAmount();
				if(secondaryBase != null){
					created += reags[secondaryBase.getIndex()].getAmount();
					reags[secondaryBase.getIndex()] = null;
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
