package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AlembicTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Alembic extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[4];

	static{
		//TODO this shape can be made tighter
		SHAPES[0] = makeCuboidShape(2, 0, 4, 14, 16, 16);
		SHAPES[1] = makeCuboidShape(0, 0, 2, 12, 16, 14);
		SHAPES[2] = makeCuboidShape(2, 0, 0, 14, 16, 12);
		SHAPES[3] = makeCuboidShape(4, 0, 2, 16, 16, 14);
	}

	public Alembic(){
		super(Properties.create(Material.IRON).hardnessAndResistance(0.5F).sound(SoundType.METAL));
		String name = "alembic";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new AlembicTileEntity();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.HORIZ_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.with(ESProperties.HORIZ_FACING, state.get(ESProperties.HORIZ_FACING).rotateY()));
			}
			return true;
		}

		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof AlembicTileEntity){
				playerIn.setHeldItem(hand, ((AlembicTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
		}
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.HORIZ_FACING).getHorizontalIndex()];
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.HORIZ_FACING);
	}
}
