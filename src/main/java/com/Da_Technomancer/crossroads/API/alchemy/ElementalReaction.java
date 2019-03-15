package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;

public class ElementalReaction implements IReaction{
	
	private final IElementReagent product;
	
	public ElementalReaction(IElementReagent product){
		this.product = product;
	}
	
	@Override
	public boolean performReaction(IReactionChamber chamber){
		//Chamber must be charged to begin
		if(chamber.isCharged()){
			//Requires practitioner's catalyst
			ReagentMap reags = chamber.getReagants();
			if(reags.getQty(EnumReagents.PRACTITIONER.id()) != 0 && product.getAlignment() == EnumBeamAlignments.getAlignment(new BeamUnit(reags.getQty(EnumReagents.PHELOSTOGEN.id()), reags.getQty(EnumReagents.AETHER.id()), reags.getQty(EnumReagents.ADAMANT.id()), 0))){
				int created = 0;
				created += reags.getQty(EnumReagents.PHELOSTOGEN.id());
				created += reags.getQty(EnumReagents.AETHER.id());
				created += reags.getQty(EnumReagents.ADAMANT.id());
				reags.remove(AlchemyCore.REAGENTS.get(EnumReagents.PHELOSTOGEN.id()));
				reags.remove(AlchemyCore.REAGENTS.get(EnumReagents.AETHER.id()));
				reags.remove(AlchemyCore.REAGENTS.get(EnumReagents.ADAMANT.id()));
				reags.addReagent(product, created, reags.getTempC());
			}
		}
		return false;
	}
}
