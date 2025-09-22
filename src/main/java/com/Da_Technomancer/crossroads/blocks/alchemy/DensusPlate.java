package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DensusPlate extends BaseEntityBlock{

	public static final MapCodec<DensusPlate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.fieldOf("anti").forGetter(DensusPlate::isAntiDensus)).apply(instance, DensusPlate::new));

	private static final VoxelShape[][] SHAPES = new VoxelShape[4][6];

	private final boolean antiDensus;

	static{
		for(int i = 0; i < 4; i++){
			int width = (i + 1) * 2;
			SHAPES[i][0] = box(0, 16 - width, 0, 16, 16, 16);
			SHAPES[i][1] = box(0, 0, 0, 16, width, 16);
			SHAPES[i][2] = box(0, 0, 16 - width, 16, 16, 16);
			SHAPES[i][3] = box(0, 0, 0, 16, 16, width);
			SHAPES[i][4] = box(16 - width, 0, 0, 16, 16, 16);
			SHAPES[i][5] = box(0, 0, 0, width, 16, 16);
		}
	}

	public DensusPlate(boolean anti){
		super(CRBlocks.getRockProperty());
		String name = anti ? "anti_densus_plate" : "densus_plate";
		CRBlocks.queueForRegister(name, this);
		antiDensus = anti;
		registerDefaultState(defaultBlockState().setValue(CRProperties.LAYERS, 1));
	}

	public boolean isAntiDensus(){
		return antiDensus;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack stack = playerIn.getItemInHand(hand);
		if(ConfigUtil.isWrench(stack)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.FACING));
			}
			return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
		}
		if(stack.getItem() == this.asItem()){
			int layers = state.getValue(CRProperties.LAYERS);
			if(layers < 4){
				if(!worldIn.isClientSide){
					worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.LAYERS, layers + 1));
					if(playerIn == null || !playerIn.isCreative()){
						stack.shrink(1);
					}
				}
				return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.LAYERS) - 1][state.getValue(CRProperties.FACING).get3DDataValue()];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.FACING, CRProperties.LAYERS);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.DENSUS_PLATE_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new DensusPlateTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, DensusPlateTileEntity.TYPE);
	}
}
