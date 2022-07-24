package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	public static final BlockEntityType<RedstoneFluidTubeTileEntity> TYPE = CRTileEntity.createType(RedstoneFluidTubeTileEntity::new, CRBlocks.redstoneFluidTube);

	public RedstoneFluidTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void serverTick(){
		if(getBlockState().getValue(CRProperties.REDSTONE_BOOL)){
			super.serverTick();
		}
	}

	@Override
	protected boolean canConnect(Direction side){
		return super.canConnect(side) && getBlockState().getValue(CRProperties.REDSTONE_BOOL);
	}
}
