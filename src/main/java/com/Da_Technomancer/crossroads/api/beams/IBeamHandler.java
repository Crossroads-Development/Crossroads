package com.Da_Technomancer.crossroads.api.beams;

import javax.annotation.Nonnull;

/** This capability is to be placed on things that RECEIVE beams, not for things that send beams.*/
public interface IBeamHandler{

	/**
	 * Handlers should assume that they receive the last beams unit sent with this method continuously for the next BEAM_TIME ticks.
	 * @param beamIn The incoming beam
	 * @param beamHitIn The collision info for the incoming beam
	 */
	default void setBeam(@Nonnull BeamUnit beamIn, @Nonnull BeamHit beamHitIn){
		setBeam(beamIn);
	}

	/**
	 * Handlers should assume that they receive the last beams unit sent with this method continuously for the next BEAM_TIME ticks.
	 * @deprecated Call the version with BeamHit instead
	 */
	@Deprecated
	void setBeam(@Nonnull BeamUnit mag);
}
