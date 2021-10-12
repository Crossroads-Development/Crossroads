package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedsAlchemicalTubeTileEntity extends AlchemicalTubeTileEntity{

	@ObjectHolder("reds_alchemical_tube")
	public static BlockEntityType<RedsAlchemicalTubeTileEntity> TYPE = null;

	public RedsAlchemicalTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public RedsAlchemicalTubeTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	private boolean isUnlocked(){
		return getBlockState().getValue(ESProperties.REDSTONE_BOOL);
	}

	@Override
	protected void performTransfer(){
		if(isUnlocked()){
			super.performTransfer();
		}
	}

	@Override
	protected boolean allowConnect(Direction side){
		return isUnlocked() && super.allowConnect(side);
	}
}
