package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.tileentities.fluid.RedstoneFluidTubeTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RedstoneFluidTube extends FluidTube{

	public RedstoneFluidTube(){
		super("redstone_fluid_tube");
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneFluidTubeTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		super.fillStateContainer(builder);
		builder.add(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		if(state.get(ESProperties.REDSTONE_BOOL)){
			return super.getShape(state, worldIn, pos, context);
		}else{
			return SHAPES[0];//Core only
		}
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable TileEntity te){
		return super.evaluate(value, state, te) && state.get(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return super.getStateForPlacement(context).with(ESProperties.REDSTONE_BOOL, context.getWorld().isBlockPowered(context.getPos()));
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.isBlockPowered(pos)){
			if(!state.get(ESProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(ESProperties.REDSTONE_BOOL, true));
			}
		}else{
			if(state.get(ESProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(ESProperties.REDSTONE_BOOL, false));
			}
		}
	}
}
