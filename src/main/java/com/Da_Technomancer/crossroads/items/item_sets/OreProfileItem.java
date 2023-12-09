package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.templates.ICreativeTabPopulatingItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

public class OreProfileItem extends Item implements ICreativeTabPopulatingItem{

	protected static final String KEY = "material";

	public OreProfileItem(Item.Properties prop){
		super(prop);
	}

	public ItemStack withMaterial(CRMaterialLibrary.OreProfile mat, int count){
		if(mat == null){
			mat = CRMaterialLibrary.getDefaultProfile();
		}
		ItemStack out = new ItemStack(this, count);
		out.setTag(new CompoundTag());
		out.getTag().putString(KEY, mat.getId());
		return out;
	}

	public static CRMaterialLibrary.OreProfile getProfile(ItemStack stack){
		Item item = stack.getItem();
		if(item instanceof OreProfileItem){
			return ((OreProfileItem) item).getSelfProfile(stack);
		}
		return CRMaterialLibrary.getDefaultProfile();
	}

	protected CRMaterialLibrary.OreProfile getSelfProfile(ItemStack stack){
		String matKey;
		if(!stack.hasTag()){
			return CRMaterialLibrary.getDefaultProfile();
		}else{
			matKey = stack.getTag().getString(KEY);
		}
		return CRMaterialLibrary.findProfile(matKey);
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
	public Component getDescription(){
		//Incorrectly displays the default material for all variants- we don't have access to an itemstack/nbt to differentiate
		return getName(withMaterial(null, 1));
	}

	@Override
	public Component getName(ItemStack stack){
		CRMaterialLibrary.OreProfile mat = getProfile(stack);
		//Note that we use the super of getTranslationKey to prevent an infinite loop
		return Component.translatable(super.getDescriptionId(stack), mat == null ? "INVALID" : mat.getName());
	}

	@Override
	public ItemStack[] populateCreativeTab(){
		//Add every material variant of this item
		Collection<CRMaterialLibrary.OreProfile> profiles = CRMaterialLibrary.getProfiles();
		ItemStack[] stacks = new ItemStack[profiles.size()];
		int i = 0;
		for(CRMaterialLibrary.OreProfile mat : profiles){
			stacks[i++] = withMaterial(mat, 1);
		}
		return stacks;
	}
}
