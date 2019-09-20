package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BeamRenderTEBase extends TileEntity{


	public BeamRenderTEBase(TileEntityType<?> type){
		super(type);
	}

	/**
	 *
	 * @return A size six array (with null elements) with integers that BeamManager.getTriple will convert into rendering instructions
	 */
	public abstract int[] getRenderedBeams();

	/**
	 * For informational displays.
//	 * Must not contain null elements
	 */
	public abstract BeamUnit[] getLastSent();
	
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
}
