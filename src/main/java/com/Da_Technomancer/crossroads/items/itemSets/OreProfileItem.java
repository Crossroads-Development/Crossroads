package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OreProfileItem extends Item{

	protected static final String KEY = "material";

	public OreProfileItem(Item.Properties prop){
		super(prop);
		CRItems.toRegister.add(this);
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

	public static OreSetup.OreProfile getProfile(ItemStack stack){
		Item item = stack.getItem();
		if(item instanceof OreProfileItem){
			return ((OreProfileItem) item).getSelfProfile(stack);
		}
		return OreSetup.getDefaultMaterial();
	}

	protected OreSetup.OreProfile getSelfProfile(ItemStack stack){
		String matKey;
		if(!stack.hasTag()){
			return OreSetup.getDefaultMaterial();
		}else{
			matKey = stack.getTag().getString(KEY);
		}
		return OreSetup.findMaterial(matKey);
	}

	@Override
	public String getDescriptionId(ItemStack stack){
//		return super.getTranslationKey(stack);
		//We 'cheat' here. Instead of returning the translation key, we return the translated text, w/ formatting applied.
		//This is because most things calling this method don't know to pass the material name as a formatter argument (and most things use getDisplayName instead)
		//This is mainly important for WAILA
		return getName(stack).getString();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ITextComponent getDescription(){
		//Incorrectly displays the default material for all variants- we don't have access to an itemstack/nbt to differentiate
		return getName(withMaterial(null, 1));
	}

	@Override
	public ITextComponent getName(ItemStack stack){
		OreSetup.OreProfile mat = getProfile(stack);
		//Note that we use the super of getTranslationKey to prevent an infinite loop
		return new TranslationTextComponent(super.getDescriptionId(stack), mat == null ? "INVALID" : mat.getName());
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			//Add every material variant of this item
			for(OreSetup.OreProfile mat : OreSetup.getMaterials()){
				items.add(withMaterial(mat, 1));
			}
		}
	}
}
