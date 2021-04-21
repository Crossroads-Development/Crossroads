package com.Da_Technomancer.crossroads.integration.JEI;


import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.DetailedCrafterRec;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class DetailedCrafterCategory implements IRecipeCategory<DetailedCrafterRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "detailed_crafter");
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable gear;
	private final IDrawable flask;
	private final IDrawable leaf;
	private final ICraftingGridHelper gridHelper;

	protected DetailedCrafterCategory(IGuiHelper guiHelper){
		ResourceLocation location = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");
		back = guiHelper.createDrawable(location, 29, 16, 125, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.detailedCrafter, 1));
		gear = guiHelper.createDrawable(location, 176, 0, 16, 16);
		flask = guiHelper.createDrawable(location, 176, 16, 16, 16);
		leaf = guiHelper.createDrawable(location, 176, 32, 16, 16);
		gridHelper = guiHelper.createCraftingGridHelper(1);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends DetailedCrafterRec> getRecipeClass(){
		return DetailedCrafterRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.detailedCrafter.getName().getString();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, DetailedCrafterRec recipe, IIngredients ingredients){
		//Based on (read: shameless copied from) the JEI implementation of vanilla crafting recipes

		IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
		guiItemStacks.init(0, false, 94, 18);

		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 3; ++x){
				int index = 1 + x + y * 3;
				guiItemStacks.init(index, true, x * 18, y * 18);
			}
		}

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
		int width = recipe.getWidth();
		int height = recipe.getHeight();
		if(width > 0 && height > 0){
			gridHelper.setInputs(guiItemStacks, inputs, width, height);
		}else{
			gridHelper.setInputs(guiItemStacks, inputs);
			layout.setShapeless();
		}

		guiItemStacks.set(0, outputs.get(0));
	}

	@Override
	public void draw(DetailedCrafterRec recipe, MatrixStack matrix, double mouseX, double mouseY){
		//Minecraft.getInstance().fontRenderer.drawString("Shapeless", 60, 5, 0x404040);
		switch(recipe.getPath()){
			case TECHNOMANCY:
				gear.draw(matrix, 95, 44);
				break;
			case ALCHEMY:
				flask.draw(matrix, 79, 44);
				break;
			case WITCHCRAFT:
				leaf.draw(matrix, 111, 44);
				break;
		}
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(DetailedCrafterRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}
}
