package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class HeatLimiterBasic extends BaseEntityBlock{

	public HeatLimiterBasic(){
		super(CRBlocks.getRockProperty());
		String name = "heat_limiter_basic";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new HeatLimiterBasicTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, HeatLimiterBasicTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, CRProperties.FACING);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te;
			if(ConfigUtil.isWrench(playerIn.getItemInHand(hand))){
				if(playerIn.isShiftKeyDown()){
					worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.ACTIVE));
				}else{
					worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.FACING));
				}
			}else if((te = worldIn.getBlockEntity(pos)) instanceof HeatLimiterBasicTileEntity){
				NetworkHooks.openScreen((ServerPlayer) playerIn, (MenuProvider) te, buf -> {buf.writeFloat(((HeatLimiterBasicTileEntity) te).setting); buf.writeUtf(((HeatLimiterBasicTileEntity) te).expression); buf.writeBlockPos(pos);});
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.heat_limiter.desc_cable"));
		tooltip.add(Component.translatable("tt.crossroads.heat_limiter.desc_purpose"));
		tooltip.add(Component.translatable("tt.crossroads.heat_limiter.desc_mode"));
		tooltip.add(Component.translatable("tt.crossroads.heat_limiter.desc_ui"));
	}
}
