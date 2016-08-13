package com.Da_Technomancer.crossroads.integration.JEI;

public class HeatingCrucibleRecipe{
	
	private final boolean copper;
	
	public HeatingCrucibleRecipe(boolean copper){
		this.copper = copper;
	}
	
	protected boolean isCopper(){
		return copper;
	}

}
