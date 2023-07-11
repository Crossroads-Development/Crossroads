package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract class AbstractCannon extends BaseEntityBlock{

	public AbstractCannon(String name, Properties properties){
		super(properties);
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.FACING, context.getClickedFace());
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack held = playerIn.getItemInHand(hand);
		if(ConfigUtil.isWrench(held)){
			if(playerIn.isShiftKeyDown()){
				//Sneak clicking- lock/unlock
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof AbstractCannonTileEntity cte){
					cte.updateLock(playerIn);
				}
				return InteractionResult.SUCCESS;
			}else{
				//Rotate this machine
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.FACING));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}
}
