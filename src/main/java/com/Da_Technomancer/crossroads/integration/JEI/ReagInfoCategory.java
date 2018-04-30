package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Main;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class ReagInfoCategory implements IRecipeCategory<ReagInfoRecipe>{

	public static final String ID = Main.MODID + ".reag_info";
	private final IDrawable back;

	protected ReagInfoCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Reagent Info";
	}

	@Override
	public String getModName(){
		return Main.MODNAME;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft){

	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ReagInfoRecipe recipe, IIngredients ingredients){
		List<ReagIngr> reag = ingredients.getInputs(ReagIngr.class).get(0);
		recipeLayout.getIngredientsGroup(ReagIngr.class).init(0, true, 2, 2);
		recipeLayout.getIngredientsGroup(ReagIngr.class).set(0, reag);

		if(ingredients.getInputs(ItemStack.class).size() != 0){
			List<ItemStack> solid = ingredients.getInputs(ItemStack.class).get(0);
			recipeLayout.getIngredientsGroup(ItemStack.class).init(0, true, 20, 2);
			recipeLayout.getIngredientsGroup(ItemStack.class).set(0, solid);
		}
	}
}
