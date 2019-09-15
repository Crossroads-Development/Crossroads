package com.Da_Technomancer.crossroads.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;

public class ChickenBoots extends ArmorItem{

	public ChickenBoots(){
		super(CrossroadsItems.BOBO, 1, EquipmentSlotType.FEET);
		setMaxStackSize(1);
		String name = "chicken_boots";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}
}
