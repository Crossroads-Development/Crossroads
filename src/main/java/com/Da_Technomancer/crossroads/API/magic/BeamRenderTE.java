package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class BeamRenderTE extends TileEntity{

	public abstract Triple<Color, Integer, Integer>[] getBeam();
	
	public abstract void refresh();
	
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
}
