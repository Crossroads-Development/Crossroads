package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Integration for the Arcane Extractor */
@ZenClass("mods.crossroads.ArcaneExtractor")
public class ArcaneExtractorHandler{

	/**
	 * This method adds a mapping between an item (metadata is ignored) and the created magic in an Arcane Extractor. If a mapping already exists for that item, the previous one is replaced. 
	 * There is no OreDict support for the Arcane Extractor. 
	 * @param input The input itemstack (metadata is ignored)
	 * @param energy Created energy
	 * @param potential Created potential
	 * @param stability Created stability
	 * @param voi Created void
	 */
	@ZenMethod
	public static void addRecipe(IItemStack input, int energy, int potential, int stability, int voi){
		CraftTweakerAPI.apply(new Add(CraftTweakerMC.getItemStack(input).getItem(), new MagicUnit(energy, potential, stability, voi)));
	}
	
	/**
	 * This method removes a mapping between an item (metadata is ignored) and the created magic in an Arcane Extractor. 
	 * @param input The input itemstack (metadata is ignored)
	 */
	@ZenMethod
	public static void removeRecipe(IItemStack input){
		CraftTweakerAPI.apply(new Remove(CraftTweakerMC.getItemStack(input).getItem()));
	}
	
	private static class Add implements IAction{

		private final Item input;
		private final MagicUnit created;
		
		private Add(Item input, MagicUnit created){
			this.input = input;
			this.created = created;
		}
		
		@Override
		public void apply(){
			RecipeHolder.magExtractRecipes.put(input, created);
		}

		@Override
		public String describe(){
			return "Adding Arcane Extractor recipe for " + input.getRegistryName();
		}	
	}
	
	private static class Remove implements IAction{

		private final Item input;
		
		private Remove(Item input){
			this.input = input;
		}
		
		@Override
		public void apply(){
			RecipeHolder.magExtractRecipes.remove(input);
		}

		@Override
		public String describe(){
			return "Removing Arcane Extractor recipe for " + input.getRegistryName();
		}
	}
}
