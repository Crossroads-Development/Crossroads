package com.Da_Technomancer.crossroads.items;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;

import net.minecraft.item.Item;

public class BasicItem extends Item{

	/*
	 * This class is capable of generating an item with no special properties.
	 * Either input a name for the item, or specify a name and an oreDict for
	 * the item.
	 */

	public BasicItem(String name){
		this(name, null);
	}

	public BasicItem(String name, String oreDict){
		this(name, oreDict, true);
	}

	public BasicItem(String name, String oreDict, boolean tab){
		setTranslationKey(name);
		setRegistryName(name);
		if(tab){
			setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		}
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
		if(oreDict != null){
			ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {oreDict}));
		}
	}
}
