package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class ReagentIngredientHelper implements IIngredientHelper<ReagIngr>{

	@Override
	public ItemStack getCheatItemStack(ReagIngr ingredient){
		ItemStack toGive = new ItemStack(ModItems.phial, 1, 1);
		ReagentMap reags = new ReagentMap();
		reags.addReagent(ingredient.getReag(), ModItems.phial.getCapacity());
		ModItems.phial.setReagents(toGive, reags, HeatUtil.toKelvin(50) * ModItems.phial.getCapacity());
		return toGive;
	}
	
	@Override
	public boolean isValidIngredient(ReagIngr ingredient){
		return ingredient != null && ingredient.getReag() != null && ingredient.getParts() >= 0;
	}

	@Override
	public List<ReagIngr> expandSubtypes(List<ReagIngr> ingredients){
		return ingredients;
	}

	@Override
	public ReagIngr getMatch(Iterable<ReagIngr> ingredients, ReagIngr ingredientToMatch){
		for(ReagIngr r : ingredients){
			if(r.getReag() == ingredientToMatch.getReag()){
				return r;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(ReagIngr ingredient){
		return ingredient.getReag().getName();
	}

	@Override
	public String getUniqueId(ReagIngr ingredient){
		return Main.MODID + ":" + ingredient.getReag().getId();
	}

	@Override
	public String getWildcardId(ReagIngr ingredient){
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(ReagIngr ingredient){
		return Main.MODID;
	}

	@Override
	public Iterable<Color> getColors(ReagIngr ingredient){
		HashSet<Color> out = new HashSet<>();
		for(EnumMatterPhase p : EnumMatterPhase.values()){
			out.add(ingredient.getReag().getColor(p));
		}
		return out;
	}

	@Override
	public String getResourceId(ReagIngr ingredient){
		return ingredient.getReag().getName();
	}

	@Override
	public ReagIngr copyIngredient(ReagIngr ingredient){
		return ingredient;//ReagentIngredient is immutable
	}

	@Override
	public String getErrorInfo(ReagIngr ingredient){
		return "ID: " + ingredient.getReag().getId() + "; NAME: " + ingredient.getReag().getName() + "; PARTS: " + ingredient.getParts();
	}
}
