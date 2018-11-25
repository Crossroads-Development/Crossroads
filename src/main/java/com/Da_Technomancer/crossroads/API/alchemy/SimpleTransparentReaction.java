package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

public class SimpleTransparentReaction extends SimpleReaction implements ITransparentReaction{

	public SimpleTransparentReaction(ReagentStack[] reagents, ReagentStack[] products, @Nullable IReagent cat, double minTemp, double maxTemp, double heatChange, boolean charged){
		super(reagents, products, cat, minTemp, maxTemp, heatChange, charged);
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
	public ReagentStack[] getReagents(){
		return reagents;
	}

	@Override
	public ReagentStack[] getProducts(){
		return products;
	}
}