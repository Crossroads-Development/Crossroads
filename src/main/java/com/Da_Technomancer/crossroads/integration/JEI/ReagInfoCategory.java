package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
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

public class ReagInfoCategory implements IRecipeCategory<IReagent>{

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
	public Class<? extends IReagent> getRecipeClass(){
		return IReagent.class;
	}

	@Override
	public String getTitle(){
		return "Reagent Info";
	}

	@Override
	public List<String> getTooltipStrings(IReagent recipe, double mouseX, double mouseY){
		if(mouseX >= 2 && mouseX <= 18 && mouseY >= 2 && mouseY <= 18){
			return ImmutableList.of(recipe.getName());
		}
		return Collections.emptyList();
	}

	@Override
	public void draw(IReagent recipe, double mouseX, double mouseY){
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		double melt = recipe.getMeltingPoint();
		double boil = recipe.getBoilingPoint();
		String line = melt >= Short.MAX_VALUE - 10 ? MiscUtil.localize("crossroads.jei.reagent.melting.no") : melt <= HeatUtil.ABSOLUTE_ZERO ? MiscUtil.localize("crossroads.jei.reagent.melting.yes") : MiscUtil.localize("crossroads.jei.reagent.melting", Math.round(melt));
		fontRenderer.drawString(line, 2, 22, 0x404040);
		line = boil >= Short.MAX_VALUE - 10 ? MiscUtil.localize("crossroads.jei.reagent.boiling.no") : boil <= HeatUtil.ABSOLUTE_ZERO ? MiscUtil.localize("crossroads.jei.reagent.boiling.yes") : MiscUtil.localize("crossroads.jei.reagent.boiling", Math.round(boil));
		fontRenderer.drawString(line, 2, 42, 0x404040);
		if(recipe.requiresCrystal()){
			fontRenderer.drawString(MiscUtil.localize("crossroads.jei.reagent.crystal"), 2, 62, 0x404040);
		}

		//GlStateManager.color(1, 1, 1);
		ReagentIngredientRenderer.RENDERER.render(2, 2, new ReagIngr(recipe, 1));
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
	public void setIngredients(IReagent recipe, IIngredients ingredients){
		ReagIngr reagIngr = new ReagIngr(recipe, 1);
		ingredients.setInput(ReagIngr.REAG, reagIngr);
		ingredients.setOutput(ReagIngr.REAG, reagIngr);
		List<ItemStack> solid = recipe.getJEISolids();
		List<List<ItemStack>> solidLists = ImmutableList.of(solid);
		ingredients.setInputLists(VanillaTypes.ITEM, solidLists);
		ingredients.setOutputLists(VanillaTypes.ITEM, solidLists);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IReagent recipe, IIngredients ingredients){
//		List<ReagIngr> reag = ingredients.getInputs(ReagIngr.REAG).get(0);
		IGuiIngredientGroup<ReagIngr> reagGroup = layout.getIngredientsGroup(ReagIngr.REAG);

		reagGroup.init(0, true, 2, 2);
		reagGroup.set(0, ingredients.getInputs(ReagIngr.REAG).get(0));

		layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 20, 2);
		layout.getIngredientsGroup(VanillaTypes.ITEM).set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
	}
}
