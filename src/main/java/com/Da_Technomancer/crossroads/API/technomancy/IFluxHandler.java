package com.Da_Technomancer.crossroads.API.technomancy;

/**
 * Not a capability. To be placed on TileEntities directly
 */
public interface IFluxHandler{

	/**
	 * @return The maximum amount of flux this device is willing to accept this cycle. This value is ignored if isFluxReceiver is false
	 */
	public int canAccept();

	public int getCapacity();

	public int getFlux();

	public int addFlux(int fluxIn);

	public boolean isFluxEmitter();

	public boolean isFluxReceiver();
}
