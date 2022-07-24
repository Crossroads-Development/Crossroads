package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCannon extends AbstractCannon{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = Shapes.or(box(0, 7, 0, 16, 16, 16));
		SHAPES[1] = Shapes.or(box(0, 0, 0, 16, 9, 16));
		SHAPES[2] = Shapes.or(box(0, 0, 7, 16, 16, 16));
		SHAPES[3] = Shapes.or(box(0, 0, 0, 16, 16, 9));
		SHAPES[4] = Shapes.or(box(7, 0, 0, 16, 16, 16));
		SHAPES[5] = Shapes.or(box(0, 0, 0, 9, 16, 16));
	}

	public ItemCannon(){
		super("item_cannon", CRBlocks.getMetalProperty());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.FACING).get3DDataValue()];
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ItemCannonTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, ItemCannonTileEntity.TYPE);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof ItemCannonTileEntity cta && newState.getBlock() != state.getBlock()){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), cta.inventory);
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		BlockEntity te = worldIn.getBlockEntity(pos);

		if(te instanceof ItemCannonTileEntity bte){
			CircuitUtil.updateFromWorld(bte.redsHandler, blockIn);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.item_cannon.desc"));
		tooltip.add(Component.translatable("tt.crossroads.beam_cannon.angle"));
		tooltip.add(Component.translatable("tt.crossroads.beam_cannon.lockable"));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", BeamCannonTileEntity.INERTIA));
	}
}
