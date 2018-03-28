package com.Da_Technomancer.crossroads.integration;

import com.Da_Technomancer.crossroads.integration.JEI.ReagIngr;
import com.Da_Technomancer.crossroads.integration.crafttweaker.CraftTweakerIntegration;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraftforge.fml.common.Loader;

/** Created by tommyTT on 18.09.2016. */
public class ModIntegration{

	public static void preInit(){
		if(Loader.isModLoaded("crafttweaker")){
			CraftTweakerIntegration.init();
		}
		if(Loader.isModLoaded("jei")){
			ReagIngr.populate();
		}
	}

	public static void init(){
		if(Loader.isModLoaded("jei")){
			RecipeHolder.rebind();
		}
	}
}
