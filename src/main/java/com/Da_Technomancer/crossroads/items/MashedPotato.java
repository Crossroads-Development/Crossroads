package com.Da_Technomancer.crossroads.items;

import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MashedPotato extends ItemFood{
	
	public MashedPotato(){
		super(5, 6, true);
		String name = "mashedPotato";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}

}
