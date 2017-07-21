package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.enums.MagicElements;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BeamCage extends Item{

	public BeamCage(){
		String name = "beam_cage";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
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
