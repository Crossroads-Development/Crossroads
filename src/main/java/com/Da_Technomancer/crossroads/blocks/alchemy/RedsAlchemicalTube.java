package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.tileentities.alchemy.RedsAlchemicalTubeTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.templates.ConduitBlock.IConduitTE;

public class RedsAlchemicalTube extends AlchemicalTube{

	public RedsAlchemicalTube(boolean crystal){
		super(crystal, (crystal ? "crystal_" : "") + "reds_alch_tube");
		registerDefaultState(defaultBlockState().setValue(ESProperties.REDSTONE_BOOL, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new RedsAlchemicalTubeTileEntity(!crystal);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(state.getValue(ESProperties.REDSTONE_BOOL) && ESConfig.isWrench(playerIn.getItemInHand(hand))){
			return super.use(state, worldIn, pos, playerIn, hand, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		boolean isPowered = worldIn.hasNeighborSignal(pos);
		if(isPowered != state.getValue(ESProperties.REDSTONE_BOOL)){
			worldIn.setBlockAndUpdate(pos, state.setValue(ESProperties.REDSTONE_BOOL, isPowered));
		}
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable IConduitTE<EnumTransferMode> te){
		return super.evaluate(value, state, te) && state.getValue(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return super.getStateForPlacement(context).setValue(ESProperties.REDSTONE_BOOL, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(ESProperties.REDSTONE_BOOL);
	}
}
