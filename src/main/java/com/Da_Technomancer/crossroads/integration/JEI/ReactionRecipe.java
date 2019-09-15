package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ITransparentReaction;

import java.util.ArrayList;

public class ReactionRecipe{

	protected final ArrayList<ReagIngr> ingr;
	protected final ArrayList<ReagIngr> prod;
	protected final boolean charged;
	protected final boolean dangerous;
	protected final IReagent catalyst;
	protected final double minTemp;
	protected final double maxTemp;
	protected final double deltaHeat;

	public ReactionRecipe(ITransparentReaction reaction){
		ingr = new ArrayList<ReagIngr>(reaction.getReagents().length);
		for(int i = 0; i < reaction.getReagents().length; i++){
			ingr.add(new ReagIngr(reaction.getReagents()[i].getType(), reaction.getReagents()[i].getAmount()));
		}
		prod = new ArrayList<ReagIngr>(reaction.getProducts().length);
		for(int i = 0; i < reaction.getProducts().length; i++){
			prod.add(new ReagIngr(reaction.getProducts()[i].getType(), reaction.getProducts()[i].getAmount()));
		}
		charged = reaction.charged();
		dangerous = reaction.isDestructive();
		catalyst = reaction.getCatalyst();
		minTemp = reaction.minTemp();
		maxTemp = reaction.maxTemp();
		deltaHeat = reaction.deltaHeatPer();
	}
}
