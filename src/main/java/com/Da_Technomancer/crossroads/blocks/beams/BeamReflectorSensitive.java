package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.templates.BeamBlock;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BeamReflectorSensitive extends BeamBlock{

	public BeamReflectorSensitive(){
		super("beam_reflector_sensitive");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BeamReflectorSensitiveTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BeamReflectorSensitiveTileEntity.TYPE);
	}
}
