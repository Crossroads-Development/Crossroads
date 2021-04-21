package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
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

public class LargeGearMaster extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		//Create a shape for each facing. Base plate + axle
		SHAPES[0] = VoxelShapes.or(box(0, 0, 0, 16, 2, 16), box(7, 2, 7, 9, 16, 9));
		SHAPES[1] = VoxelShapes.or(box(0, 14, 0, 16, 16, 16), box(7, 0, 7, 9, 14, 9));
		SHAPES[2] = VoxelShapes.or(box(0, 0, 0, 16, 16, 2), box(7, 7, 2, 9, 9, 16));
		SHAPES[3] = VoxelShapes.or(box(0, 0, 14, 16, 16, 16), box(7, 7, 0, 9, 9, 14));
		SHAPES[4] = VoxelShapes.or(box(0, 0, 0, 2, 16, 16), box(2, 7, 7, 16, 9, 9));
		SHAPES[5] = VoxelShapes.or(box(14, 0, 0, 16, 16, 16), box(0, 7, 7, 14, 9, 9));
	}

	public LargeGearMaster(){
		super(CRBlocks.getMetalProperty());
		String name = "large_gear_master";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new LargeGearMasterTileEntity();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof LargeGearMasterTileEntity){
			return CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) te).getMember(), 1);
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		List<ItemStack> drops = new ArrayList<>();
		TileEntity te = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
		if(te instanceof LargeGearMasterTileEntity){
			drops.add(CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) te).getMember(), 1));
		}
		return drops;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		if(willHarvest && worldIn.getBlockEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getBlockEntity(pos)).breakGroup(state.getValue(ESProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		if(worldIn.getBlockEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getBlockEntity(pos)).breakGroup(state.getValue(ESProperties.FACING), false);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}
}
