package com.Da_Technomancer.crossroads.items.itemSets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class OreProfileItem extends Item{

	protected static final String KEY = "material";

	public OreProfileItem(Item.Properties prop){
		super(prop);
	}

	public ItemStack withMaterial(OreSetup.OreProfile mat, int count){
		if(mat == null){
			mat = OreSetup.getDefaultMaterial();
		}
		ItemStack out = new ItemStack(this, count);
		out.setTag(new CompoundNBT());
		out.getTag().putString(KEY, mat.getId());
		return out;
	}


	@Nullable
	public static OreSetup.OreProfile getProfile(ItemStack stack){
		String matKey;
		if(!stack.hasTag()){
			return GearFactory.getDefaultMaterial();
		}else{
			matKey = stack.getTag().getString(KEY);
		}
		return GearFactory.findMaterial(matKey);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack){
		OreSetup.OreProfile mat = getProfile(stack);
		return new TranslationTextComponent(getTranslationKey(stack), mat == null ? "INVALID" : mat.getName());
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(isInGroup(group)){
			//Add every material variant of this item
			for(OreSetup.OreProfile mat : OreSetup.getMaterials()){
				items.add(withMaterial(mat, 1));
			}
		}
	}
}
