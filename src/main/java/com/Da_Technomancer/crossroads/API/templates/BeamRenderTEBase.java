package com.Da_Technomancer.crossroads.API.templates;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class BeamRenderTEBase extends TileEntity{
	
	public abstract Triple<Color, Integer, Integer>[] getBeam();

	/**
	 * For informational displays. 
	 */
	@Nullable
	public abstract MagicUnit[] getLastFullSent();
	
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
	
	public abstract void refresh();

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
}
