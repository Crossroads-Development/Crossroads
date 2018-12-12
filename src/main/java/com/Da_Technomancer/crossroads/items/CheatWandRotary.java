package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class CheatWandRotary extends Item{

	public CheatWandRotary(){
		String name = "cheat_wand_rotary";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite())){
			if(playerIn.isSneaking()){
				te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).addEnergy(-1000, true, true);
			}else{
				te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).addEnergy(1000, true, true);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Creative Mode Only");
	}
}
