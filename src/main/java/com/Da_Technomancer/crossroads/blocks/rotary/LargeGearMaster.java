package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LargeGearMaster extends BaseEntityBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		//Create a shape for each facing. Base plate + axle
		SHAPES[0] = Shapes.or(box(0, 0, 0, 16, 2, 16), box(7, 2, 7, 9, 16, 9));
		SHAPES[1] = Shapes.or(box(0, 14, 0, 16, 16, 16), box(7, 0, 7, 9, 14, 9));
		SHAPES[2] = Shapes.or(box(0, 0, 0, 16, 16, 2), box(7, 7, 2, 9, 9, 16));
		SHAPES[3] = Shapes.or(box(0, 0, 14, 16, 16, 16), box(7, 7, 0, 9, 9, 14));
		SHAPES[4] = Shapes.or(box(0, 0, 0, 2, 16, 16), box(2, 7, 7, 16, 9, 9));
		SHAPES[5] = Shapes.or(box(14, 0, 0, 16, 16, 16), box(0, 7, 7, 14, 9, 9));
	}

	public LargeGearMaster(){
		super(CRBlocks.getMetalProperty());
		String name = "large_gear_master";
		CRBlocks.queueForRegister(name, this, false, null);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new LargeGearMasterTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, LargeGearMasterTileEntity.TYPE);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof LargeGearMasterTileEntity){
			return CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) te).getMember(), 1);
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.FACING).get3DDataValue()];
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		List<ItemStack> drops = new ArrayList<>();
		BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(te instanceof LargeGearMasterTileEntity){
			drops.add(CRItems.largeGear.withMaterial(((LargeGearMasterTileEntity) te).getMember(), 1));
		}
		return drops;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level worldIn, BlockPos pos, Player player, boolean willHarvest, FluidState fluid){
		if(willHarvest && worldIn.getBlockEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getBlockEntity(pos)).breakGroup(state.getValue(CRProperties.FACING), true);
		}
		return super.onDestroyedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		if(worldIn.getBlockEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getBlockEntity(pos)).breakGroup(state.getValue(CRProperties.FACING), false);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}
}
