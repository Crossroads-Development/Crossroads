package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CageChargerTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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

import javax.annotation.Nullable;
import java.util.List;

public class CageCharger extends BlockContainer{

	public CageCharger(){
		super(Material.IRON);
		String name = "cage_charger";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().withProperty(Properties.ACTIVE, false));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new CageChargerTileEntity();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Charges Beam Cages placed on it with incoming beams");
		tooltip.add("Ratiators measure the total power in the held beam cage");
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
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.ACTIVE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.ACTIVE, meta == 1);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			if(state.getValue(Properties.ACTIVE)){
				playerIn.inventory.addItemStackToInventory(((CageChargerTileEntity) worldIn.getTileEntity(pos)).getCage());
				((CageChargerTileEntity) worldIn.getTileEntity(pos)).setCage(ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.ACTIVE, false));
			}else if(!playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == ModItems.beamCage){
				((CageChargerTileEntity) worldIn.getTileEntity(pos)).setCage(playerIn.getHeldItem(hand));
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.ACTIVE, true));
			}
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(state.getValue(Properties.ACTIVE)){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((CageChargerTileEntity) world.getTileEntity(pos)).getCage());
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.ACTIVE) ? 1 : 0;
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
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean sproinksOrSomethingIDontKnowItsLateAndImTired){
		addCollisionBoxToList(pos, mask, list, BB);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		IAdvancedRedstoneHandler handler;
		if(te != null && (handler = te.getCapability(Capabilities.ADVANCED_REDSTONE_CAPABILITY, null)) != null){
			return (int) handler.getOutput(true);
		}
		return 0;
	}
}
