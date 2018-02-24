package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.items.alchemy.CustomMaterial;

import net.minecraft.item.ItemStack;

public class CrystalFormationReaction implements IReaction{

	@Override
	public boolean performReaction(IReactionChamber chamber, boolean[] solvents){
		ReagentStack[] reags = chamber.getReagants();
		
		if(reags[19] != null && reags[19].getAmount() >= 4.99 && chamber.getContent() - reags[19].getAmount() >= 19.99){
			
			double practAmount = reags[19].getAmount();

			chamber.addHeat(-(chamber.getTemp() + 273D) * (chamber.getContent() - practAmount));
			
			reags[19] = null;
			
			ItemStack crystal = CustomMaterial.createCrystal(reags, chamber.getContent());
			
			chamber.dropItem(crystal);
			
			for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
				reags[i] = null;
			}
			
			reags[19] = new ReagentStack(AlchemyCore.REAGENTS[19], practAmount);
			
			return true;
		}
		
		return false;
	}

}
