package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.beams.LensFrameTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class LensFrame extends BlockContainer{

	public LensFrame(){
		super(Material.ROCK);
		String name = "lens_frame";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new LensFrameTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		return Math.min(15, (int) ((LensFrameTileEntity) worldIn.getTileEntity(pos)).getRedstone() / 3);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(EssentialsProperties.AXIS, (placer == null) ? Axis.X : blockFaceClickedOn.getAxis());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.AXIS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(EssentialsProperties.AXIS, EnumFacing.Axis.values()[meta]);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ItemStack stack = playerIn.getHeldItem(hand);

			if(EssentialsConfig.isWrench(stack, false)){
				worldIn.setBlockState(pos, state.cycleProperty(EssentialsProperties.AXIS));
				((LensFrameTileEntity) worldIn.getTileEntity(pos)).refresh();
			}else{
				TileEntity te = worldIn.getTileEntity(pos);
				if(!(te instanceof LensFrameTileEntity)){
					return false;
				}
				LensFrameTileEntity lens = (LensFrameTileEntity) te;
				ItemStack held = lens.getItem();
				if(!held.isEmpty()){
					if(!playerIn.inventory.addItemStackToInventory(held)){
						EntityItem dropped = playerIn.dropItem(held, false);
						dropped.setNoPickupDelay();
						dropped.setOwner(playerIn.getName());
					}
					lens.setContents(0);
				}else if(!stack.isEmpty()){
					int id = lens.getIDFromItem(stack);
					if(id != 0){
						lens.setContents(id);
						stack.shrink(1);
					}
				}
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LensFrameTileEntity){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((LensFrameTileEntity) te).getItem());
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(EssentialsProperties.AXIS).ordinal();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[] {new AxisAlignedBB(.375D, 0, 0, .625D, 1, 1), new AxisAlignedBB(0, .375D, 0, 1, .625D, 1), new AxisAlignedBB(0, 0, .375D, 1, 1, .625D)};

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB[state.getValue(EssentialsProperties.AXIS).ordinal()];
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean stuff){
		addCollisionBoxToList(pos, mask, list, getBoundingBox(state, worldIn, pos));
	}
}
