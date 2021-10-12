package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneAxisTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IWireConnect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneAxis extends BaseEntityBlock implements IWireConnect{
	
	public RedstoneAxis(){
		super(CRBlocks.getMetalProperty());
		String name = "redstone_axis";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.POWER_LEVEL, 0));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("tt.crossroads.redstone_axis.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.redstone_axis.power"));
		tooltip.add(new TranslatableComponent("tt.crossroads.redstone_axis.circuit"));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING, CRProperties.POWER_LEVEL);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side){
		return side != null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new RedstoneAxisTileEntity(pos, state);

	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		BlockEntity te = worldIn.getBlockEntity(pos);

		if(te instanceof RedstoneAxisTileEntity){
			RedstoneAxisTileEntity bte = (RedstoneAxisTileEntity) te;
			CircuitUtil.updateFromWorld(bte.redsHandler, blockIn);
		}
	}

	@Override
	public boolean canConnect(Direction side, BlockState state){
		return true;
	}
}
