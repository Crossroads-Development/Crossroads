package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedsAlchemicalTubeTileEntity extends AlchemicalTubeTileEntity{

	public static final BlockEntityType<RedsAlchemicalTubeTileEntity> TYPE = CRTileEntity.createType(RedsAlchemicalTubeTileEntity::new, CRBlocks.redsAlchemicalTubeGlass, CRBlocks.redsAlchemicalTubeCrystal);

	public RedsAlchemicalTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public RedsAlchemicalTubeTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	private boolean isUnlocked(){
		return getBlockState().getValue(CRProperties.REDSTONE_BOOL);
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
