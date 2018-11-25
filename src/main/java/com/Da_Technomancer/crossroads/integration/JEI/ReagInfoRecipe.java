package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ReagInfoRecipe implements IRecipeWrapper{

	private final ReagIngr type;
	private final List<ItemStack> solid;

	public ReagInfoRecipe(IReagent type){
		this.type = new ReagIngr(type, 0);
		solid = type.getJEISolids();
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		if(mouseX >= 2 && mouseX <= 18 && mouseY >= 2 && mouseY <= 18){
			return ImmutableList.of(type.getReag().getName());
		}
		return Collections.emptyList();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		FontRenderer fontRenderer = minecraft.fontRenderer;
		double melt = type.getReag().getMeltingPoint();
		double boil = type.getReag().getBoilingPoint();
		String line = "Melting: " + (melt >= Short.MAX_VALUE - 10 ? "Never" : melt < HeatUtil.ABSOLUTE_ZERO ? "Always" : (melt + "°C"));
		fontRenderer.drawString(line, 2, 22, 4210752);
		line = "Boiling: " + (boil >= Short.MAX_VALUE - 10 ? "Never" : boil < HeatUtil.ABSOLUTE_ZERO ? "Always" : (boil + "°C"));
		fontRenderer.drawString(line, 2, 42, 4210752);

		GlStateManager.color(1, 1, 1);
		ReagentIngredientRenderer.RENDERER.render(minecraft, 2, 2, type);
	}

	@Override
	public void getIngredients(IIngredients ingredients){
		ingredients.setInput(ReagIngr.class, type);
		ingredients.setOutput(ReagIngr.class, type);
		if(solid != null){
			ingredients.setInput(ItemStack.class, solid);
			ingredients.setOutput(ItemStack.class, solid);
		}
	}
}
