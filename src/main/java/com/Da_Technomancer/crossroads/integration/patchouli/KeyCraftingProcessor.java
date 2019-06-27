package com.Da_Technomancer.crossroads.integration.patchouli;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public class KeyCraftingProcessor implements IComponentProcessor {

	private IRecipe recipe;
	private String title;
	private String text;

	private static final String NOTITLE = "thIsDoes_notHave-a_title";

	@Override
	public void setup(IVariableProvider<String> variables) {
		String loc = variables.get("recipe");

		if(loc != null) {
			ResourceLocation res = new ResourceLocation(loc);
			recipe = CraftingManager.getRecipe(res);
			if(recipe == null) {
				// this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
				recipe = CraftingManager.getRecipe(new ResourceLocation("crafttweaker", res.getResourcePath()));
			}
		}

		if (variables.has("title")) {
			title = variables.get("title");
		} else {
			title = NOTITLE;
		}
		if (variables.has("text")) {
			text = variables.get("text");
		} else {
			text = "";
		}
	}

	@Override
	public String process(String key) {
		if(key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			if (recipe != null && recipe.getIngredients().size() > index) {
				Ingredient ingredient = recipe.getIngredients().get(index);
				ItemStack[] stacks = ingredient.getMatchingStacks();
				ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
				return ItemStackUtil.serializeStack(stack);
			}
		}
		else if (recipe != null && key.equals("output")) {
			return ItemStackUtil.serializeStack(recipe.getRecipeOutput());
		} else if (recipe != null && key.equals("title") && title.equals(NOTITLE)) {
			return recipe.getRecipeOutput().getDisplayName();
		}
		else if (key.equals("text")) {
			String[] replacements = StringUtils.substringsBetween(text, "#", "#");
			if (replacements != null && replacements.length != 0) {
				for (String replacing : replacements) {
					text = StringUtils.replace(text, "#" + replacing + "#", DataBuilder.dataMap.get(replacing));
				}
			}
			return(text);
		}

		return null;
	}
}
