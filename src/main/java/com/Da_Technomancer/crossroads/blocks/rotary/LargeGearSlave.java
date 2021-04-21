package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LargeGearSlave extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	private static final VoxelShape[] COL_SHAPES = new VoxelShape[6];

	static{
		//Create a shape for each facing. Solid base plate
		SHAPES[0] = box(0, 0, 0, 16, 2, 16);
		SHAPES[1] = box(0, 14, 0, 16, 16, 16);
		SHAPES[2] = box(0, 0, 0, 16, 16, 2);
		SHAPES[3] = box(0, 0, 14, 16, 16, 16);
		SHAPES[4] = box(0, 0, 0, 2, 16, 16);
		SHAPES[5] = box(14, 0, 0, 16, 16, 16);

		//Create a collision shape for each facing. A small strip is missing to prevent it being considered "solid" for things like torches, while still blocking basically all entity movement
		//Note: could be changed to have one pixel missing instead of a strip if needed
		COL_SHAPES[0] = box(0, 0, 0, 15, 2, 16);
		COL_SHAPES[1] = box(0, 14, 0, 15, 16, 16);
		COL_SHAPES[2] = box(0, 0, 0, 15, 16, 2);
		COL_SHAPES[3] = box(0, 0, 14, 15, 16, 16);
		COL_SHAPES[4] = box(0, 0, 0, 2, 15, 16);
		COL_SHAPES[5] = box(14, 0, 0, 15, 16, 16);
	}

	public LargeGearSlave(){
		super(CRBlocks.getMetalProperty());
		String name = "large_gear_slave";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return COL_SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos){
		return VoxelShapes.empty();
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new LargeGearSlaveTileEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		RotaryUtil.increaseMasterKey(true);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof LargeGearSlaveTileEntity && ((LargeGearSlaveTileEntity) te).masterPos != null){
			te = world.getBlockEntity(pos.offset(((LargeGearSlaveTileEntity) te).masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				return CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) (te)).getMember(), 1);
			}
		}
		return ItemStack.EMPTY;	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		if(willHarvest && worldIn.getBlockEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getBlockEntity(pos)).passBreak(state.getValue(ESProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		if(worldIn.getBlockEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getBlockEntity(pos)).passBreak(state.getValue(ESProperties.FACING), false);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		return new ArrayList<>(0);
	}
}
