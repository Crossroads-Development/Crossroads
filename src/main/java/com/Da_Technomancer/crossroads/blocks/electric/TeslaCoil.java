package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoil extends BaseEntityBlock implements IReadable{

	private static final VoxelShape SHAPE_EMPT = Shapes.or(box(0, 0, 0, 16, 2, 16), box(0, 14, 0, 16, 16, 16), box(5, 2, 0, 11, 14, 1), box(5, 2, 15, 11, 14,16), box(0, 2, 5, 1, 4, 11), box(15, 2, 5, 16, 14, 11));
	private static final VoxelShape SHAPE_LEYD = Shapes.or(SHAPE_EMPT, box(5, 2, 5, 11, 14, 11));

	public TeslaCoil(){
		super(CRBlocks.getMetalProperty());
		String name = "tesla_coil";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new TeslaCoilTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return state.getValue(CRProperties.ACTIVE) ? SHAPE_LEYD : SHAPE_EMPT;
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.HORIZ_FACING, context.getHorizontalDirection().getOpposite());
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

		if(ESConfig.isWrench(heldItem)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.HORIZ_FACING));
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					((TeslaCoilTileEntity) te).rotate();
				}
			}
			return InteractionResult.SUCCESS;
		}

		if(heldItem.getItem() == CRItems.leydenJar){
			if(!state.getValue(CRProperties.ACTIVE)){
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isClientSide){
						((TeslaCoilTileEntity) te).addJar(heldItem);
						playerIn.setItemInHand(hand, ItemStack.EMPTY);
						worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.ACTIVE, true));
					}
					return InteractionResult.SUCCESS;
				}
			}
		}else if(heldItem.isEmpty()){
			if(state.getValue(CRProperties.ACTIVE)){
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isClientSide){
						playerIn.setItemInHand(hand, ((TeslaCoilTileEntity) te).removeJar());
						worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.ACTIVE, false));
					}
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.getValue(CRProperties.ACTIVE) && newState.getBlock() != this){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof TeslaCoilTileEntity){
				Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((TeslaCoilTileEntity) te).removeJar());
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, ESProperties.HORIZ_FACING);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_coil.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_coil.top"));
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_coil.leyden", LeydenJar.MAX_CHARGE));
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_coil.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, state));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof TeslaCoilTileEntity){
			return ((TeslaCoilTileEntity) te).getRedstone();
		}else{
			return 0;
		}
	}
}
