package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	@ObjectHolder("redstone_fluid_tube")
	public static BlockEntityType<RedstoneFluidTubeTileEntity> TYPE = null;

	public RedstoneFluidTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void serverTick(){
		if(getBlockState().getValue(ESProperties.REDSTONE_BOOL)){
			super.serverTick();
		}
	}

	@Override
	protected boolean canConnect(Direction side){
		return super.canConnect(side) && getBlockState().getValue(ESProperties.REDSTONE_BOOL);
	}
}
