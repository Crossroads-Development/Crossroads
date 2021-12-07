package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeamCage extends Item{

	public static final int CAPACITY = 2048;

	public BeamCage(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "beam_cage";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Nonnull
	public static BeamUnit getStored(ItemStack stack){
		CompoundTag nbt = stack.getTag();
		if(nbt == null){
			return BeamUnit.EMPTY;
		}
		BeamUnit stored = BeamUnit.readFromNBT("beam", nbt);
		return stored.getPower() == 0 ? BeamUnit.EMPTY : stored;
	}

	public static void storeBeam(ItemStack stack, @Nonnull BeamUnit toStore){
		CompoundTag nbt = stack.getTag();
		if(nbt == null){
			stack.setTag(new CompoundTag());
			nbt = stack.getTag();
		}
		if(toStore.getEnergy() > CAPACITY || toStore.getPotential() > CAPACITY || toStore.getStability() > CAPACITY || toStore.getVoid() > CAPACITY){
			toStore = new BeamUnit(Math.min(CAPACITY, toStore.getEnergy()), Math.min(CAPACITY, toStore.getPotential()), Math.min(CAPACITY, toStore.getStability()), Math.min(CAPACITY, toStore.getVoid()));
		}
		toStore.writeToNBT("beam", nbt);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			storeBeam(stack, new BeamUnit(CAPACITY, CAPACITY, CAPACITY, CAPACITY));
			items.add(stack);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced){
		BeamUnit stored = getStored(stack);
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cage.energy", stored.getEnergy(), CAPACITY));
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cage.potential", stored.getPotential(), CAPACITY));
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cage.stability", stored.getStability(), CAPACITY));
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cage.void", stored.getVoid(), CAPACITY));
	}
}
