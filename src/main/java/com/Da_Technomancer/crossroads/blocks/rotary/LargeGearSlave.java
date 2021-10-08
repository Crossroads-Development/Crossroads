package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class LargeGearSlave extends BaseEntityBlock{

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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return COL_SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos){
		return Shapes.empty();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new LargeGearSlaveTileEntity();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.INVISIBLE;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		RotaryUtil.increaseMasterKey(true);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof LargeGearSlaveTileEntity && ((LargeGearSlaveTileEntity) te).masterPos != null){
			te = world.getBlockEntity(pos.offset(((LargeGearSlaveTileEntity) te).masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				return CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) (te)).getMember(), 1);
			}
		}
		return ItemStack.EMPTY;	}

	@Override
	public boolean removedByPlayer(BlockState state, Level worldIn, BlockPos pos, Player player, boolean willHarvest, FluidState fluid){
		if(willHarvest && worldIn.getBlockEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getBlockEntity(pos)).passBreak(state.getValue(ESProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving){
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
