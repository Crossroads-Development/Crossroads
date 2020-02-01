package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

import java.util.ArrayList;
import java.util.List;

public class LargeGearSlave extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	private static final VoxelShape[] COL_SHAPES = new VoxelShape[6];

	static{
		//Create a shape for each facing. Solid base plate
		SHAPES[0] = makeCuboidShape(0, 0, 0, 16, 2, 16);
		SHAPES[1] = makeCuboidShape(0, 14, 0, 16, 16, 16);
		SHAPES[2] = makeCuboidShape(0, 0, 0, 16, 16, 2);
		SHAPES[3] = makeCuboidShape(0, 0, 14, 16, 16, 16);
		SHAPES[4] = makeCuboidShape(0, 0, 0, 2, 16, 16);
		SHAPES[5] = makeCuboidShape(14, 0, 0, 16, 16, 16);

		//Create a collision shape for each facing. A small strip is missing to prevent it being considered "solid" for things like torches, while still blocking basically all entity movement
		//Note: could be changed to have one pixel missing instead of a strip if needed
		COL_SHAPES[0] = makeCuboidShape(0, 0, 0, 15, 2, 16);
		COL_SHAPES[1] = makeCuboidShape(0, 14, 0, 15, 16, 16);
		COL_SHAPES[2] = makeCuboidShape(0, 0, 0, 15, 16, 2);
		COL_SHAPES[3] = makeCuboidShape(0, 0, 14, 15, 16, 16);
		COL_SHAPES[4] = makeCuboidShape(0, 0, 0, 2, 15, 16);
		COL_SHAPES[5] = makeCuboidShape(14, 0, 0, 15, 16, 16);
	}

	public LargeGearSlave(){
		super(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3));
		String name = "large_gear_slave";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return COL_SHAPES[state.get(ESProperties.FACING).getIndex()];
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.FACING).getIndex()];
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new LargeGearSlaveTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		RotaryUtil.increaseMasterKey(true);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LargeGearSlaveTileEntity && ((LargeGearSlaveTileEntity) te).masterPos != null){
			te = world.getTileEntity(pos.add(((LargeGearSlaveTileEntity) te).masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				return new ItemStack(GearFactory.gearTypes.get(((LargeGearMasterTileEntity) (te)).getMember()).getLargeGear(), 1);
			}
		}
		return ItemStack.EMPTY;	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid){
		if(willHarvest && worldIn.getTileEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos)).passBreak(state.get(ESProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		if(worldIn.getTileEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos)).passBreak(state.get(ESProperties.FACING), false);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		return new ArrayList<>(0);
	}
}
