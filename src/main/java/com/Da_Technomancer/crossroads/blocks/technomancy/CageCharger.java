package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CageChargerTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CageCharger extends ContainerBlock{

	public CageCharger(){
		super(Material.IRON);
		String name = "cage_charger";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(Properties.ACTIVE, false));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new CageChargerTileEntity();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Charges Beam Cages placed on it with incoming beams");
		tooltip.add("Ratiators measure the total power in the held beam cage");
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.ACTIVE);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.ACTIVE, meta == 1);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(!worldIn.isRemote && (te = worldIn.getTileEntity(pos)) != null){
			if(state.get(Properties.ACTIVE)){
				playerIn.inventory.addItemStackToInventory(((CageChargerTileEntity) te).getCage());
				((CageChargerTileEntity) te).setCage(ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().with(Properties.ACTIVE, false));
			}else if(!playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == CRItems.beamCage){
				((CageChargerTileEntity) te).setCage(playerIn.getHeldItem(hand));
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
				worldIn.setBlockState(pos, getDefaultState().with(Properties.ACTIVE, true));
			}
		}
		return true;
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.get(Properties.ACTIVE)){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((CageChargerTileEntity) world.getTileEntity(pos)).getCage());
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.ACTIVE) ? 1 : 0;
	}
	
	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}
	
	private static final AxisAlignedBB BB = new AxisAlignedBB(.25D, 0, .25D, .75D, .5D, .75D);
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face == Direction.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean sproinksOrSomethingIDontKnowItsLateAndImTired){
		addCollisionBoxToList(pos, mask, list, BB);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		IAdvancedRedstoneHandler handler;
		if(te != null && (handler = te.getCapability(Capabilities.ADVANCED_REDSTONE_CAPABILITY, null)) != null){
			return (int) handler.getOutput(true);
		}
		return 0;
	}
}
