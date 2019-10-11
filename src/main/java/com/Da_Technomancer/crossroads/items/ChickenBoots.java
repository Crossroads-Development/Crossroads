package com.Da_Technomancer.crossroads.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;

public class ChickenBoots extends ArmorItem{

	public ChickenBoots(){
		super(CRItems.BOBO, 1, EquipmentSlotType.FEET);
		setMaxStackSize(1);
		String name = "chicken_boots";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}
}
