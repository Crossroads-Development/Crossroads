package com.Da_Technomancer.crossroads.API.beams;

import javax.annotation.Nonnull;

/** This capability is to be placed on things that RECEIVE beams, not for things that send beams.*/
public interface IBeamHandler{
	
	/**
	 * Handlers should assume that they receive the last beams unit sent with this method continuously for the next BEAM_TIME ticks.
	 */
	void setBeam(@Nonnull BeamUnit mag);
}
