package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class ReagInfoCategory implements IRecipeCategory<ReagInfoRecipe>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "reag_info");
	private final IDrawable back;
	private final IDrawable icon;

	protected ReagInfoCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRItems.phialGlass, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends ReagInfoRecipe> getRecipeClass(){
		return ReagInfoRecipe.class;
	}

	@Override
	public String getTitle(){
		return "Reagent Info";//TODO localize
	}

	@Override
	public List<String> getTooltipStrings(ReagInfoRecipe recipe, double mouseX, double mouseY){
		if(mouseX >= 2 && mouseX <= 18 && mouseY >= 2 && mouseY <= 18){
			return ImmutableList.of(recipe.type.getReag().getName());
		}
		return Collections.emptyList();
	}

	@Override
	public void draw(ReagInfoRecipe recipe, double mouseX, double mouseY){
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		double melt = recipe.type.getReag().getMeltingPoint();
		double boil = recipe.type.getReag().getBoilingPoint();
		String line = "Melting: " + (melt >= Short.MAX_VALUE - 10 ? "Never" : melt < HeatUtil.ABSOLUTE_ZERO ? "Always" : (melt + "°C"));//TODO localize
		fontRenderer.drawString(line, 2, 22, 0x404040);
		line = "Boiling: " + (boil >= Short.MAX_VALUE - 10 ? "Never" : boil < HeatUtil.ABSOLUTE_ZERO ? "Always" : (boil + "°C"));//TODO localize
		fontRenderer.drawString(line, 2, 42, 0x404040);

		//GlStateManager.color(1, 1, 1);
		ReagentIngredientRenderer.RENDERER.render(Minecraft.getInstance(), 2, 2, recipe.type);
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(ReagInfoRecipe recipe, IIngredients ingredients){
		ingredients.setInput(ReagIngr.REAG, recipe.type);
		ingredients.setOutput(ReagIngr.REAG, recipe.type);
		if(!recipe.solid.isEmpty()){
			ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.solid));
			ingredients.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.solid));
		}
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ReagInfoRecipe recipe, IIngredients ingredients){
//		List<ReagIngr> reag = ingredients.getInputs(ReagIngr.REAG).get(0);
		IGuiIngredientGroup<ReagIngr> reagGroup = layout.getIngredientsGroup(ReagIngr.REAG);

		reagGroup.init(0, true, 2, 2);
		reagGroup.set(0, recipe.type);

		if(!recipe.solid.isEmpty()){
			layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 20, 2);
			layout.getIngredientsGroup(VanillaTypes.ITEM).set(0, recipe.solid);
		}
	}
}
