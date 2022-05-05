package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;
import java.util.List;

public class ReagInfoCategory implements IRecipeCategory<IReagent>{

	public static final RecipeType<IReagent> TYPE = RecipeType.create(Crossroads.MODID, "reag_info", IReagent.class);
	private final IDrawable back;
	private final IDrawable icon;

	protected ReagInfoCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CRItems.phialGlass, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return TYPE.getUid();
	}

	@Override
	public Class<? extends IReagent> getRecipeClass(){
		return TYPE.getRecipeClass();
	}

	@Override
	public RecipeType<IReagent> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return new TranslatableComponent("crossroads.jei.reag_info.cat_name");
	}

	@Override
	public List<Component> getTooltipStrings(IReagent recipe, IRecipeSlotsView view, double mouseX, double mouseY){
		if(mouseX >= 2 && mouseX <= 18 && mouseY >= 2 && mouseY <= 18){
			return ImmutableList.of(new TextComponent(recipe.getName()));
		}
		return Collections.emptyList();
	}

	@Override
	public void draw(IReagent recipe, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		Font fontRenderer = Minecraft.getInstance().font;
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
	public void setRecipe(IRecipeLayoutBuilder builder, IReagent recipe, IFocusGroup focuses){
		ReagIngr reagIngr = new ReagIngr(recipe, 1);
		//We add as both input and output to enable the lookup in both directions
		builder.addSlot(RecipeIngredientRole.INPUT, 3, 3).addIngredient(ReagIngr.REAG, reagIngr);
		builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredient(ReagIngr.REAG, reagIngr);
		//In the event that items fail to load into JEI because the tag hasn't been initialized yet, the try-catch lets the recipe load without the item form
		try{
			TagKey<Item> jeiSolids = recipe.getJEISolids();
			Ingredient itemForm = Ingredient.of(jeiSolids);
			if(!itemForm.isEmpty()){
				builder.addSlot(RecipeIngredientRole.INPUT, 21, 3).addIngredients(itemForm);
				builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredients(itemForm);
			}
		}catch(Exception e){
			Crossroads.logger.error(String.format("Failed to load item form of reagent %1$s for JEI integration", recipe.getName()));
		}
	}
}
