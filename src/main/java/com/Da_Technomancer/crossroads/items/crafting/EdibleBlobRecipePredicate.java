package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class EdibleBlobRecipePredicate implements RecipePredicate<ItemStack>{

	private final int hunger;
	private final int saturation;

	/**
	 * @param hunger The hunger value of the accepted blob.
	 * @param saturation The saturation value of the accepted blob.
	 * @param count
	 */
	public EdibleBlobRecipePredicate(int hunger, int saturation){
		this.hunger = hunger;
		this.saturation = saturation;
	}

	@Override
	public boolean test(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		if(stack.getItem() == CrossroadsItems.edibleBlob && stack.hasTag() && stack.getTag().getInt("food") == hunger && stack.getTag().getInt("sat") == saturation){
			return true;
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingList(){
		ItemStack out = new ItemStack(CrossroadsItems.edibleBlob, 1);
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("food", hunger);
		nbt.putInt("sat", saturation);
		out.put(nbt);
		return ImmutableList.of(out);
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this){
			return true;
		}
		if(other instanceof EdibleBlobRecipePredicate){
			EdibleBlobRecipePredicate otherStack = (EdibleBlobRecipePredicate) other;
			return hunger == otherStack.hunger && saturation == otherStack.saturation;
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return (hunger << 4) + saturation;
	}
}
