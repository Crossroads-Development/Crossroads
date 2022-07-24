package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class GlasswareHolder extends BaseEntityBlock{

	private static final VoxelShape EMPTY_SHAPE = box(5, 0, 5, 11, 8, 11);
	private static final VoxelShape PHIAL_SHAPE = box(5, 0, 5, 11, 16, 11);
	private static final VoxelShape FLORENCE_SHAPE = Shapes.or(box(5, 8, 5, 11, 16, 11), box(3, 0, 3, 13, 8, 13));
	private static final VoxelShape SHELL_SHAPE = box(4, 0, 4, 12, 12, 12);

	public GlasswareHolder(){
		this("glassware_holder");
	}

	protected GlasswareHolder(String name){
		super(CRBlocks.getMetalProperty());
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new GlasswareHolderTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, GlasswareHolderTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		switch(state.getValue(CRProperties.CONTAINER_TYPE)){
			case NONE:
				return EMPTY_SHAPE;
			case PHIAL:
				return PHIAL_SHAPE;
			case FLORENCE:
				return FLORENCE_SHAPE;
			case SHELL:
				return SHELL_SHAPE;
		}
		return Shapes.block();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		if(newState.getBlock() != this){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof GlasswareHolderTileEntity){
				((GlasswareHolderTileEntity) te).onBlockDestroyed(state);
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.hasNeighborSignal(pos)){
			if(!state.getValue(CRProperties.REDSTONE_BOOL)){
				worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.REDSTONE_BOOL, true));
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}
		}else if(state.getValue(CRProperties.REDSTONE_BOOL)){
			worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.REDSTONE_BOOL, false));
			worldIn.updateNeighbourForOutputSignal(pos, this);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CRYSTAL, CRProperties.CONTAINER_TYPE, CRProperties.REDSTONE_BOOL);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof GlasswareHolderTileEntity){
				playerIn.setItemInHand(hand, ((GlasswareHolderTileEntity) te).rightClickWithItem(playerIn.getItemInHand(hand), playerIn.isShiftKeyDown(), playerIn, hand));
			}
		}
		return InteractionResult.SUCCESS;
	}
}
