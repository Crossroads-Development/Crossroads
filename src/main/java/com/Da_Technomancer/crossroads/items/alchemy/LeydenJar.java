package com.Da_Technomancer.crossroads.items.alchemy;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LeydenJar extends Item{

	public static final int MAX_CHARGE = 100_000;
	
	public LeydenJar(){
		String name = "leyden_jar";
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}
	
	public static int getCharge(ItemStack stack){
		NBTTagCompound nbt = stack.getTagCompound();
		if(stack.getItem() == ModItems.leydenJar && nbt != null){
			return nbt.getInteger("charge");
		}else{
			return 0;
		}
	}
	
	public static void setCharge(ItemStack stack, int chargeIn){
		if(stack.hasTagCompound()){
			stack.getTagCompound().setInteger("charge", Math.min(chargeIn, MAX_CHARGE));
		}else{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("charge", Math.min(chargeIn, MAX_CHARGE));
			stack.setTagCompound(nbt);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Charge: " + getCharge(stack) + "/" + MAX_CHARGE + " FE");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
		if(isInCreativeTab(tab)){
			list.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setCharge(stack, MAX_CHARGE);
			list.add(stack);
		}
	}
}
