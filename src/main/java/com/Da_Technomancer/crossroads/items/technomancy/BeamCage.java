package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeamCage extends Item{

	public static final int CAPACITY = 1024;

	public BeamCage(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "beam_cage";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Nonnull
	public static BeamUnit getStored(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		if(nbt == null){
			return BeamUnit.EMPTY;
		}
		BeamUnit stored = BeamUnit.readFromNBT("beam", nbt);
		return stored.getPower() == 0 ? BeamUnit.EMPTY : stored;
	}

	public static void storeBeam(ItemStack stack, @Nonnull BeamUnit toStore){
		CompoundNBT nbt = stack.getTag();
		if(nbt == null){
			stack.setTag(new CompoundNBT());
			nbt = stack.getTag();
		}
		if(toStore.getEnergy() > CAPACITY || toStore.getPotential() > CAPACITY || toStore.getStability() > CAPACITY || toStore.getVoid() > CAPACITY){
			toStore = new BeamUnit(Math.min(CAPACITY, toStore.getEnergy()), Math.min(CAPACITY, toStore.getPotential()), Math.min(CAPACITY, toStore.getStability()), Math.min(CAPACITY, toStore.getVoid()));
		}
		toStore.writeToNBT("beam", nbt);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(isInGroup(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			storeBeam(stack, new BeamUnit(CAPACITY, CAPACITY, CAPACITY, CAPACITY));
			items.add(stack);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		BeamUnit stored = getStored(stack);
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cage.energy", stored.getEnergy(), CAPACITY));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cage.potential", stored.getPotential(), CAPACITY));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cage.stability", stored.getStability(), CAPACITY));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cage.void", stored.getVoid(), CAPACITY));
	}
}
