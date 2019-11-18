package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBeamRenderTE{

	/**
	 *
	 * @return A size six array (with null elements) with integers that BeamManager.getTriple will convert into rendering instructions
	 */
	public int[] getRenderedBeams();

	/**
	 * For informational displays.
	 * Must not contain null elements
	 */
	public BeamUnit[] getLastSent();

	/* Recommended override for subclasses
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
	 */
}
