package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.TEBlock;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class LensFrame extends TEBlock implements IReadable{

	private static final VoxelShape[] SHAPE = new VoxelShape[3];

	static{
		SHAPE[0] = box(6, 0, 0, 10, 16, 16);
		SHAPE[1] = box(0, 6, 0, 16, 10, 16);
		SHAPE[2] = box(0, 0, 6, 16, 16, 10);
	}

	public LensFrame(){
		super(CRBlocks.getRockProperty());
		String name = "lens_frame";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE[state.getValue(CRProperties.AXIS).ordinal()];
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new LensFrameTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, LensFrameTileEntity.TYPE);
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.AXIS, context.getNearestLookingDirection().getAxis());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.AXIS);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack stack = playerIn.getItemInHand(hand);

		if(ConfigUtil.isWrench(stack)){
			// Wrenches rotate the block instead
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.AXIS));
			}
			return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
//		}else if(stack.getItem() == CRItems.omnimeter)){
//			// Omnimeter performs its function instead
//			return InteractionResult.PASS;
		}else{
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(!(te instanceof LensFrameTileEntity lens)){
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			ItemStack inLens = lens.getLensItem();
			if(!inLens.isEmpty()){
				if(!worldIn.isClientSide){
					if(!playerIn.getInventory().add(inLens)){
						ItemEntity dropped = playerIn.drop(inLens, false);
						if(dropped != null){
							dropped.setNoPickUpDelay();
							dropped.setThrower(playerIn);
						}
					}
					lens.setLensItem(ItemStack.EMPTY);
				}
				return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
			}else if(!stack.isEmpty()){
				if(worldIn.getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, new SingleRecipeInput(stack), worldIn).isPresent()){
					if(!worldIn.isClientSide){
						lens.setLensItem(stack.split(1));
					}
					return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
				}
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(newState.getBlock() != this && te instanceof LensFrameTileEntity lte){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), lte.getLensItem());
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof LensFrameTileEntity lte){
			return lte.getRedstone();
		}
		return 0;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.LENS_FRAME_TYPE.value();
	}
}
