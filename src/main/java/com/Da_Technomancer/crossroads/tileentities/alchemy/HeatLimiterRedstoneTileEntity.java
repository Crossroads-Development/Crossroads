package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;

public class HeatLimiterRedstoneTileEntity extends HeatLimiterBasicTileEntity{

	@Override
	public double getSetting(){
		return RedstoneUtil.getPowerAtPos(world, pos);
	}
}