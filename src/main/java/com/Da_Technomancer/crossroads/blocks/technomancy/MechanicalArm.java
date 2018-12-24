package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MechanicalArm extends BlockContainer{
	
	public MechanicalArm(){
		super(Material.IRON);
		String name = "mechanical_arm";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new MechanicalArmTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Interacts with the world around it. Controlled by 3 rotary inputs and 2 redstone inputs");
		tooltip.add("Can move items, break/place blocks, use items, move mobs, launch mobs across the map, and more");
		tooltip.add("The rotary inputs control x/y/z position of the claw, the red redstone input triggers actions, and the (optional) yellow redstone input allows setting facings");

		//TODO
		tooltip.add("TEMP TOOLTIP UNTIL DOCS ARE DONE:");
		tooltip.add("Red Redstone: 0: Nothing; 1: Pickup Mob; 2: Pickup Block; 3: Pickup from Inventory; 4: Pickup 1 from Inventory; 5: Use Item; 6: Attack with Item; 7: Deposit in Inventory; 8: Drop Mob/Item; 9: Throw Item/Mob");
		tooltip.add("Yellow Redstone: 0: Down; 1: Up; 2: North; 3: South; 4: West; 5: East");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		 //Redstone controlled by north side.
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanicalArmTileEntity){
			((MechanicalArmTileEntity) te).setRedstone((int) Math.round(RedstoneUtil.getDirectPowerOnSide(worldIn, pos, EnumFacing.NORTH)));
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side){
		return side == EnumFacing.NORTH || side == EnumFacing.SOUTH;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanicalArmTileEntity){
			((MechanicalArmTileEntity) te).ridable.setDead();
		}
		
		super.breakBlock(worldIn, pos, state);
	}
}