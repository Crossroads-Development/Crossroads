package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;

public interface IBeamRenderTE{

	/**
	 *
	 * @return A size six array (with null elements) with integers that BeamManager.getTriple will convert into rendering instructions
	 */
	int[] getRenderedBeams();

	/**
	 * For informational displays.
	 * Must not contain null elements
	 */
	BeamUnit[] getLastSent();

	/* Recommended override for subclasses
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
	 */
}
