package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.heat.IceboxTileEntity;
import com.Da_Technomancer.crossroads.crafting.IceboxRec;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;


public class IceboxFuelCategory implements IRecipeCategory<IceboxRec>{

	public static final RecipeType<IceboxRec> TYPE = RecipeType.create(Crossroads.MODID, "icebox", IceboxRec.class);
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable icon;

	protected IceboxFuelCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.icebox, 1));
	}

	@Override
	public RecipeType<IceboxRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.icebox.getName();
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
	public void draw(IceboxRec rec, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		slot.draw(matrix, 20, 50);

		int coolTime = Math.round(rec.getCooling());
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.font.draw(matrix, MiscUtil.localize("crossroads.jei.icebox.total", -coolTime * IceboxTileEntity.RATE), 50, 25, 0x404040);
		minecraft.font.draw(matrix, MiscUtil.localize("crossroads.jei.icebox.duration", coolTime), 50, 45, 0x404040);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IceboxRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 21, 51).addIngredients(recipe.getIngredient());
	}
}
