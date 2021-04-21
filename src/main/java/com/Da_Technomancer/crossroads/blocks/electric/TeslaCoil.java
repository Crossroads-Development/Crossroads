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
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoil extends ContainerBlock implements IReadable{

	private static final VoxelShape SHAPE_EMPT = VoxelShapes.or(box(0, 0, 0, 16, 2, 16), box(0, 14, 0, 16, 16, 16), box(5, 2, 0, 11, 14, 1), box(5, 2, 15, 11, 14,16), box(0, 2, 5, 1, 4, 11), box(15, 2, 5, 16, 14, 11));
	private static final VoxelShape SHAPE_LEYD = VoxelShapes.or(SHAPE_EMPT, box(5, 2, 5, 11, 14, 11));

	public TeslaCoil(){
		super(CRBlocks.getMetalProperty());
		String name = "tesla_coil";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new TeslaCoilTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return state.getValue(CRProperties.ACTIVE) ? SHAPE_LEYD : SHAPE_EMPT;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(ESProperties.HORIZ_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos prevPos, boolean isMoving){
		if(worldIn.isClientSide){
			return;
		}
		TileEntity te = worldIn.getBlockEntity(pos);
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
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);

		if(ESConfig.isWrench(heldItem)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.HORIZ_FACING));
				TileEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					((TeslaCoilTileEntity) te).rotate();
				}
			}
			return ActionResultType.SUCCESS;
		}

		if(heldItem.getItem() == CRItems.leydenJar){
			if(!state.getValue(CRProperties.ACTIVE)){
				TileEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isClientSide){
						((TeslaCoilTileEntity) te).addJar(heldItem);
						playerIn.setItemInHand(hand, ItemStack.EMPTY);
						worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.ACTIVE, true));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}else if(heldItem.isEmpty()){
			if(state.getValue(CRProperties.ACTIVE)){
				TileEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isClientSide){
						playerIn.setItemInHand(hand, ((TeslaCoilTileEntity) te).removeJar());
						worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.ACTIVE, false));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.getValue(CRProperties.ACTIVE) && newState.getBlock() != this){
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof TeslaCoilTileEntity){
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((TeslaCoilTileEntity) te).removeJar());
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, ESProperties.HORIZ_FACING);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.top"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.leyden", LeydenJar.MAX_CHARGE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, state));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof TeslaCoilTileEntity){
			return ((TeslaCoilTileEntity) te).getRedstone();
		}else{
			return 0;
		}
	}
}
