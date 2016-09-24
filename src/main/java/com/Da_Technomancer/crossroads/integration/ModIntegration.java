package com.Da_Technomancer.crossroads.integration;

import com.Da_Technomancer.crossroads.integration.minetweaker.MineTweakerIntegration;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by tommyTT on 18.09.2016.
 */
public class ModIntegration {

	public static boolean isJEIAvailable = false;

	public static void init() {
		if(Loader.isModLoaded("JEI")){
			RecipeHolder.rebind();
			isJEIAvailable = true;
		}

		if (Loader.isModLoaded("MineTweaker3")) {
			MineTweakerIntegration.init();
		}
	}
}
