package com.Da_Technomancer.crossroads.items;

import java.util.List;

import com.Da_Technomancer.crossroads.API.enums.MagicElements;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BeamCage extends Item{

	public BeamCage(){
		String name = "beam_cage";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			nbt = new NBTTagCompound();
		}
		tooltip.add("Energy stored: " + nbt.getInteger("stored_" + MagicElements.ENERGY.name()));
		tooltip.add("Potential stored: " + nbt.getInteger("stored_" + MagicElements.POTENTIAL.name()));
		tooltip.add("Stability stored: " + nbt.getInteger("stored_" + MagicElements.STABILITY.name()));
		tooltip.add("Void stored: " + nbt.getInteger("stored_" + MagicElements.VOID.name()));
	}
}
