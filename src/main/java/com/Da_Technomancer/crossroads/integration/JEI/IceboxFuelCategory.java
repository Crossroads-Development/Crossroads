package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.IceboxRec;
import com.Da_Technomancer.crossroads.tileentities.heat.IceboxTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


public class IceboxFuelCategory implements IRecipeCategory<IceboxRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "icebox");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable icon;

	protected IceboxFuelCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.icebox, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends IceboxRec> getRecipeClass(){
		return IceboxRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.icebox.getName().getString();
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
	public void setIngredients(IceboxRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
	}

	@Override
	public void draw(IceboxRec rec, MatrixStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 20, 50);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();

		int coolTime = Math.round(rec.getCooling());
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.font.draw(matrix, MiscUtil.localize("crossroads.jei.icebox.total", -coolTime * IceboxTileEntity.RATE), 50, 25, 0x404040);
		minecraft.font.draw(matrix, MiscUtil.localize("crossroads.jei.icebox.duration", coolTime), 50, 45, 0x404040);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IceboxRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();
		itemGroup.init(0, true, 20, 50);
//		itemGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
		itemGroup.set(ingredients);
	}
}
