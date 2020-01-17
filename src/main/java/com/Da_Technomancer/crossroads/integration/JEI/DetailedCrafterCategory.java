package com.Da_Technomancer.crossroads.integration.JEI;


import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IShapedRecipe;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class DetailedCrafterCategory implements IRecipeCategory<DetailedCrafterRecipe>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "detailed_crafter");
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable gear;
	private final IDrawable flask;
//	private final IDrawable leaf;

	protected DetailedCrafterCategory(IGuiHelper guiHelper){
		ResourceLocation location = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");
		back = guiHelper.createDrawable(location, 29, 16, 125, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.detailedCrafter, 1));
		gear = guiHelper.createDrawable(location, 176, 0, 16, 16);
		flask = guiHelper.createDrawable(location, 176, 16, 16, 16);
//		leaf = guiHelper.createDrawable(location, 176, 32, 16, 16);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends DetailedCrafterRecipe> getRecipeClass(){
		return DetailedCrafterRecipe.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.detailedCrafter.getNameTextComponent().getFormattedText();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, DetailedCrafterRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();

		//TODO clean up- very messy way of doing it to go through the ingredient
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				itemGroup.init((y * 3) + x, true, x * 18, y * 18);
				itemGroup.set((y * 3) + x, ingredients.getInputs(VanillaTypes.ITEM).get((y * 3) + x));
			}
		}

		itemGroup.init(9, false, 94, 18);
		itemGroup.set(9, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

		itemGroup.set(ingredients);
	}

	@Override
	public void draw(DetailedCrafterRecipe recipe, double mouseX, double mouseY){
		Minecraft.getInstance().fontRenderer.drawString("Shapeless", 60, 5, 0x404040);
		switch(recipe.type){
			case 0:
				gear.draw(95, 44);
				break;
			case 1:
				flask.draw(79, 44);
				break;
			case 2:
//			leaf.draw(111, 44);
				break;
		}
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(DetailedCrafterRecipe recipe, IIngredients ingredients){
		//TODO clear up- very messy way of doing it
		if(recipe.recipe instanceof IShapedRecipe){
			Object[] input = recipe.recipe.getIngredients().toArray();
			int width = ((IShapedRecipe) recipe.recipe).getRecipeWidth();
			int height = ((IShapedRecipe) recipe.recipe).getRecipeHeight();
			ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(format(input[0]), format(width < 2 ? null : input[1]), format(width < 3 ? null : input[2]), format(height < 2 ? null : input[width]), format(height < 2 || width < 2 ? null : input[width + 1]), format(height < 2 || width < 3 ? null : input[width + 2]), format(height < 3 ? null : input[width * 2]), format(height < 3 || width < 2 ? null : input[(width * 2) + 1]), format(height < 3 || width < 3 ? null : input[(width * 2) + 2])));
		}else{
			NonNullList<Ingredient> input = recipe.recipe.getIngredients();
			ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(format(input.get(0)), format(input.size() < 2 ? null : input.get(1)), format(input.size() < 3 ? null : input.get(2)), format(input.size() < 4 ? null : input.get(3)), format(input.size() < 5 ? null : input.get(4)), format(input.size() < 6 ? null : input.get(5)), format(input.size() < 7 ? null : input.get(6)), format(input.size() < 8 ? null : input.get(7)), format(input.size() < 9 ? null : input.get(8))));
		}
		ingredients.setOutputs(VanillaTypes.ITEM, ImmutableList.of(recipe.recipe.getRecipeOutput()));
	}

	@SuppressWarnings("unchecked")
	private static List<ItemStack> format(@Nullable Object ingr){
		if(ingr instanceof NonNullList<?>){
			return (NonNullList<ItemStack>) ingr;
		}
		if(ingr instanceof ItemStack){
			return ImmutableList.of((ItemStack) ingr);
		}
		if(ingr instanceof Ingredient){
			return Arrays.asList(((Ingredient) ingr).getMatchingStacks());
		}
		return ImmutableList.of();
	}
}
