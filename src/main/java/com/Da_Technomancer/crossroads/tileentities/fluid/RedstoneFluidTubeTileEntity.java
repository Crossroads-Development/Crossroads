package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity{

	@ObjectHolder("redstone_fluid_tube")
	private static TileEntityType<RedstoneFluidTubeTileEntity> type = null;

	public RedstoneFluidTubeTileEntity(){
		super(type);
	}

	@Override
	public void tick(){
		if(getBlockState().get(ESProperties.REDSTONE_BOOL)){
			super.tick();
		}
	}

	@Override
	protected boolean canConnect(Direction side){
		return super.canConnect(side) && getBlockState().get(ESProperties.REDSTONE_BOOL);
	}
}
