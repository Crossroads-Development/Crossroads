package com.Da_Technomancer.crossroads.blocks.technomancy;

import java.util.List;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.StaffChargerTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StaffCharger extends BlockContainer{

	public StaffCharger(){
		super(Material.IRON);
		String name = "staffCharger";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new StaffChargerTileEntity();
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
				playerIn.inventory.addItemStackToInventory(((StaffChargerTileEntity) worldIn.getTileEntity(pos)).getStaff());
				((StaffChargerTileEntity) worldIn.getTileEntity(pos)).setStaff(ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.HEAD, false));
			}else if(!playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == ModItems.staffTechnomancy){
				((StaffChargerTileEntity) worldIn.getTileEntity(pos)).setStaff(playerIn.getHeldItem(hand));
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.HEAD, true));
			}
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(state.getValue(Properties.HEAD)){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((StaffChargerTileEntity) world.getTileEntity(pos)).getStaff());
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
