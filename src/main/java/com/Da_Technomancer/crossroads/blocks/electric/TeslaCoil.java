package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoil extends ContainerBlock{

	public TeslaCoil(){
		super(Material.IRON);
		String name = "tesla_coil";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(Properties.ACTIVE, false));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new TeslaCoilTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(Properties.HORIZ_FACING, placer == null ? Direction.EAST : placer.getHorizontalFacing().getOpposite()).with(Properties.ACTIVE, false);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos prevPos){
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
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if(EssentialsConfig.isWrench(heldItem, worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(Properties.HORIZ_FACING));
			}
			return true;
		}

		if(heldItem.getItem() == CRItems.leydenJar){
			if(!state.get(Properties.ACTIVE)){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isRemote){
						((TeslaCoilTileEntity) te).addJar(heldItem);
						playerIn.setHeldItem(hand, ItemStack.EMPTY);
						worldIn.setBlockState(pos, state.with(Properties.ACTIVE, true));
					}
					return true;
				}
			}
		}else if(heldItem.isEmpty()){
			if(state.get(Properties.ACTIVE)){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isRemote){
						playerIn.setHeldItem(hand, ((TeslaCoilTileEntity) te).removeJar());
						worldIn.setBlockState(pos, state.with(Properties.ACTIVE, false));
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.get(Properties.ACTIVE)){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TeslaCoilTileEntity){
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((TeslaCoilTileEntity) te).removeJar());
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING, Properties.ACTIVE);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.HORIZ_FACING, Direction.byHorizontalIndex(meta & 3)).with(Properties.ACTIVE, (meta & 4) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.HORIZ_FACING).getHorizontalIndex() + (state.get(Properties.ACTIVE) ? 4 : 0);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face.getAxis() == Direction.Axis.Y ? BlockFaceShape.SOLID : BlockFaceShape.CENTER_BIG;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Allows transferring FE when used with a Tesla Coil Top");
		tooltip.add("Different tops can increase range, transfer rate, or act as a weapon");
		tooltip.add("Insert a Leyden Jar to increase capacity");
	}
}
