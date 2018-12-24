package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.RatiatorTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class Ratiator extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

	protected Ratiator(){
		super(Material.CIRCUITS);
		String name = "ratiator";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(0);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RatiatorTileEntity();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
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
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		return worldIn.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP);
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		return state.getWeakPower(blockAccess, pos, side);
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(side == state.getValue(Properties.HORIZ_FACING).getOpposite()){
			double d = ((RatiatorTileEntity) blockAccess.getTileEntity(pos)).getOutput();
			if(d >= 15){
				return 15;
			}
			return (int) Math.round(d);
		}else{
			return 0;
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(!canPlaceBlockAt(worldIn, pos)){
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			return;
		}

		if(!worldIn.isBlockTickPending(pos, this)){
			int i = -1;

			if(BlockRedstoneDiode.isDiode(worldIn.getBlockState(pos.offset(state.getValue(Properties.HORIZ_FACING))))){
				i = -3;
			}
			worldIn.updateBlockTick(pos, this, 2, i);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, null, null);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		TileEntity rawTE = worldIn.getTileEntity(pos);
		if(rawTE == null){
			return;
		}
		RatiatorTileEntity te = ((RatiatorTileEntity) rawTE);
		double lastOut = te.getOutput();
		double sidePower = Math.max(RedstoneUtil.getDirectPowerOnSide(worldIn, pos, state.getValue(Properties.HORIZ_FACING).rotateY()), RedstoneUtil.getDirectPowerOnSide(worldIn, pos, state.getValue(Properties.HORIZ_FACING).rotateYCCW()));
		double backPower = RedstoneUtil.getMeasuredPower(worldIn, pos, state.getValue(Properties.HORIZ_FACING).getOpposite());
		te.setOutput(state.getValue(EssentialsProperties.REDSTONE_BOOL) ? backPower / (sidePower == 0 ? 1D : sidePower) : backPower * sidePower);
		if(lastOut != te.getOutput()){
			worldIn.neighborChanged(pos.offset(state.getValue(Properties.HORIZ_FACING)), this, pos);
			worldIn.notifyNeighborsOfStateExcept(pos.offset(state.getValue(Properties.HORIZ_FACING)), this, state.getValue(Properties.HORIZ_FACING).getOpposite());
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), false)){
				IBlockState newState = state.withProperty(Properties.HORIZ_FACING, state.getValue(Properties.HORIZ_FACING).rotateY());
				worldIn.setBlockState(pos, newState);
				neighborChanged(newState, worldIn, pos, null, null);
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof RatiatorTileEntity){
					((RatiatorTileEntity) te).onRotate();
				}
				return true;
			}

			boolean oldValue = state.getValue(EssentialsProperties.REDSTONE_BOOL);
			worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, !oldValue));
			worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, oldValue ? .55F : .5F);
			neighborChanged(state.withProperty(EssentialsProperties.REDSTONE_BOOL, !state.getValue(EssentialsProperties.REDSTONE_BOOL)), worldIn, pos, this, pos);
		}
		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return side != null && side.getAxis() != EnumFacing.Axis.Y;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING, EssentialsProperties.REDSTONE_BOOL);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZ_FACING).getIndex() + (state.getValue(EssentialsProperties.REDSTONE_BOOL) ? 8 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, EnumFacing.byIndex(meta & 7)).withProperty(EssentialsProperties.REDSTONE_BOOL, meta >= 8);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing();
		return getDefaultState().withProperty(Properties.HORIZ_FACING, enumfacing).withProperty(EssentialsProperties.REDSTONE_BOOL, false);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		((RatiatorTileEntity) worldIn.getTileEntity(pos)).setOutput(0);
		worldIn.neighborChanged(pos.offset(state.getValue(Properties.HORIZ_FACING)), this, pos);
		worldIn.notifyNeighborsOfStateExcept(pos.offset(state.getValue(Properties.HORIZ_FACING)), this, state.getValue(Properties.HORIZ_FACING).getOpposite());
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos){
		return true;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		if(pos.getY() == neighbor.getY() && world instanceof World){
			neighborChanged(world.getBlockState(pos), (World) world, pos, world.getBlockState(neighbor).getBlock(), neighbor);
		}
	}
}
