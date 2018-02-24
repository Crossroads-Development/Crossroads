package com.Da_Technomancer.crossroads.blocks.technomancy;

import java.util.List;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CageChargerTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CageCharger extends BlockContainer{

	public CageCharger(){
		super(Material.IRON);
		String name = "cage_charger";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new CageChargerTileEntity();
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
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.HEAD, false);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.HEAD});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HEAD, meta == 1);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			if(state.getValue(Properties.HEAD)){
				playerIn.inventory.addItemStackToInventory(((CageChargerTileEntity) worldIn.getTileEntity(pos)).getCage());
				((CageChargerTileEntity) worldIn.getTileEntity(pos)).setCage(ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.HEAD, false));
			}else if(!playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == ModItems.beamCage){
				((CageChargerTileEntity) worldIn.getTileEntity(pos)).setCage(playerIn.getHeldItem(hand));
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.HEAD, true));
			}
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(state.getValue(Properties.HEAD)){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((CageChargerTileEntity) world.getTileEntity(pos)).getCage());
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HEAD) ? 1 : 0;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	private static final AxisAlignedBB BB = new AxisAlignedBB(.25D, 0, .25D, .75D, .5D, .75D);
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean sproinksOrSomethingIDontKnowItsLateAndImTired){
		addCollisionBoxToList(pos, mask, list, BB);
	}
}
