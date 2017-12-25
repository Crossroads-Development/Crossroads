package com.Da_Technomancer.crossroads.integration.crafttweaker;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.items.crafting.CraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.OreDictCraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Integration for the Heating Crucible. All recipes active at 1000°C. */
@ZenClass("mods.crossroads.HeatingCrucible")
public class HeatingCrucibleHandler{

	/**
	 * Adds a recipe from an ItemStack to a FluidStack to be crafted in a Heating Crucible. If a recipe with the same input already exists, this does not remove the other recipe. Call removeRecipe first in that case. 
	 * @param input The ItemStack input. Stacksize is ignored. 
	 * @param out The FluidStack created. 
	 * @param texture The texture to be displayed in the crucible while in solid form. In format modid:blocks/texture_name, ex: minecraft:blocks/cobblestone, crossroads:blocks/ore_native_copper. 
	 */
	@ZenMethod
	public static void addRecipe(IItemStack input, ILiquidStack out, String texture){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		CraftTweakerAPI.apply(new Add(new CraftingStack(in.getItem(), 1, in.getMetadata()), CraftTweakerMC.getLiquidStack(out), texture));
	}
	
	/**
	 * Adds a recipe from an OreDict to a FluidStack to be crafted in a Heating Crucible. If a recipe with the same input already exists, this does not remove the other recipe. Call removeRecipe first in that case. 
	 * @param input The OreDict input. Stacksize is ignored. 
	 * @param out The FluidStack created. 
	 * @param texture The texture to be displayed in the crucible while in solid form. In format modid:blocks/texture_name, ex: minecraft:blocks/cobblestone, crossroads:blocks/ore_native_copper. 
	 */
	@ZenMethod
	public static void addRecipe(IOreDictEntry input, ILiquidStack out, String texture){
		CraftTweakerAPI.apply(new Add(new OreDictCraftingStack(input.getName(), 1), CraftTweakerMC.getLiquidStack(out), texture));
	}
	
	/**
	 * This method removes a recipe from an ItemStack to a FluidStack in a Heating Crucible. 
	 * @param input The input itemstack (stacksize is ignored)
	 */
	@ZenMethod
	public static void removeRecipe(IItemStack input){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		CraftTweakerAPI.apply(new Remove(new CraftingStack(in.getItem(), 1, in.getMetadata())));
	}
	
	/**
	 * This method removes a recipe from an OreDict to a FluidStack in a Heating Crucible. 
	 * @param input The input oredict (stacksize is ignored)
	 */
	@ZenMethod
	public static void removeRecipe(IOreDictEntry input){
		CraftTweakerAPI.apply(new Remove(new OreDictCraftingStack(input.getName(), 1)));
	}
	
	private static class Add implements IAction{

		private final ICraftingStack<ItemStack> in;
		private final FluidStack out;
		private final String text;
		
		private Add(ICraftingStack<ItemStack> input, FluidStack out, String text){
			this.in = input;
			this.out = out;
			this.text = text;
		}
		
		@Override
		public void apply(){
			RecipeHolder.heatingCrucibleRecipes.add(Triple.of(in, out, text));
		}

		@Override
		public String describe(){
			return "Adding Heating Crucible recipe for " + in.toString();
		}	
	}
	
	private static class Remove implements IAction{

		private final ICraftingStack<ItemStack> input;
		
		private Remove(ICraftingStack<ItemStack> input){
			this.input = input;
		}
		
		@Override
		public void apply(){
			for(int i = 0; i < RecipeHolder.heatingCrucibleRecipes.size(); i++){
				if(RecipeHolder.heatingCrucibleRecipes.get(i).getLeft().equals(input)){
					RecipeHolder.heatingCrucibleRecipes.remove(i);
					return;
				}
			}
		}

		@Override
		public String describe(){
			return "Removing Heating Crucible recipe for " + input.toString();
		}
	}
}
