package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DensusPlateTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

	private static final VoxelShape[][] SHAPES = new VoxelShape[4][6];

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
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.LAYERS, 1));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack stack = playerIn.getItemInHand(hand);
		if(ESConfig.isWrench(stack)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
			return InteractionResult.SUCCESS;
		}
		if(stack.getItem() == this.asItem()){
			int layers = state.getValue(CRProperties.LAYERS);
			if(layers < 4){
				if(!worldIn.isClientSide){
					worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.LAYERS, layers + 1));
					stack.shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.LAYERS) - 1][state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING, CRProperties.LAYERS);
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
