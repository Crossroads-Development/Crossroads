package com.Da_Technomancer.crossroads.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.Arrays;

public class CraftTweakerIntegration{

	public static void init(){
		CraftTweakerAPI.registerClass(GrindstoneHandler.class);
		CraftTweakerAPI.registerClass(FluidCoolingChamberHandler.class);
		CraftTweakerAPI.registerClass(ArcaneExtractorHandler.class);
		CraftTweakerAPI.registerClass(HeatingCrucibleHandler.class);
		CraftTweakerAPI.registerClass(FusionBeamHandler.class);
		CraftTweakerAPI.registerClass(EnvHeatSourceHandler.class);
		CraftTweakerAPI.registerClass(DetailedCrafterHandler.class);

		if(Loader.isModLoaded("contenttweaker")){
			CraftTweakerAPI.registerClass(AdvFusionBeamHandler.class);
			CraftTweakerAPI.registerClass(AdvEnvHeatSourceHandler.class);
		}
	}

	private static final ItemStack[] EMPTY = new ItemStack[0];

	protected static ItemStack[] toItemStack(IIngredient... ingredients){
		if(ingredients == null || ingredients.length == 0){
			return EMPTY;
		}

		ArrayList<IIngredient> ingred = new ArrayList<IIngredient>(Arrays.asList(ingredients));
		ingred.remove(null);
		ingred.remove(null);

		if(ingred.size() == 0){
			return EMPTY;
		}

		ItemStack[] itemStacks = new ItemStack[ingred.size()];
		for(int i = 0; i < ingred.size(); i++){
			itemStacks[i] = CraftTweakerMC.getItemStack(ingred.get(i));
		}
		return itemStacks;
	}
}
