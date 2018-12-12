package com.Da_Technomancer.crossroads.API.technomancy;

public class DefaultFluxHandler implements IFluxHandler{

	@Override
	public boolean canReceiveFlux(){
		return true;
	}

	@Override
	public int getCapacity(){
		return 0;
	}

	@Override
	public int getFlux(){
		return 0;
	}

	@Override
	public int addFlux(int fluxIn){
		return 0;
	}
}
