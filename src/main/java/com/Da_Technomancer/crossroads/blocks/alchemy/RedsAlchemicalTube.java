package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.tileentities.alchemy.RedsAlchemicalTubeTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RedsAlchemicalTube extends AlchemicalTube{

	public RedsAlchemicalTube(boolean crystal){
		super(crystal, (crystal ? "crystal_" : "") + "reds_alch_tube");
		registerDefaultState(defaultBlockState().setValue(ESProperties.REDSTONE_BOOL, false));
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new RedsAlchemicalTubeTileEntity(!crystal);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(state.getValue(ESProperties.REDSTONE_BOOL) && ESConfig.isWrench(playerIn.getItemInHand(hand))){
			return super.use(state, worldIn, pos, playerIn, hand, hit);
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
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
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return super.getStateForPlacement(context).setValue(ESProperties.REDSTONE_BOOL, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(ESProperties.REDSTONE_BOOL);
	}
}
