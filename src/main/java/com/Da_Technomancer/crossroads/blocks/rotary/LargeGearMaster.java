package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import java.util.ArrayList;
import java.util.List;

public class LargeGearMaster extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		//Create a shape for each facing. Base plate + axle
		SHAPES[0] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 2, 16), makeCuboidShape(7, 2, 7, 9, 16, 9));
		SHAPES[1] = VoxelShapes.or(makeCuboidShape(0, 14, 0, 16, 16, 16), makeCuboidShape(7, 0, 7, 9, 14, 9));
		SHAPES[2] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 16, 2), makeCuboidShape(7, 7, 2, 9, 9, 16));
		SHAPES[3] = VoxelShapes.or(makeCuboidShape(0, 0, 14, 16, 16, 16), makeCuboidShape(7, 7, 0, 9, 9, 14));
		SHAPES[4] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 2, 16, 16), makeCuboidShape(2, 7, 7, 16, 9, 9));
		SHAPES[5] = VoxelShapes.or(makeCuboidShape(14, 0, 0, 16, 16, 16), makeCuboidShape(0, 7, 7, 14, 9, 9));
	}

	public LargeGearMaster(){
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		String name = "large_gear_master";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new LargeGearMasterTileEntity();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LargeGearMasterTileEntity){
			return CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) te).getMember(), 1);
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.FACING).getIndex()];
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		List<ItemStack> drops = new ArrayList<>();
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
		if(te instanceof LargeGearMasterTileEntity){
			drops.add(CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) te).getMember(), 1));
		}
		return drops;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid){
		if(willHarvest && worldIn.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).breakGroup(state.get(ESProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		if(worldIn.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).breakGroup(state.get(ESProperties.FACING), false);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
}
