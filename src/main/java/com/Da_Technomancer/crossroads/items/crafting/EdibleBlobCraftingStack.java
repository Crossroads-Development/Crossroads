package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EdibleBlobCraftingStack implements ICraftingStack{

	private final int hunger;
	private final int saturation;
	private final int count;

	/**
	 * @param hunger The hunger value of the accepted blob.
	 * @param saturation The saturation value of the accepted blob.
	 * @param count
	 */
	public EdibleBlobCraftingStack(int hunger, int saturation, int count){
		this.hunger = hunger;
		this.saturation = saturation;
		this.count = count;
	}

	@Override
	public boolean match(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		if(stack.getItem() == ModItems.edibleBlob && stack.getCount() == count && stack.hasTagCompound() && stack.getTagCompound().getInteger("food") == hunger && stack.getTagCompound().getInteger("sat") == saturation){
			return true;
		}

		return false;
	}

	@Override
	public boolean softMatch(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		if(stack.getItem() == ModItems.edibleBlob && stack.hasTagCompound() && stack.getTagCompound().getInteger("food") == hunger && stack.getTagCompound().getInteger("sat") == saturation){
			return true;
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public List<ItemStack> getMatchingList(){
		ItemStack out = new ItemStack(ModItems.edibleBlob, count);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("food", hunger);
		nbt.setInteger("sat", saturation);
		out.setTagCompound(nbt);
		return ImmutableList.of(out);
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this){
			return true;
		}
		if(other instanceof EdibleBlobCraftingStack){
			EdibleBlobCraftingStack otherStack = (EdibleBlobCraftingStack) other;
			return hunger == otherStack.hunger && saturation == otherStack.saturation && count == otherStack.count;
		}
		
		return false;
	}
}
