package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

public class SimpleTransparentReaction extends SimpleReaction implements ITransparentReaction{

	public SimpleTransparentReaction(Pair<IReagent, Integer>[] reagents, Pair<IReagent, Integer>[] products, @Nullable IReagent cat, double minTemp, double maxTemp, double heatChange, EnumSolventType[] solvents, boolean charged){
		super(reagents, products, cat, minTemp, maxTemp, heatChange, solvents, charged);
	}

	@Override
	public IReagent getCatalyst(){
		return cat;
	}

	@Override
	public double minTemp(){
		return minTemp;
	}

	@Override
	public double maxTemp(){
		return maxTemp;
	}

	@Override
	public boolean charged(){
		return charged;
	}

	@Override
	public double deltaHeatPer(){
		return heatChange;
	}

	@Override
	public EnumSolventType[] requiredSolvents(){
		return solvents;
	}

	@Override
	public Pair<IReagent, Integer>[] getReagents(){
		return reagents;
	}

	@Override
	public Pair<IReagent, Integer>[] getProducts(){
		return products;
	}
}