package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.GlasswareHolderTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GlasswareHolder extends ContainerBlock{

	private static final VoxelShape EMPTY_SHAPE = makeCuboidShape(5, 0, 5, 11, 8, 11);
	private static final VoxelShape PHIAL_SHAPE = makeCuboidShape(5, 0, 5, 11, 16, 11);
	private static final VoxelShape FLORENCE_SHAPE = VoxelShapes.or(makeCuboidShape(5, 8, 5, 11, 16, 11), makeCuboidShape(3, 0, 3, 13, 8, 13));
	private static final VoxelShape SHELL_SHAPE = makeCuboidShape(4, 0, 4, 12, 12, 12);

	public GlasswareHolder(){
		this("glassware_holder");
	}

	protected GlasswareHolder(String name){
		super(CRBlocks.METAL_PROPERTY);
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new GlasswareHolderTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		switch(state.get(CRProperties.CONTAINER_TYPE)){
			case NONE:
				return EMPTY_SHAPE;
			case PHIAL:
				return PHIAL_SHAPE;
			case FLORENCE:
				return FLORENCE_SHAPE;
			case SHELL:
				return SHELL_SHAPE;
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(newState.getBlock() != this){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof GlasswareHolderTileEntity){
				((GlasswareHolderTileEntity) te).onBlockDestroyed(state);
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.isBlockPowered(pos)){
			if(!state.get(ESProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(ESProperties.REDSTONE_BOOL, true));
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}else if(state.get(ESProperties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.with(ESProperties.REDSTONE_BOOL, false));
			worldIn.updateComparatorOutputLevel(pos, this);
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CRYSTAL, CRProperties.CONTAINER_TYPE, ESProperties.REDSTONE_BOOL);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof GlasswareHolderTileEntity){
				playerIn.setHeldItem(hand, ((GlasswareHolderTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
		}
		return ActionResultType.SUCCESS;
	}
}
