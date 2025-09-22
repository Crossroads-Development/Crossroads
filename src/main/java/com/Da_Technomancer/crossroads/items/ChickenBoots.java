package com.Da_Technomancer.crossroads.items;

import net.minecraft.world.item.ArmorItem;

public class ChickenBoots extends ArmorItem{

	protected ChickenBoots(){
		super(CRItems.BOBO_ARMOR_MATERIAL, Type.BOOTS, new Properties().stacksTo(1).rarity(CRItems.BOBO_RARITY));
		String name = "chicken_boots";
		CRItems.queueForRegister(name, this);
	}
}
