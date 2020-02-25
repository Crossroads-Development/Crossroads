package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;

public interface IFluxLink extends ILongReceiver, ILinkTE, IInfoTE{

	int getFlux();

	/**
	 * Used for things like omnimeter readouts and rendering- anything the player would see to smooth out the technical fluctuations in getFlux()
	 * @return The total flux that should be represented to the player
	 */
	default int getReadingFlux(){
		return getFlux();
	}

	default void addFlux(int deltaFlux){
		setFlux(Math.max(getFlux() + deltaFlux, 0));
	}

	void setFlux(int newFlux);

	default int getMaxFlux(){
		return 64;
	}

	@Override
	default int getMaxLinks(){
		return getBehaviour() == Behaviour.NODE ? 16 : 1;
	}

	@Override
	default boolean canBeginLinking(){
		return getBehaviour() != Behaviour.SINK;
	}

	@Override
	default int getRange(){
		return 16;
	}

	@Override
	default boolean canLink(ILinkTE otherTE){
		switch(getBehaviour()){
			case SOURCE:
				return otherTE instanceof IFluxLink && ((IFluxLink) otherTE).getBehaviour() == Behaviour.NODE;
			case SINK:
				return false;
			case NODE:
				return otherTE instanceof IFluxLink && ((IFluxLink) otherTE).getBehaviour() != Behaviour.SOURCE;
			default:
				throw new IllegalArgumentException("Undefined flux transfer behaviour- report to mod author: " + (getBehaviour() == null ? "NULL" : getBehaviour().name()));
		}
	}

	/**
	 * Allows flux TEs to categorize their behaviour
	 * This is used for default methods in this interface and for determining if a link should be allowed
	 * @return A (constant) behaviour value
	 */
	default Behaviour getBehaviour(){
		return Behaviour.SOURCE;
	}

	enum Behaviour{

		SOURCE(),//Flux should be routed away from this TE
		SINK(),//Flux should be routed towards this TE
		NODE()//Connect in a large network of these TEs
	}
}
