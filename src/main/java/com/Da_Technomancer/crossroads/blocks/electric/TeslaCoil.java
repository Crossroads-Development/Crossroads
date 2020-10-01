package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
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

public class TeslaCoil extends ContainerBlock{

	private static VoxelShape SHAPE_EMPT = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 2, 16), makeCuboidShape(0, 14, 0, 16, 16, 16), makeCuboidShape(5, 2, 0, 11, 14, 1), makeCuboidShape(5, 2, 15, 11, 14,16), makeCuboidShape(0, 2, 5, 1, 4, 11), makeCuboidShape(15, 2, 5, 16, 14, 11));
	private static VoxelShape SHAPE_LEYD = VoxelShapes.or(SHAPE_EMPT, makeCuboidShape(5, 2, 5, 11, 14, 11));

	public TeslaCoil(){
		super(Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL));
		String name = "tesla_coil";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.ACTIVE, false));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new TeslaCoilTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return state.get(CRProperties.ACTIVE) ? SHAPE_LEYD : SHAPE_EMPT;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.HORIZ_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos prevPos, boolean isMoving){
		if(worldIn.isRemote){
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof TeslaCoilTileEntity){
			TeslaCoilTileEntity ts = (TeslaCoilTileEntity) te;
			if(worldIn.isBlockPowered(pos)){
				if(!ts.redstone){
					ts.redstone = true;
					ts.syncState();
					ts.markDirty();
				}
			}else if(ts.redstone){
				ts.redstone = false;
				ts.syncState();
				ts.markDirty();
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if(ESConfig.isWrench(heldItem)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.func_235896_a_(ESProperties.HORIZ_FACING));
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					((TeslaCoilTileEntity) te).rotate();
				}
			}
			return ActionResultType.SUCCESS;
		}

		if(heldItem.getItem() == CRItems.leydenJar){
			if(!state.get(CRProperties.ACTIVE)){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isRemote){
						((TeslaCoilTileEntity) te).addJar(heldItem);
						playerIn.setHeldItem(hand, ItemStack.EMPTY);
						worldIn.setBlockState(pos, state.with(CRProperties.ACTIVE, true));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}else if(heldItem.isEmpty()){
			if(state.get(CRProperties.ACTIVE)){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isRemote){
						playerIn.setHeldItem(hand, ((TeslaCoilTileEntity) te).removeJar());
						worldIn.setBlockState(pos, state.with(CRProperties.ACTIVE, false));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.get(CRProperties.ACTIVE) && newState.getBlock() != this){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TeslaCoilTileEntity){
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((TeslaCoilTileEntity) te).removeJar());
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, ESProperties.HORIZ_FACING);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.top"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.leyden", LeydenJar.MAX_CHARGE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
