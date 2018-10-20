package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.crafting.ItemRecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
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

/** Integration for the Millstone. */
@ZenClass("mods.crossroads.Millstone")
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
		CraftTweakerAPI.apply(new Add(new ItemRecipePredicate(in.getItem(), in.getMetadata()), CraftTweakerIntegration.toItemStack(output1, output2, output3)));
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
		CraftTweakerAPI.apply(new Add(new OreDictCraftingStack(key), CraftTweakerIntegration.toItemStack(output1, output2, output3)));
	}

	/** Operation to add a new recipe. */
	private static class Add implements IAction{
		private final RecipePredicate<ItemStack> input;
		private final ItemStack[] outputs;

		private Add(RecipePredicate<ItemStack> input, ItemStack[] outputs){
			this.input = input;
			this.outputs = outputs;
		}

		@Override
		public void apply(){
			RecipeHolder.grindRecipes.put(input, outputs);
		}

		@Override
		public String describe(){
			return "Adding Millstone recipe for " + input;
		}
	}

	/** Remove a millstone recipe for the input item stack.
	 *
	 * @param input the input for the recipe */
	@ZenMethod
	public static void removeRecipe(IItemStack input){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		if(in.isEmpty()){
			return;
		}

		CraftTweakerAPI.apply(new Remove(new ItemRecipePredicate(in.getItem(), in.getMetadata())));
	}

	/** Remove a millstone recipe for the ore dictionary entry of the input item stack.
	 *
	 * @param input the input for the recipe
	 */
	@ZenMethod
	public static void removeRecipe(IOreDictEntry input){
		String key = input.getName();
		if(key == null)
			return;

		CraftTweakerAPI.apply(new Remove(new OreDictCraftingStack(key)));
	}

	/** Operation to remove a millstone recipe. */
	private static class Remove implements IAction{
		RecipePredicate<ItemStack> input;

		Remove(RecipePredicate<ItemStack> input){
			this.input = input;
		}

		@Override
		public void apply(){
			Main.logger.info("input: " + input + ": " + RecipeHolder.grindRecipes.remove(input));
		}

		@Override
		public String describe(){
			return "Removing Millstone recipe for " + input;
		}
	}
}
