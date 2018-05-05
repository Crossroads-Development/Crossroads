package com.Da_Technomancer.crossroads.items.crafting;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.CustomTool;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CustomToolRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe{

	private final String toolClass;
	private final byte[][] pattern;
	private static final int STICK_ORE_ID = OreDictionary.getOreID("stickWood");

	protected CustomToolRecipe(String toolClass, byte[][] pattern){
		this.toolClass = toolClass;
		this.pattern = pattern;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				ItemStack slot = inv.getStackInRowAndColumn(j, i);
				switch(pattern[i][j]){
					case 0:
						if(!slot.isEmpty()){
							return false;
						}
						break;
					case 1:
//						if(slot.getItem() != ModItems.customMaterial){
//							return false;
//						}
						break;
					default:
						if(slot.isEmpty()){
							return false;
						}
						int[] ores = OreDictionary.getOreIDs(slot);
						boolean found = false;
						for(int ore : ores){
							if(ore == STICK_ORE_ID){
								found = true;
								break;
							}
						}
						if(!found){
							return false;
						}
						break;
				}
			}
		}

		return true;
	}

	@Override
	public boolean isDynamic(){
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv){
		//Assumes matches would return true.
		ArrayList<ItemStack> crystals = new ArrayList<ItemStack>();
		for(int i = 0; i < inv.getSizeInventory(); i++){
			ItemStack stack = inv.getStackInSlot(i);
//			if(stack.getItem() == ModItems.customMaterial){
//				crystals.add(stack);
//			}
		}

		return CustomTool.craftCustomTool(toolClass, crystals);
	}

	@Override
	public ItemStack getRecipeOutput(){
		return new ItemStack(CustomTool.TOOL_TYPES.get(toolClass), 1);
	}

	@Override
	public boolean canFit(int width, int height){
		return width >= 3 && height >= 3;
	}

	@Override
	public int getRecipeWidth(){
		return 3;
	}

	@Override
	public int getRecipeHeight(){
		return 3;
	}

	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> list = NonNullList.create();
		
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(pattern[i][j] == 0){
					list.add(Ingredient.EMPTY);
				}else if(pattern[i][j] == 1){
					//list.add(CraftingHelper.getIngredient(ModItems.customMaterial));
				}else{
					list.add(CraftingHelper.getIngredient("stickWood"));
				}
			}
		}
		
		return list;

	}
}
