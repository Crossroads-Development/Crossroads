package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TeslaCoil extends BlockContainer{

	public TeslaCoil(){
		super(Material.IRON);
		String name = "tesla_coil";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().withProperty(Properties.LIGHT, false).withProperty(Properties.ACTIVE, false));
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
		return getDefaultState().withProperty(Properties.HORIZONTAL_FACING, placer == null ? EnumFacing.EAST : placer.getHorizontalFacing().getOpposite()).withProperty(Properties.ACTIVE, false);
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
		if(!(worldIn.getBlockState(pos.offset(EnumFacing.UP)).getBlock() instanceof TeslaCoilTop)){
			worldIn.destroyBlock(pos, true);
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.setBlockState(pos.offset(EnumFacing.UP), ModBlocks.teslaCoilTop.getDefaultState());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if(EssentialsConfig.isWrench(heldItem, worldIn.isRemote)){
			if(!worldIn.isRemote){
				if(playerIn.isSneaking()){
					worldIn.setBlockState(pos, state.cycleProperty(Properties.LIGHT));
					playerIn.sendMessage(new TextComponentString("Attack Mode: " + (state.getValue(Properties.LIGHT) ? "DISABLED" : "ENABLED")));
				}else{
					worldIn.setBlockState(pos, state.cycleProperty(Properties.HORIZONTAL_FACING));
				}
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
		return new BlockStateContainer(this, Properties.HORIZONTAL_FACING, Properties.ACTIVE, Properties.LIGHT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZONTAL_FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(Properties.ACTIVE, (meta & 4) != 0).withProperty(Properties.LIGHT, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZONTAL_FACING).getHorizontalIndex() + (state.getValue(Properties.ACTIVE) ? 4 : 0) + (state.getValue(Properties.LIGHT) ? 8 : 0);
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state){
		return EnumPushReaction.BLOCK;
	}
}
