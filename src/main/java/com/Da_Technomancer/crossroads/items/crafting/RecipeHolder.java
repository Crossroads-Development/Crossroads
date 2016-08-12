package com.Da_Technomancer.crossroads.items.crafting;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public final class RecipeHolder {
	
	/**For the string, use the item registry name (WITH path, ex. "crossroads:dustCopper") for a specific item, or oreDict name (WITHOUT path, ex. "dustCopper")
	 * 
	 */
	public static final HashMap<String, ItemStack[]> grindRecipes = new HashMap<String, ItemStack[]>();
	
	/**Block is input, blockstate is the created block, Double1 is heat created, Double2 is the limit.
	 * 
	 */
	public static final HashMap<Block, Triple<IBlockState, Double, Double>> envirHeatSource = new HashMap<Block, Triple<IBlockState, Double, Double>>();

	/**Fluid is input, Integer is the amount required, ItemStack is output, Double1 is maximum temperature, and Double2 is heat added on craft.
	 * 
	 */
	public static final HashMap<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> fluidCoolingRecipes = new HashMap<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>>();
	
	/**A list of all recipes, Item Array are the ingredients, and itemstack is output.
	 * A list for poisonous potato recipes and mashed potato recipes.
	 */
	protected static final ArrayList<Pair<CraftingStack[], ItemStack>> mashedBoboRecipes = new ArrayList<Pair<CraftingStack[], ItemStack>>();
	protected static final ArrayList<Pair<CraftingStack[], ItemStack>> poisonBoboRecipes = new ArrayList<Pair<CraftingStack[], ItemStack>>();
	
	
	//TODO test
	public static ItemStack recipeMatch(boolean poisonous, ArrayList<EntityItem> itemEnt){
		if(itemEnt == null){
			return null;
		}
		
		final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		for(EntityItem it : itemEnt){
			if(it.getEntityItem() == null || it.getEntityItem().stackSize != 1){
				return null;
			}
			items.add(it.getEntityItem());
		}
		
		if(poisonous){
			for(Pair<CraftingStack[], ItemStack> craft : poisonBoboRecipes){
				if(items.size() == craft.getLeft().length){
					ArrayList<ItemStack> itemCop = new ArrayList<ItemStack>();
					itemCop.addAll(items);
					
					for(CraftingStack cStack : craft.getLeft()){
						for(ItemStack stack : items){
							if(itemCop.contains(stack) && cStack.softMatch(stack)){
								itemCop.remove(stack);
							}
						}
					}
					
					if(itemCop.size() == 0){
						return craft.getRight();
					}
				}
			}
		}else{
			for(Pair<CraftingStack[], ItemStack> craft : mashedBoboRecipes){
				if(items.size() == craft.getLeft().length){
					ArrayList<ItemStack> itemCop = new ArrayList<ItemStack>();
					itemCop.addAll(items);
					
					for(CraftingStack cStack : craft.getLeft()){
						for(ItemStack stack : items){
							if(itemCop.contains(stack) && cStack.softMatch(stack)){
								itemCop.remove(stack);
							}
						}
					}
					
					if(itemCop.size() == 0){
						return craft.getRight();
					}
				}
			}
		}
		
		return null;
	}
}
