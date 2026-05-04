package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.technomancy.IGateway;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class GatewayControllerDestination extends BaseEntityBlock{

	public GatewayControllerDestination(){
		super(CRBlocks.getMetalProperty());
		String name = "gateway_controller_destination";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false).setValue(CRProperties.HORIZ_FACING, Direction.NORTH));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new GatewayControllerDestinationTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, GatewayControllerDestinationTileEntity.TYPE);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		BlockEntity te;
		if(!worldIn.isClientSide && worldIn.hasNeighborSignal(pos) && (te = worldIn.getBlockEntity(pos)) instanceof GatewayControllerDestinationTileEntity){
			((GatewayControllerDestinationTileEntity) te).redstoneInput();
		}
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.GATEWAY_CONTROLLER_DESTINATION_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		//If this is formed into a multiblock, we let the TESR on the top handle all rendering
		return state.getValue(CRProperties.ACTIVE) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, CRProperties.HORIZ_FACING);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray){
		if(ConfigUtil.isWrench(held)){
			if(player.isCrouching()){
				//Attempt to form the multiblock
				if(!state.getValue(CRProperties.ACTIVE)){
					BlockEntity te = world.getBlockEntity(pos);
					if(te instanceof GatewayControllerDestinationTileEntity gte){
						gte.assemble(player);
						return ItemInteractionResult.sidedSuccess(world.isClientSide);
					}
				}
			}else{
				//Rotate the block
				if(state.getValue(CRProperties.ACTIVE)){
					//Only swap the two valid orientations
					if(!world.isClientSide){
						world.setBlockAndUpdate(pos, state.setValue(CRProperties.HORIZ_FACING, state.getValue(CRProperties.HORIZ_FACING).getOpposite()));
					}
					return ItemInteractionResult.sidedSuccess(world.isClientSide);
				}else{
					if(!world.isClientSide){
						world.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_FACING));
					}
					return ItemInteractionResult.sidedSuccess(world.isClientSide);
				}
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof IGateway gte){
			gte.dismantle();//Shutdown the multiblock
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.gateway.desc"));
		tooltip.add(Component.translatable("tt.crossroads.gateway.destination"));
		tooltip.add(Component.translatable("tt.crossroads.gateway.destination.redial"));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.BLOCK;//Some mods make TileEntities piston moveable. That would be really bad for this block
	}
}
