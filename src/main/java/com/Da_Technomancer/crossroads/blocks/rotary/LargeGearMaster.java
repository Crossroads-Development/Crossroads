package com.Da_Technomancer.crossroads.blocks.rotary;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LargeGearMaster extends BlockContainer{
	
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, .5D);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(0D, 0D, .5D, 1D, 1D, 1D);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(.5D, 0D, 0D, 1D, 1D, 1D);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0D, 0D, 0D, .5D, 1D, 1D);
	private static final AxisAlignedBB UP = new AxisAlignedBB(0D, .5D, 0D, 1D, 1D, 1D);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(0D, 0D, 0D, 1D, .5D, 1D);
	
	public LargeGearMaster(){
		super(Material.IRON);
		String name = "large_gear_master";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new LargeGearMasterTileEntity();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		if(world.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			return new ItemStack(GearFactory.LARGE_GEARS.get(((LargeGearMasterTileEntity) world.getTileEntity(pos)).getMember()), 1);
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING});
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		switch(state.getValue(Properties.FACING)){
			case UP:
				return UP;
			case DOWN:
				return DOWN;
			case NORTH:
				return NORTH;
			case SOUTH:
				return SOUTH;
			case EAST:
				return EAST;
			default:
				return WEST;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos){
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		List<ItemStack> drops = new ArrayList<ItemStack>();
		LargeGearMasterTileEntity te = (LargeGearMasterTileEntity) world.getTileEntity(pos);
		if(te.getMember() != null){
			drops.add(new ItemStack(GearFactory.LARGE_GEARS.get(te.getMember())));
		}
		return drops;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean canHarvest){
		if(canHarvest && worldIn.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).breakGroup(state.getValue(Properties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, canHarvest);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		if(worldIn.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).breakGroup(state.getValue(Properties.FACING), false);
		}
		super.breakBlock(worldIn, pos, state);
	}
}
