package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatedTubeTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HeatedTube extends ContainerBlock{

	private static final VoxelShape SHAPE_X;
	private static final VoxelShape SHAPE_Z;
	private final boolean crystal;

	static{
		VoxelShape vertical = box(3, 0, 3, 13, 16, 13);
		SHAPE_X = VoxelShapes.or(vertical, box(0, 4, 4, 16, 12, 12));
		SHAPE_Z = VoxelShapes.or(vertical, box(4, 4, 0, 12, 12, 16));
	}

	public HeatedTube(boolean crystal){
		super(CRBlocks.getGlassProperty());
		this.crystal = crystal;
		String name = (crystal ? "crystal_"  : "") + "heated_tube";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new HeatedTubeTileEntity(!crystal);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return state.getValue(CRProperties.HORIZ_FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_FACING);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_FACING));
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_FACING, context.getHorizontalDirection());
	}
}
