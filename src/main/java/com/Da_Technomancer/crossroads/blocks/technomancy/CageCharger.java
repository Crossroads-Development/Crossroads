package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class CageCharger extends BaseEntityBlock implements IReadable{

	private static final VoxelShape SHAPE = Shapes.or(box(0, 0, 0, 16, 4, 16), box(4, 4, 4, 12, 8, 12));

	public CageCharger(){
		super(CRBlocks.getMetalProperty());
		String name = "cage_charger";
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new CageChargerTileEntity(pos, state);
	}

//	Non-ticking tile entity
//	@Nullable
//	@Override
//	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
//		return ITickableTileEntity.createTicker(type, CageChargerTileEntity.TYPE);
//	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.cage_charger.desc"));
		tooltip.add(Component.translatable("tt.crossroads.cage_charger.redstone"));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) != null){
			if(state.getValue(CRProperties.ACTIVE)){
				playerIn.getInventory().add(((CageChargerTileEntity) te).getCage());
				((CageChargerTileEntity) te).setCage(ItemStack.EMPTY);
				worldIn.setBlockAndUpdate(pos, defaultBlockState().setValue(CRProperties.ACTIVE, false));
			}else if(!playerIn.getItemInHand(hand).isEmpty() && playerIn.getItemInHand(hand).getItem() == CRItems.beamCage){
				((CageChargerTileEntity) te).setCage(playerIn.getItemInHand(hand));
				playerIn.setItemInHand(hand, ItemStack.EMPTY);
				worldIn.setBlockAndUpdate(pos, defaultBlockState().setValue(CRProperties.ACTIVE, true));
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		if(!isMoving && state.getValue(CRProperties.ACTIVE) && newState.getBlock() != this){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((CageChargerTileEntity) world.getBlockEntity(pos)).getCage());
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		if(state.getValue(CRProperties.ACTIVE)){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof CageChargerTileEntity){
				ItemStack cage = ((CageChargerTileEntity) te).getCage();
				return BeamCage.getStored(cage).getPower();
			}
		}
		return 0;
	}
}
