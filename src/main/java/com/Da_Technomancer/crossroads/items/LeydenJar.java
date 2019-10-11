package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
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
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}
	
	public static int getCharge(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		if(stack.getItem() == CRItems.leydenJar && nbt != null){
			return nbt.getInt("charge");
		}else{
			return 0;
		}
	}
	
	public static void setCharge(ItemStack stack, int chargeIn){
		if(stack.hasTag()){
			stack.getTag().putInt("charge", Math.min(chargeIn, MAX_CHARGE));
		}else{
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
			stack.put(nbt);
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Charge: " + getCharge(stack) + "/" + MAX_CHARGE + " FE");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubItems(ItemGroup tab, NonNullList<ItemStack> list){
		if(isInCreativeTab(tab)){
			list.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setCharge(stack, MAX_CHARGE);
			list.add(stack);
		}
	}
}
