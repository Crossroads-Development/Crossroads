package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.fluid.RedstoneFluidTube;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	@Override
	protected void updateState(){
		BlockState state = world.getBlockState(pos);
		BlockState newState = state;
		if(state.getBlock() instanceof RedstoneFluidTube){
			boolean reds = state.get(ESProperties.REDSTONE_BOOL);
			for(int i = 0; i < 6; i++){
				newState = newState.with(CRProperties.CONDUIT_SIDES[i], reds && hasMatch[i] ? configure[i] : EnumTransferMode.NONE);
			}
		}
		if(state != newState){
			world.setBlockState(pos, newState, 2);
		}
	}

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
