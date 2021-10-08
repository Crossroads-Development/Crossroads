package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	@ObjectHolder("redstone_fluid_tube")
	private static BlockEntityType<RedstoneFluidTubeTileEntity> type = null;

	public RedstoneFluidTubeTileEntity(){
		super(type);
	}

	@Override
	public void tick(){
		if(getBlockState().getValue(ESProperties.REDSTONE_BOOL)){
			super.tick();
		}
	}

	@Override
	protected boolean canConnect(Direction side){
		return super.canConnect(side) && getBlockState().getValue(ESProperties.REDSTONE_BOOL);
	}
}
