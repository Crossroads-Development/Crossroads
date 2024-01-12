package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.TEBlock;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoil extends TEBlock implements IReadable{

	private static final VoxelShape SHAPE_EMPT = Shapes.or(box(0, 0, 0, 16, 2, 16), box(0, 14, 0, 16, 16, 16), box(5, 2, 0, 11, 14, 1), box(5, 2, 15, 11, 14,16), box(0, 2, 5, 1, 4, 11), box(15, 2, 5, 16, 14, 11));
	private static final VoxelShape SHAPE_LEYD = Shapes.or(SHAPE_EMPT, box(5, 2, 5, 11, 14, 11));

	public TeslaCoil(){
		super(CRBlocks.getMetalProperty());
		String name = "tesla_coil";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new TeslaCoilTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, TeslaCoilTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return state.getValue(CRProperties.ACTIVE) ? SHAPE_LEYD : SHAPE_EMPT;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos prevPos, boolean isMoving){
		if(worldIn.isClientSide){
			return;
		}
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te instanceof TeslaCoilTileEntity){
			TeslaCoilTileEntity ts = (TeslaCoilTileEntity) te;
			if(worldIn.hasNeighborSignal(pos)){
				if(!ts.redstone){
					ts.redstone = true;
					ts.syncState();
					ts.setChanged();
				}
			}else if(ts.redstone){
				ts.redstone = false;
				ts.syncState();
				ts.setChanged();
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);

		if(ConfigUtil.isWrench(heldItem)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_FACING));
				if(worldIn.getBlockEntity(pos) instanceof TeslaCoilTileEntity tte){
					tte.rotate();
				}
			}
			return InteractionResult.SUCCESS;
		}

		if(!worldIn.isClientSide && worldIn.getBlockEntity(pos) instanceof TeslaCoilTileEntity te){
			if(heldItem.isEmpty()){
				playerIn.setItemInHand(hand, te.removeBattery());
			}else{
				playerIn.setItemInHand(hand, te.addBattery(heldItem));
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, CRProperties.HORIZ_FACING);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil.desc"));
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil.top"));
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil.leyden", LeydenJar.MAX_CHARGE));
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		if(world.getBlockEntity(pos) instanceof TeslaCoilTileEntity te){
			return te.getRedstone();
		}else{
			return 0;
		}
	}
}
