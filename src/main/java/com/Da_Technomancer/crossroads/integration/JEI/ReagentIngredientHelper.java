package com.Da_Technomancer.crossroads.integration.JEI;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumSolventType;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.items.ModItems;

import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;

public class ReagentIngredientHelper implements IIngredientHelper<ReagIngr>{

	@Override
	public ItemStack cheatIngredient(ReagIngr ingredient, boolean fullStack){
		ItemStack toGive = new ItemStack(ModItems.phial, 1, 1);

		ReagentStack[] reag = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		reag[ingredient.getReag().getIndex()] = new ReagentStack(AlchemyCore.REAGENTS[ingredient.getReag().getIndex()], ModItems.phial.getCapacity());
		reag[ingredient.getReag().getIndex()].updatePhase(100, new boolean[EnumSolventType.values().length]);
		ModItems.phial.setReagents(toGive, reag, (100 + 273D) * ModItems.phial.getCapacity(), ModItems.phial.getCapacity());
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
		return Main.MODID + ":" + ingredient.getReag().toString();
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
		HashSet<Color> out = new HashSet<Color>();
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
		return "ID: " + ingredient.getReag().getIndex() + "; NAME: " + ingredient.getReag().getName() + "; PARTS: " + ingredient.getParts();
	}
}
