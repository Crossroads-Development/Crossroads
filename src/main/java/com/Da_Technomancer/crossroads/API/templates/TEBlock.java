package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Used as a generic superclass by blocks that have associated tile entities
 */
public abstract class TEBlock extends BaseEntityBlock{

	protected TEBlock(Properties prop){
		super(prop);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		//Drop items from the inventory, if applicable
		if(newState.getBlock() != state.getBlock() && world.getBlockEntity(pos) instanceof Container cont){
			Containers.dropContents(world, pos, cont);
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	//Defer comparator measurements to circuit value reading, if applicable
	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return this instanceof IReadable;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		return this instanceof IReadable readable ? RedstoneUtil.clampToVanilla(readable.read(world, pos, state)) : 0;
	}
}
