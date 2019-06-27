package com.Da_Technomancer.crossroads.integration.patchouli;

import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import org.apache.commons.lang3.StringUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public class DetailedCrafterProcessor implements IComponentProcessor {
	private IRecipe recipe;
	private String path;
	private String title;
	private String text;

	private static final String ALCHEMY = "alchemy";
	private static final String TECHNOMANCY = "technomancy";
	private static final String ANY = "any";

	private static final String NOTITLE = "thIsDoes_notHave-a_title";

	@Override
	public void setup(IVariableProvider<String> variables) {
		//the unlocalized name. the easiest way I saw to do it (I dont know forge very well ok?), if horribly flawed.
		//this will not work with metadata, which thankfully is gone with 1.13, and multiple recipes for a single item.
		//Fortunately the detailed crafter does not have any item results with multiple pathways to it.
		//The detailed crafter recipes are not assigned a ResourceLocation, which would be the clean way to get it.
		//Alternatively, a map for the recipe storage would enable the same sort of behavior, if nowhere near as "perfect"
		String output = variables.get("recipe");

		//valid paths: technomancy, and alchemy
		path = variables.get("path");

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

		if (path.equalsIgnoreCase(ALCHEMY) || path.equals(ANY)){
			recipe = RecipeHolder.alchemyRecipes.parallelStream().
					filter(rep -> rep != null && rep.getRecipeOutput().getUnlocalizedName().equals(output)).
					findAny().orElse(null);
		}
		if (path.equalsIgnoreCase(TECHNOMANCY)){
			recipe = RecipeHolder.technomancyRecipes.parallelStream().
					filter(rep -> rep != null && rep.getRecipeOutput().getUnlocalizedName().equals(output)).
					findAny().orElse(null);
		}


	}

	@Override
	public String process(String key) {
		if(key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			if (recipe.getIngredients().size() > index) {
				Ingredient ingredient = recipe.getIngredients().get(index);
				ItemStack[] stacks = ingredient.getMatchingStacks();
				ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
				return ItemStackUtil.serializeStack(stack);
			}
		}
		else if (key.equals("output")) {
			return ItemStackUtil.serializeStack(recipe.getRecipeOutput());
		} else if (key.equals("title") && title.equals(NOTITLE)) {
			return recipe.getRecipeOutput().getDisplayName();
		}
		if (key.equals("text")) {
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

	@Override
	public boolean allowRender(String group) {
		if (path.equals(ANY)) {
			return true;
		}
		if (group.equals(ALCHEMY) && path.equals(ALCHEMY)) {
			return true;
		}
		if (group.equals(TECHNOMANCY) && path.equals(TECHNOMANCY)) {
			return true;
		}
		return false;
	}
}
