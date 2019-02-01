package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public Integer[] getConnectMode(boolean forRender){
		init();
		if(forRender && !world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL)){
			return new Integer[] {0, 0, 0, 0, 0, 0};
		}
		return super.getConnectMode(forRender);
	}

	@Override
	public void update(){
		if(!world.isRemote && world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL)){
			super.update();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && connectMode != null && (side == null || connectMode[side.getIndex()] != 0)){
			if(world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL)){
				return side == null || connectMode[side.getIndex()] == 1 ? (T) mainHandler : connectMode[side.getIndex()] == 2 ? (T) outHandler : (T) inHandler;
			}else{
				return null;
			}
		}

		return super.getCapability(capability, side);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && connectMode != null && (side == null || connectMode[side.getIndex()] != 0)){
			return world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL);
		}
		return super.hasCapability(capability, side);
	}
}
