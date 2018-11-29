package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoil extends BlockContainer{

	public TeslaCoil(){
		super(Material.IRON);
		String name = "tesla_coil";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().withProperty(Properties.ACTIVE, false));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new TeslaCoilTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, placer == null ? EnumFacing.EAST : placer.getHorizontalFacing().getOpposite()).withProperty(Properties.ACTIVE, false);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.offset(EnumFacing.UP));
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos prevPos){
		if(worldIn.isRemote){
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof TeslaCoilTileEntity){
			TeslaCoilTileEntity ts = (TeslaCoilTileEntity) te;
			if(worldIn.isBlockPowered(pos)){
				if(!ts.redstone){
					ts.redstone = true;
					ts.markDirty();
				}
			}else if(ts.redstone){
				ts.redstone = false;
				ts.markDirty();
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if(EssentialsConfig.isWrench(heldItem, worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycleProperty(Properties.HORIZ_FACING));
			}
			return true;
		}

		if(heldItem.getItem() == ModItems.leydenJar){
			if(!state.getValue(Properties.ACTIVE)){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isRemote){
						((TeslaCoilTileEntity) te).addJar(heldItem);
						playerIn.setHeldItem(hand, ItemStack.EMPTY);
						worldIn.setBlockState(pos, state.withProperty(Properties.ACTIVE, true));
					}
					return true;
				}
			}
		}else if(heldItem.isEmpty()){
			if(state.getValue(Properties.ACTIVE)){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TeslaCoilTileEntity){
					if(!worldIn.isRemote){
						playerIn.setHeldItem(hand, ((TeslaCoilTileEntity) te).removeJar());
						worldIn.setBlockState(pos, state.withProperty(Properties.ACTIVE, false));
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(state.getValue(Properties.ACTIVE)){
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
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(Properties.ACTIVE, (meta & 4) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZ_FACING).getHorizontalIndex() + (state.getValue(Properties.ACTIVE) ? 4 : 0);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return face.getAxis() == EnumFacing.Axis.Y ? BlockFaceShape.SOLID : BlockFaceShape.CENTER_BIG;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Allows transferring FE when used with a Tesla Coil Top");
		tooltip.add("Different tops can increase range, transfer rate, or act as a weapon");
		tooltip.add("Insert a Leyden Jar to increase capacity");
	}
}
