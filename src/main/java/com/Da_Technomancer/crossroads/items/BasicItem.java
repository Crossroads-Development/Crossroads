package com.Da_Technomancer.crossroads.items;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;

import net.minecraft.item.Item;

/**
 * @deprecated That addition by vanilla of item properties and the removal of oredict in favor of tags makes this class redundant
 */
@Deprecated
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
		super(tab ? CRItems.itemProp : new Item.Properties());
		setRegistryName(name);
		CRItems.toRegister.add(this);
		if(oreDict != null){
			ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {oreDict}));
		}
	}
}
