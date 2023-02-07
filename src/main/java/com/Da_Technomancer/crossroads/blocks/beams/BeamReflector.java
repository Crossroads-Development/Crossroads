package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.templates.BeamBlock;
import com.Da_Technomancer.crossroads.blocks.heat.FireboxTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class BeamReflector extends BeamBlock{

	public BeamReflector(){
		super("beam_reflector");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BeamReflectorTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BeamReflectorTileEntity.TYPE);
	}
}
