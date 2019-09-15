package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BeamRenderTEBase extends TileEntity{

	/**
	 *
	 * @return A size six array (with null elements) with integers that BeamManager.getTriple will convert into rendering instructions
	 */
	public abstract int[] getRenderedBeams();

	/**
	 * For informational displays.
	 * May contain null elements
	 */
	public abstract BeamUnit[] getLastSent();
	
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
}
