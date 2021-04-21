package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	public List<ITextComponent> getTooltipStrings(IReagent recipe, double mouseX, double mouseY){
		if(mouseX >= 2 && mouseX <= 18 && mouseY >= 2 && mouseY <= 18){
			return ImmutableList.of(new StringTextComponent(recipe.getName()));
		}
		return Collections.emptyList();
	}

	@Override
	public void draw(IReagent recipe, MatrixStack matrix, double mouseX, double mouseY){
		FontRenderer fontRenderer = Minecraft.getInstance().font;
		double melt = recipe.getMeltingPoint();
		double boil = recipe.getBoilingPoint();
		String line = melt >= Short.MAX_VALUE - 10 ? MiscUtil.localize("crossroads.jei.reagent.melting.no") : melt <= HeatUtil.ABSOLUTE_ZERO ? MiscUtil.localize("crossroads.jei.reagent.melting.yes") : MiscUtil.localize("crossroads.jei.reagent.melting", Math.round(melt));
		fontRenderer.draw(matrix, line, 2, 22, 0x404040);
		line = boil >= Short.MAX_VALUE - 10 ? MiscUtil.localize("crossroads.jei.reagent.boiling.no") : boil <= HeatUtil.ABSOLUTE_ZERO ? MiscUtil.localize("crossroads.jei.reagent.boiling.yes") : MiscUtil.localize("crossroads.jei.reagent.boiling", Math.round(boil));
		fontRenderer.draw(matrix, line, 2, 42, 0x404040);
		line = MiscUtil.localize("crossroads.jei.reagent.effect", recipe.getEffect().getName().getString());
		fontRenderer.draw(matrix, line, 2, 62, 0x404040);
		if(recipe.requiresCrystal()){
			fontRenderer.draw(matrix, MiscUtil.localize("crossroads.jei.reagent.crystal"), 2, 82, 0x404040);
		}

		//GlStateManager.color(1, 1, 1);
		ReagentIngredientRenderer.RENDERER.render(matrix, 2, 2, new ReagIngr(recipe, 1));
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

		//There is a known issue (github issue #119) where the items fail to load into JEI because the tag hasn't been initialized yet
		//The try-catch lets the recipe load without the item form
		try{
			List<ItemStack> solid = recipe.getJEISolids().getValues().stream().map(ItemStack::new).collect(Collectors.toList());
			List<List<ItemStack>> solidLists = ImmutableList.of(solid);
			ingredients.setInputLists(VanillaTypes.ITEM, solidLists);
			ingredients.setOutputLists(VanillaTypes.ITEM, solidLists);
		}catch(Exception e){
			Crossroads.logger.error(String.format("Failed to load item form of reagent %1$s for JEI integration", recipe.getName()));
		}
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IReagent recipe, IIngredients ingredients){
//		List<ReagIngr> reag = ingredients.getInputs(ReagIngr.REAG).get(0);
		IGuiIngredientGroup<ReagIngr> reagGroup = layout.getIngredientsGroup(ReagIngr.REAG);
		IGuiItemStackGroup itemGroup = layout.getItemStacks();

		reagGroup.init(0, true, 2, 2);
//		reagGroup.set(0, ingredients.getInputs(ReagIngr.REAG).get(0));

		itemGroup.init(0, true, 20, 2);
//		itemGroup.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

		reagGroup.set(ingredients);
		itemGroup.set(ingredients);
	}
}
