package com.Da_Technomancer.crossroads.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.SortingHopperTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SortingHopper extends BlockContainer{

	public static final PropertyDirection FACING = BlockHopper.FACING;
	public static final PropertyBool ENABLED = BlockHopper.ENABLED;
	private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

	public SortingHopper(){
		super(Material.IRON);
		String name = "sortingHopper";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setSoundType(SoundType.METAL);
		this.setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, Boolean.valueOf(true)));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return FULL_BLOCK_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn){
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		EnumFacing enumfacing = facing.getOpposite();

		if(enumfacing == EnumFacing.UP){
			enumfacing = EnumFacing.DOWN;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ENABLED, Boolean.valueOf(true));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SortingHopperTileEntity();
	}

	@Override
	public boolean isFullyOpaque(IBlockState state){
		return true;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state){
		this.updateState(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if(tileentity instanceof SortingHopperTileEntity){
				playerIn.displayGUIChest((SortingHopperTileEntity) tileentity);
				playerIn.addStat(StatList.HOPPER_INSPECTED);
			}
		}
		return true;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		this.updateState(worldIn, pos, state);
	}

	private void updateState(World worldIn, BlockPos pos, IBlockState state){
		boolean flag = !worldIn.isBlockPowered(pos);

		if(flag != state.getValue(ENABLED).booleanValue()){
			worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 4);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof SortingHopperTileEntity){
			InventoryHelper.dropInventoryItems(worldIn, pos, (SortingHopperTileEntity) tileentity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		return true;
	}

	public static boolean isEnabled(int meta){
		return (meta & 8) != 8;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(ENABLED, Boolean.valueOf(isEnabled(meta)));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(FACING).getIndex() + (state.getValue(ENABLED) ? 0 : 8);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {FACING, ENABLED});
	}
}
