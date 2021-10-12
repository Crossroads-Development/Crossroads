package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.tileentities.fluid.RedstoneFluidTubeTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.templates.ConduitBlock.IConduitTE;

public class RedstoneFluidTube extends FluidTube{

	public RedstoneFluidTube(){
		super("redstone_fluid_tube");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new RedstoneFluidTubeTileEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		if(state.getValue(ESProperties.REDSTONE_BOOL)){
			return super.getShape(state, worldIn, pos, context);
		}else{
			return SHAPES[0];//Core only
		}
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable IConduitTE<EnumTransferMode> te){
		return super.evaluate(value, state, te) && state.getValue(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return super.getStateForPlacement(context).setValue(ESProperties.REDSTONE_BOOL, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		boolean isPowered = worldIn.hasNeighborSignal(pos);
		if(isPowered != state.getValue(ESProperties.REDSTONE_BOOL)){
			worldIn.setBlockAndUpdate(pos, state.setValue(ESProperties.REDSTONE_BOOL, isPowered));
		}
	}
}
