package com.Da_Technomancer.crossroads.items.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.integration.JEI.FluidCoolingRecipe;
import com.Da_Technomancer.crossroads.integration.JEI.GrindstoneRecipe;
import com.Da_Technomancer.crossroads.integration.JEI.HeatingCrucibleRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public final class RecipeHolder{

	/**
	 * For the string, use the item registry name (WITH path, ex.
	 * "crossroads:dustCopper") for a specific item, or oreDict name (WITHOUT
	 * path, ex. "dustCopper")
	 * 
	 */
	public static final HashMap<String, ItemStack[]> grindRecipes = new HashMap<String, ItemStack[]>();

	/**
	 * Block is input, blockstate is the created block, Double1 is heat created,
	 * Double2 is the limit.
	 * 
	 */
	public static final HashMap<Block, Triple<IBlockState, Double, Double>> envirHeatSource = new HashMap<Block, Triple<IBlockState, Double, Double>>();

	/**
	 * Fluid is input, Integer is the amount required, ItemStack is output,
	 * Double1 is maximum temperature, and Double2 is heat added on craft.
	 * 
	 */
	public static final HashMap<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> fluidCoolingRecipes = new HashMap<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>>();

	/**
	 * A list of all recipes, Item Array are the ingredients, and itemstack is
	 * output. A list for poisonous potato recipes and mashed potato recipes.
	 */
	protected static final ArrayList<Pair<CraftingStack[], ItemStack>> mashedBoboRecipes = new ArrayList<Pair<CraftingStack[], ItemStack>>();
	protected static final ArrayList<Pair<CraftingStack[], ItemStack>> poisonBoboRecipes = new ArrayList<Pair<CraftingStack[], ItemStack>>();

	/**
	 * Item is input, magic unit is the magic extracted. For the Arcane Extractor
	 */
	public static final HashMap<Item, MagicUnit> magExtractRecipes = new HashMap<Item, MagicUnit>();
	
	public static final ArrayList<Object> JEIWrappers = new ArrayList<Object>();

	/*
	 * Converts the versions of the recipes used internally into fake recipes
	 * for JEI. Not called unless JEI is installed.
	 */
	public static void rebind(){
		for(Entry<String, ItemStack[]> rec : grindRecipes.entrySet()){
			JEIWrappers.add(new GrindstoneRecipe(rec));
		}
		for(Entry<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> rec : fluidCoolingRecipes.entrySet()){
			JEIWrappers.add(new FluidCoolingRecipe(rec));
		}
		JEIWrappers.add(new HeatingCrucibleRecipe(true));
		JEIWrappers.add(new HeatingCrucibleRecipe(false));
	}

	public static ItemStack recipeMatch(boolean poisonous, ArrayList<EntityItem> itemEnt){
		if(itemEnt == null){
			return null;
		}

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();

		for(EntityItem it : itemEnt){
			if(it.getEntityItem() == null || it.getEntityItem().stackSize != 1){
				return null;
			}
			items.add(it.getEntityItem());
		}

		if(items.size() != 3){
			return null;
		}

		if(poisonous){
			for(Pair<CraftingStack[], ItemStack> craft : poisonBoboRecipes){
				ArrayList<ItemStack> itemCop = new ArrayList<ItemStack>();
				itemCop.addAll(items);

				for(CraftingStack cStack : craft.getLeft()){
					for(ItemStack stack : items){
						if(itemCop.contains(stack) && cStack.softMatch(stack)){
							itemCop.remove(stack);
							break;
						}
					}

					if(itemCop.size() == 0){
						return craft.getRight();
					}
				}
			}
		}else{
			for(Pair<CraftingStack[], ItemStack> craft : mashedBoboRecipes){
				ArrayList<ItemStack> itemCop = new ArrayList<ItemStack>();
				itemCop.addAll(items);

				for(CraftingStack cStack : craft.getLeft()){
					for(ItemStack stack : items){
						if(itemCop.contains(stack) && cStack.softMatch(stack)){
							itemCop.remove(stack);
							break;
						}
					}
				}

				if(itemCop.size() == 0){
					return craft.getRight();
				}
			}
		}

		return null;
	}
}
