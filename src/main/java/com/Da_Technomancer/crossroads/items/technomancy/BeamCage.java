package com.Da_Technomancer.crossroads.items.technomancy;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.items.ModItems;

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
		setTranslationKey(name);
		setRegistryName(name);
		maxStackSize = 1;
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	public static BeamUnit getStored(ItemStack stack){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return null;
		}
		BeamUnit stored = new BeamUnit(nbt.getInteger("stored_" + EnumBeamAlignments.ENERGY.name().toLowerCase()), nbt.getInteger("stored_" + EnumBeamAlignments.POTENTIAL.name().toLowerCase()), nbt.getInteger("stored_" + EnumBeamAlignments.STABILITY.name().toLowerCase()), nbt.getInteger("stored_" + EnumBeamAlignments.VOID.name().toLowerCase()));
		return stored.getPower() == 0 ? null : stored;
	}

	public static void storeBeam(ItemStack stack, @Nullable BeamUnit toStore){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			stack.setTagCompound(new NBTTagCompound());
			nbt = stack.getTagCompound();
		}
		nbt.setInteger("stored_" + EnumBeamAlignments.ENERGY.name().toLowerCase(), toStore == null ? 0 : toStore.getEnergy());
		nbt.setInteger("stored_" + EnumBeamAlignments.POTENTIAL.name().toLowerCase(), toStore == null ? 0 : toStore.getPotential());
		nbt.setInteger("stored_" + EnumBeamAlignments.STABILITY.name().toLowerCase(), toStore == null ? 0 : toStore.getStability());
		nbt.setInteger("stored_" + EnumBeamAlignments.VOID.name().toLowerCase(), toStore == null ? 0 : toStore.getVoid());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		BeamUnit stored = getStored(stack);
		tooltip.add("Energy stored: " + (stored == null ? 0 : stored.getEnergy()));
		tooltip.add("Potential stored: " + (stored == null ? 0 : stored.getPotential()));
		tooltip.add("Stability stored: " + (stored == null ? 0 : stored.getStability()));
		tooltip.add("Void stored: " + (stored == null ? 0 : stored.getVoid()));
	}
}
