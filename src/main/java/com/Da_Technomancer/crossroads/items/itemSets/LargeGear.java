package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LargeGear extends Item{

	private final GearTypes type;
	public static final ModelResourceLocation LOCAT = new ModelResourceLocation(Main.MODID + ":gear_base", "inventory");


	public LargeGear(GearTypes typeIn){
		String name = "large_gear_" + typeIn.toString().toLowerCase();
		setUnlocalizedName(name);
		setRegistryName(name);
		type = typeIn;
		setCreativeTab(ModItems.tabGear);
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of(this, 0), LOCAT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Mass: " + MiscOp.betterRound(4.5D * type.getDensity(), 2));
		tooltip.add("I: " + MiscOp.betterRound(4.5D * type.getDensity(), 2) * 1.125D);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		pos = pos.offset(side);

		for(BlockPos cPos : section(pos, side)){
			if(!worldIn.getBlockState(cPos).getBlock().isReplaceable(worldIn, cPos)){
				return EnumActionResult.SUCCESS;
			}
		}

		if(!playerIn.capabilities.isCreativeMode){
			playerIn.getHeldItem(hand).shrink(1);
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

		return EnumActionResult.SUCCESS;
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
