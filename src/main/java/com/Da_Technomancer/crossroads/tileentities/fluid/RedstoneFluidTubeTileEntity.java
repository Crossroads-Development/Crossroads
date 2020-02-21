package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.util.Direction;

public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	@Override
	public void tick(){
		if(!world.isRemote && world.getBlockState(pos).get(ESProperties.REDSTONE_BOOL)){
			super.tick();
		}
	}

	@Override
	protected boolean canConnect(Direction side){
		return super.canConnect(side) && world.getBlockState(pos).get(ESProperties.REDSTONE_BOOL);
	}
}
