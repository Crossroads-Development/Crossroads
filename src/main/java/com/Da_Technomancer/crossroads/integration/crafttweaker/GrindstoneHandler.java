package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.items.crafting.CraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.OreDictCraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Integration for the Grindstone. */
@ZenClass("mods.crossroads.Grindstone")
public class GrindstoneHandler{

	/** Add a new recipe.
	 *
	 * @param input
	 *            input item stack
	 * @param output1
	 *            first output
	 * @param output2
	 *            second output, optional
	 * @param output3
	 *            third output, optional */
	@ZenMethod
	public static void addRecipe(IItemStack input, IIngredient output1, @Optional IIngredient output2, @Optional IIngredient output3){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		if(in.isEmpty()){
			return;
		}
		CraftTweakerAPI.apply(new Add(new CraftingStack(in.getItem(), in.getCount(), in.getMetadata()), CraftTweakerIntegration.toItemStack(output1, output2, output3)));
	}

	/** Add a new ore dict recipe.
	 *
	 * @param input
	 *            the ore dictionary entry for the input
	 * @param output1
	 *            first output
	 * @param output2
	 *            second output, optional
	 * @param output3
	 *            third output, optional */
	@ZenMethod
	public static void addRecipe(IOreDictEntry input, IIngredient output1, @Optional IIngredient output2, @Optional IIngredient output3){
		String key = input.getName();
		if(key == null){
			return;
		}
		CraftTweakerAPI.apply(new Add(new OreDictCraftingStack(key, 1), CraftTweakerIntegration.toItemStack(output1, output2, output3)));
	}

	/** Adds a new grindstone recipe and updates JEI Integration.
	 *
	 * @param input
	 *            the key that represents the input item
	 * @param outputs
	 *            the results of the grindstone operation */
	private static ItemStack[] addGrindstoneRecipe(ICraftingStack<ItemStack> input, ItemStack[] outputs){
		return RecipeHolder.grindRecipes.put(input, outputs);
	}

	/** Remove the recipe for the grindstone and return the previously registered output stack for that key (if it exists).
	 * If the outputs are specified via the parameter, the recipe is only removed if the output stacks matches!
	 *
	 * @param input
	 *            key for the input of the grindstone recipe
	 * @param outputs
	 *            output stacks of the recipe, can be null if it should be removed regardless of what output was registered
	 * @param overwritten
	 *            the previously registered output for the input, that will be added again
	 * @return the output stack that was registered for the input key */
	private static ItemStack[] replaceGrindstoneRecipe(ICraftingStack<ItemStack> input, ItemStack[] outputs, ItemStack[] overwritten){
		if(outputs != null){
			// remove only if the exact output is registered
			if(RecipeHolder.grindRecipes.remove(input, outputs)){
				if(overwritten != null){
					addGrindstoneRecipe(input, overwritten);
				}
				return outputs;
			}else{
				return null;
			}
		}else{
			// remove any recipe registered for that entry
			ItemStack[] removedStack = RecipeHolder.grindRecipes.remove(input);
			if(overwritten != null){
				addGrindstoneRecipe(input, overwritten);
			}
			return removedStack;
		}
	}

	/** Operation to add a new recipe. */
	private static class Add implements IAction{
		private final ICraftingStack<ItemStack> input;
		private final ItemStack[] outputs;

		private Add(ICraftingStack<ItemStack> input, ItemStack[] outputs){
			this.input = input;
			this.outputs = outputs;
		}

		@Override
		public void apply(){
			addGrindstoneRecipe(input, outputs);
		}

		@Override
		public String describe(){
			return "Adding Grindstone recipe for " + input;
		}
	}

	/** Remove a grindstone recipe for the input item stack.
	 *
	 * @param input the input for the recipe */
	@ZenMethod
	public static void removeRecipe(IItemStack input){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		if(in.isEmpty()){
			return;
		}

		CraftTweakerAPI.apply(new Remove(new CraftingStack(in.getItem(), in.getCount(), in.getMetadata())));
	}

	/** Remove a grindstone recipe for the ore dictionary entry of the input item stack.
	 *
	 * @param input */
	@ZenMethod
	public static void removeRecipe(IOreDictEntry input){
		String key = input.getName();
		if(key == null)
			return;

		CraftTweakerAPI.apply(new Remove(new OreDictCraftingStack(key, 1)));
	}

	/** Operation to remove a grindstone recipe. */
	private static class Remove implements IAction{
		ICraftingStack<ItemStack> input;

		Remove(ICraftingStack<ItemStack> input){
			this.input = input;
		}

		@Override
		public void apply(){
			replaceGrindstoneRecipe(input, null, null);
		}

		@Override
		public String describe(){
			return "Removing grindstone recipe for " + input;
		}
	}
}
