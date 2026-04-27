package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.HydroponicsTroughTileEntity;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HydroponicsTroughCategory implements IRecipeCategory<HydroponicsTroughTileEntity.IHydroponicsRec>{

	public static final RecipeType<HydroponicsTroughTileEntity.IHydroponicsRec> TYPE = RecipeType.create(Crossroads.MODID, "hydroponics_trough", HydroponicsTroughTileEntity.IHydroponicsRec.class);
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected HydroponicsTroughCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 80);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.hydroponicsTrough, 1));

		arrowStatic = guiHelper.createDrawable(ResourceLocation.parse("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public RecipeType<HydroponicsTroughTileEntity.IHydroponicsRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return Component.translatable("crossroads.jei.hydroponics_trough.cat_name");
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
	public void draw(HydroponicsTroughTileEntity.IHydroponicsRec recipe, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY){
		slot.draw(graphics, 20, 15);//Input
		arrowStatic.draw(graphics, 42, 16);
		//Render without shadow
		List<ItemStack> outputs = recipe.getJeiOutputs();
		if(outputs == null){
			//Display message that we can't explicitly state output items
			graphics.drawString(Minecraft.getInstance().font, Component.translatable("crossroads.jei.hydroponics_trough.unknown_output"), 70, 21, 0x404040, false);
		}else{
			//Output
			for(int i = 0; i < 4; i++){
				slot.draw(graphics, 69 + 18 * i, 15);
			}
		}
		graphics.drawString(Minecraft.getInstance().font, recipe.needsLight() ? Component.translatable("crossroads.jei.hydroponics_trough.lighting.light") : Component.translatable("crossroads.jei.hydroponics_trough.lighting.no_light"), 21, 38, 0x404040, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HydroponicsTroughTileEntity.IHydroponicsRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 21, 16).addIngredients(recipe.getIngredient());
		List<ItemStack> outputs = recipe.getJeiOutputs();
		if(outputs == null){
			//We can't explicitly show the outputs, but we can guess that the seed item is also probably in the output list somewhere, so we add it invisibly to make it show up under recipes-for
			builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredients(recipe.getIngredient());
		}else{
			for(int i = 0; i < outputs.size(); i++){
				builder.addOutputSlot(70 + 18*i, 16).addItemStack(outputs.get(i));
			}
		}
	}
}
