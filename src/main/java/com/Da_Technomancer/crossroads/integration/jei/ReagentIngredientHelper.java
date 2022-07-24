package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

public class ReagentIngredientHelper implements IIngredientHelper<ReagIngr>{

	@Override
	public ItemStack getCheatItemStack(ReagIngr ingredient){
		ItemStack toGive = new ItemStack(CRItems.phialCrystal, 1);
		ReagentMap reags = new ReagentMap();
		reags.addReagent(ingredient.getReag(), CRItems.phialCrystal.getCapacity(), 50);
		CRItems.phialCrystal.setReagents(toGive, reags);
		return toGive;
	}
	
	@Override
	public boolean isValidIngredient(ReagIngr ingredient){
		return ingredient.getReag() != null && ingredient.getParts() >= 0;
	}

	@Override
	public IIngredientType<ReagIngr> getIngredientType(){
		return ReagIngr.REAG;
	}

	@Override
	public String getDisplayName(ReagIngr ingredient){
		return ingredient.getReag().getName();
	}

	@Override
	public String getUniqueId(ReagIngr ingredient, UidContext context){
		return Crossroads.MODID + ":" + ingredient.getID();
	}

	@Override
	public Iterable<Integer> getColors(ReagIngr ingredient){
		HashSet<Integer> out = new HashSet<>();
		for(EnumMatterPhase p : EnumMatterPhase.values()){
			out.add(ingredient.getReag().getColor(p).getRGB());
		}
		return out;
	}

	@Override
	public ResourceLocation getResourceLocation(ReagIngr ingredient){
		return new ResourceLocation(Crossroads.MODID, ingredient.getReag().getID());
	}

	@Override
	public ReagIngr copyIngredient(ReagIngr ingredient){
		return ingredient;//ReagentIngredient is immutable
	}

	@Override
	public ReagIngr normalizeIngredient(ReagIngr ingredient){
		return ingredient.getParts() == 1 ? ingredient : new ReagIngr(ingredient.getReag(), 1);
	}

	@Override
	public String getErrorInfo(ReagIngr ingredient){
		if(ingredient == null){
			return "NULL ingredient";
		}
		if(ingredient.getReag() == null){
			return "ID: " + ingredient.getID() + "; Name: NULL; Parts: " + ingredient.getParts();
		}
		return "ID: " + ingredient.getID() + "; NAME: " + ingredient.getReag().getName() + "; PARTS: " + ingredient.getParts();
	}
}
