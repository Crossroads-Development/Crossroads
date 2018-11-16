package com.Da_Technomancer.crossroads.items;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ChickenBoots extends ItemArmor{

	public ChickenBoots(){
		super(ModItems.BOBO, 1, EntityEquipmentSlot.FEET);
		setMaxStackSize(1);
		String name = "chicken_boots";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}
}
