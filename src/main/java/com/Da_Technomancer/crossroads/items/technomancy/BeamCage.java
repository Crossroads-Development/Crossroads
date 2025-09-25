package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.templates.ICreativeTabPopulatingItem;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.util.List;

public class BeamCage extends Item implements ICreativeTabPopulatingItem{

	public static final int CAPACITY = 2048;

	public BeamCage(){
		super(new Properties().stacksTo(1));
		String name = "beam_cage";
		CRItems.queueForRegister(name, this);
	}

	@Nonnull
	public static BeamUnit getStored(ItemStack stack){
		return stack.getOrDefault(CRItems.STORED_BEAM_DATA, BeamUnit.EMPTY);
	}

	public static void storeBeam(ItemStack stack, @Nonnull BeamUnit toStore){
		if(toStore.getEnergy() > CAPACITY || toStore.getPotential() > CAPACITY || toStore.getStability() > CAPACITY || toStore.getVoid() > CAPACITY){
			toStore = new BeamUnit(Math.min(CAPACITY, toStore.getEnergy()), Math.min(CAPACITY, toStore.getPotential()), Math.min(CAPACITY, toStore.getStability()), Math.min(CAPACITY, toStore.getVoid()));
		}
		stack.set(CRItems.STORED_BEAM_DATA, toStore);
	}

	@Override
	public ItemStack[] populateCreativeTab(){
		ItemStack chargedStack = new ItemStack(this);
		storeBeam(chargedStack, new BeamUnit(CAPACITY, CAPACITY, CAPACITY, CAPACITY));
		return new ItemStack[] {new ItemStack(this), chargedStack};
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		BeamUnit stored = getStored(stack);
		tooltip.add(Component.translatable("tt.crossroads.beam_cage.energy", stored.getEnergy(), CAPACITY));
		tooltip.add(Component.translatable("tt.crossroads.beam_cage.potential", stored.getPotential(), CAPACITY));
		tooltip.add(Component.translatable("tt.crossroads.beam_cage.stability", stored.getStability(), CAPACITY));
		tooltip.add(Component.translatable("tt.crossroads.beam_cage.void", stored.getVoid(), CAPACITY));
	}
}
