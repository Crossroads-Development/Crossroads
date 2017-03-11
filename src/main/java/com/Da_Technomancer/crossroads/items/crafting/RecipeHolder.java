package com.Da_Technomancer.crossroads.items.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.integration.JEI.FluidCoolingRecipe;
import com.Da_Technomancer.crossroads.integration.JEI.GrindstoneRecipe;
import com.Da_Technomancer.crossroads.integration.JEI.HeatExchangerRecipe;
import com.Da_Technomancer.crossroads.integration.JEI.HeatingCrucibleRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public final class RecipeHolder{

	/**
	 * CraftingStack is input, the array is the outputs. HAVE NO MORE THAN 3 ITEMSTACKS IN THE ARRAY.
	 * 
	 */
	public static final HashMap<ICraftingStack, ItemStack[]> grindRecipes = new HashMap<ICraftingStack, ItemStack[]>();

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
	protected static final ArrayList<Pair<ICraftingStack[], ItemStack>> poisonBoboRecipes = new ArrayList<Pair<ICraftingStack[], ItemStack>>();

	/**
	 * Item is input, magic unit is the magic extracted. For the Arcane Extractor
	 */
	public static final HashMap<Item, MagicUnit> magExtractRecipes = new HashMap<Item, MagicUnit>();
	
	public static final ArrayList<Object> JEIWrappers = new ArrayList<Object>();

	/**
	 * Converts the versions of the recipes used internally into fake recipes
	 * for JEI. Not called unless JEI is installed.
	 */
	public static void rebind(){
		for(Entry<ICraftingStack, ItemStack[]> rec : grindRecipes.entrySet()){
			JEIWrappers.add(new GrindstoneRecipe(rec));
		}
		for(Entry<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> rec : fluidCoolingRecipes.entrySet()){
			JEIWrappers.add(new FluidCoolingRecipe(rec));
		}
		for(Entry<Block, Triple<IBlockState, Double, Double>> rec : envirHeatSource.entrySet()){
			JEIWrappers.add(new HeatExchangerRecipe(rec));
		}
		JEIWrappers.add(new HeatingCrucibleRecipe(true));
		JEIWrappers.add(new HeatingCrucibleRecipe(false));
	}

	@Nonnull
	public static ItemStack recipeMatch(ArrayList<EntityItem> itemEnt){
		if(itemEnt == null){
			return ItemStack.EMPTY;
		}

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();

		for(EntityItem it : itemEnt){
			if(it.getEntityItem() == null || it.getEntityItem().getCount() != 1){
				return ItemStack.EMPTY;
			}
			items.add(it.getEntityItem());
		}

		if(items.size() != 3){
			return ItemStack.EMPTY;
		}

		for(Pair<ICraftingStack[], ItemStack> craft : poisonBoboRecipes){
			ArrayList<ItemStack> itemCop = new ArrayList<ItemStack>();
			itemCop.addAll(items);

			for(ICraftingStack cStack : craft.getLeft()){
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

		return ItemStack.EMPTY;
	}
}
