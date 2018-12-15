package com.Da_Technomancer.crossroads.API.technomancy;

/**
 * Not a capability. To be placed on TileEntities directly
 */
public interface IFluxHandler{

	public boolean canReceiveFlux();

	public int getCapacity();

	public int getFlux();

	public int addFlux(int fluxIn);

	public boolean isFluxEmitter();

	public boolean isFluxReceiver();
}
