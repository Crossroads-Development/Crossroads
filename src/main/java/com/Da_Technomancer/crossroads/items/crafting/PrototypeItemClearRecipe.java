package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PrototypeItemClearRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe{

	private final Item toClear;
	private final String nbtPath;

	protected PrototypeItemClearRecipe(Item toClear, String nbtPath){
		this.toClear = toClear;
		this.nbtPath = nbtPath;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn){
		boolean found = false;
		for(int x = 0; x < inv.getWidth(); x++){
			for(int y = 0; y < inv.getHeight(); y++){
				if(!found){
					ItemStack slot = inv.getStackInRowAndColumn(x, y);
					if(!slot.isEmpty()){
						if(slot.getItem() == toClear && slot.hasTag() && slot.getTag().contains(nbtPath)){
							found = true;
						}else{
							return false;
						}
					}
				}else if(!inv.getStackInRowAndColumn(x, y).isEmpty()){
					return false;
				}
			}
		}
		return found;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv){
		//Assumes matches would return true.
		for(int x = 0; x < inv.getWidth(); x++){
			for(int y = 0; y < inv.getHeight(); y++){
				ItemStack slot = inv.getStackInRowAndColumn(x, y);
				if(!slot.isEmpty()){
					slot = slot.copy();
					slot.getTag().remove(nbtPath);
					return slot;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return new ItemStack(toClear, 1);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv){
		NonNullList<ItemStack> outList = NonNullList.withSize(9, ItemStack.EMPTY);
		
		//Assumes matches would return true.
		for(int x = 0; x < inv.getWidth(); x++){
			for(int y = 0; y < inv.getHeight(); y++){
				ItemStack slot = inv.getStackInRowAndColumn(x, y);
				if(slot.getItem() == toClear){
					ItemStack outStack = new ItemStack(CrossroadsBlocks.prototype, 1);
					outStack.put(slot.getTag().getCompound(nbtPath));
					outList.set((y * 3) + x, outStack);
					return outList;
				}
			}
		}
		return outList;
	}

	@Override
	public boolean canFit(int width, int height){
		return width * height >= 1;
	}
}
