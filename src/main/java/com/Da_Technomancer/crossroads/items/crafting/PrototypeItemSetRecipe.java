package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PrototypeItemSetRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe{

	private final Item toSet;
	private final String nbtPath;

	protected PrototypeItemSetRecipe(Item toSet, String nbtPath){
		this.toSet = toSet;
		this.nbtPath = nbtPath;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn){
		boolean found = false;
		boolean protFound = false;
		for(int x = 0; x < inv.getWidth(); x++){
			for(int y = 0; y < inv.getHeight(); y++){
				ItemStack slot = inv.getStackInRowAndColumn(x, y);
				if(!found){
					if(!slot.isEmpty() && slot.getItem() != Item.getItemFromBlock(ModBlocks.prototype)){
						if(slot.getItem() == toSet && (!slot.hasTagCompound() || !slot.getTagCompound().hasKey(nbtPath))){
							found = true;
							continue;
						}else{
							return false;
						}
					}
				}
				if(!protFound){
					if(!slot.isEmpty()){
						if(slot.getItem() == Item.getItemFromBlock(ModBlocks.prototype)){
							protFound = true;
							continue;
						}else{
							return false;
						}
					}
				}
				if(!inv.getStackInRowAndColumn(x, y).isEmpty()){
					return false;
				}
			}
		}
		return protFound && found;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv){
		ItemStack prot = ItemStack.EMPTY;
		ItemStack item = ItemStack.EMPTY;
		//Assumes matches would return true.
		for(int x = 0; x < inv.getWidth(); x++){
			for(int y = 0; y < inv.getHeight(); y++){
				ItemStack slot = inv.getStackInRowAndColumn(x, y);
				if(prot == ItemStack.EMPTY || item == ItemStack.EMPTY){
					if(!slot.isEmpty()){
						if(slot.getItem() == toSet){
							item = slot;
							if(!prot.isEmpty()){
								break;
							}
						}else if(slot.getItem() == Item.getItemFromBlock(ModBlocks.prototype)){
							prot = slot;
							if(!item.isEmpty()){
								break;
							}
						}else{
							return ItemStack.EMPTY;
						}
					}
				}
			}
		}
		
		ItemStack out = item.copy();
		if(!out.hasTagCompound()){
			out.setTagCompound(new NBTTagCompound());
		}
		if(prot.hasTagCompound()){
			//If the prototype doesn't have a tag compound, it's invalid and will be destroyed on craft. 
			out.getTagCompound().setTag(nbtPath, prot.getTagCompound());
		}
		return out;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return new ItemStack(toSet);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
		return NonNullList.withSize(9, ItemStack.EMPTY);
	}

	@Override
	public boolean canFit(int width, int height){
		return width * height >= 2;
	}
}
