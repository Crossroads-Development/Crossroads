package com.Da_Technomancer.crossroads.API.technomancy;

public interface IFluxHandler{

	public boolean canReceiveFlux();

	public int getCapacity();

	public int getFlux();

	public int addFlux(int fluxIn);
}
