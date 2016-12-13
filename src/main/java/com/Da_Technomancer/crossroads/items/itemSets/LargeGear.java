package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.List;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class LargeGear extends Item{

	private GearTypes type;

	public LargeGear(GearTypes typeIn){
		setUnlocalizedName("largeGear" + typeIn.toString());
		setRegistryName("largeGear" + typeIn.toString());
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
		type = typeIn;
		ModItems.itemAddQue(this);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this, 1), "###", "#$#", "###", '#', GearFactory.basicGears.get(typeIn), '$', "block" + typeIn.toString()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add("Mass: " + MiscOp.betterRound(4.5D * type.getDensity(), 2));
		tooltip.add("I: " + MiscOp.betterRound(4.5D * type.getDensity(), 2) * 1.125D);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		pos = pos.offset(side);

		for(BlockPos cPos : section(pos, side)){
			if(!worldIn.getBlockState(cPos).getBlock().isReplaceable(worldIn, cPos)){
				return EnumActionResult.PASS;
			}
		}

		if(!playerIn.capabilities.isCreativeMode && --playerIn.getHeldItem(hand).stackSize <= 0){
			playerIn.setHeldItem(hand, null);
		}

		for(BlockPos cPos : section(pos, side)){
			if(pos.equals(cPos)){
				worldIn.setBlockState(pos, ModBlocks.largeGearMaster.getDefaultState().withProperty(Properties.FACING, side.getOpposite()), 3);
				((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).initSetup(type);
			}else{
				worldIn.setBlockState(cPos, ModBlocks.largeGearSlave.getDefaultState().withProperty(Properties.FACING, side.getOpposite()), 3);
				((LargeGearSlaveTileEntity) worldIn.getTileEntity(cPos)).setInitial(pos);
			}
		}
		++CommonProxy.masterKey;

		return EnumActionResult.PASS;
	}

	private static BlockPos[] section(BlockPos pos, EnumFacing side){
		if(side == EnumFacing.UP || side == EnumFacing.DOWN){
			return new BlockPos[] {pos.offset(EnumFacing.NORTH, -1).offset(EnumFacing.EAST, -1), pos.offset(EnumFacing.NORTH, -1), pos.offset(EnumFacing.NORTH, -1).offset(EnumFacing.EAST, 1), pos.offset(EnumFacing.EAST, -1), pos, pos.offset(EnumFacing.EAST, 1), pos.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, -1), pos.offset(EnumFacing.NORTH, 1), pos.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 1)};
		}
		if(side == EnumFacing.EAST || side == EnumFacing.WEST){
			return new BlockPos[] {pos.offset(EnumFacing.NORTH, -1).offset(EnumFacing.UP, -1), pos.offset(EnumFacing.NORTH, -1), pos.offset(EnumFacing.NORTH, -1).offset(EnumFacing.UP, 1), pos.offset(EnumFacing.UP, -1), pos, pos.offset(EnumFacing.UP, 1), pos.offset(EnumFacing.NORTH, 1).offset(EnumFacing.UP, -1), pos.offset(EnumFacing.NORTH, 1), pos.offset(EnumFacing.NORTH, 1).offset(EnumFacing.UP, 1)};
		}
		return new BlockPos[] {pos.offset(EnumFacing.UP, -1).offset(EnumFacing.EAST, -1), pos.offset(EnumFacing.UP, -1), pos.offset(EnumFacing.UP, -1).offset(EnumFacing.EAST, 1), pos.offset(EnumFacing.EAST, -1), pos, pos.offset(EnumFacing.EAST, 1), pos.offset(EnumFacing.UP, 1).offset(EnumFacing.EAST, -1), pos.offset(EnumFacing.UP, 1), pos.offset(EnumFacing.UP, 1).offset(EnumFacing.EAST, 1)};
	}

}
