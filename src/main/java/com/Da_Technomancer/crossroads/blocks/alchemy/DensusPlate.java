package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DensusPlateTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DensusPlate extends ContainerBlock{

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
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack stack = playerIn.getItemInHand(hand);
		if(ESConfig.isWrench(stack)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
			return ActionResultType.SUCCESS;
		}
		if(stack.getItem() == this.asItem()){
			int layers = state.getValue(CRProperties.LAYERS);
			if(layers < 4){
				if(!worldIn.isClientSide){
					worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.LAYERS, layers + 1));
					stack.shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.getValue(CRProperties.LAYERS) - 1][state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING, CRProperties.LAYERS);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new DensusPlateTileEntity();
	}
}
