package com.Da_Technomancer.crossroads.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EdibleBlob extends ItemFood{

	public EdibleBlob(){
		super(0, 0, true);
		String name = "edibleBlob";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		//This is not in a creative tab due to creative giving a version that has no NBT
	}

	@Override
	public int getHealAmount(ItemStack stack){
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("food") : 0;
	}

	@Override
	public float getSaturationModifier(ItemStack stack){
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("sat") : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add(stack.hasTagCompound() ? "Food value: " + stack.getTagCompound().getInteger("food") : "ERROR");
		tooltip.add(stack.hasTagCompound() ? "Saturation value: " + stack.getTagCompound().getInteger("sat") : "ERROR");
	}
}
