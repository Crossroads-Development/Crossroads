package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.recipes.AlchemyRec;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AlchemyCategory implements IRecipeCategory<AlchemyRec>{

	public static final RecipeType<AlchemyRec> TYPE = RecipeType.create(Crossroads.MODID, "reaction", AlchemyRec.class);
	protected static final ResourceLocation ICONS = new ResourceLocation(Crossroads.MODID, "textures/gui/icons.png");

	private final IDrawable back;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable icon;
	private final IDrawable bolt;
	private final IDrawable blast;

	protected AlchemyCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		arrowStatic = guiHelper.createDrawable(ICONS, 32, 0, 24, 16);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(ICONS, 32, 16, 24, 16), 40, IDrawableAnimated.StartDirection.LEFT, false);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CRItems.florenceFlaskCrystal, 1));
		bolt = guiHelper.createDrawable(AlchemyCategory.ICONS, 16, 0, 16, 16);
		blast = guiHelper.createDrawable(AlchemyCategory.ICONS, 64, 0, 16, 16);
	}

	@Override
	public ResourceLocation getUid(){
		return TYPE.getUid();
	}

	@Override
	public Class<? extends AlchemyRec> getRecipeClass(){
		return TYPE.getRecipeClass();
	}

	@Override
	public RecipeType<AlchemyRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return new TranslatableComponent("crossroads.jei.alchemy.cat_name");
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void draw(AlchemyRec recipe, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		arrowStatic.draw(matrix, 78, 22);
		arrow.draw(matrix, 78, 22);

		Font fontRenderer = Minecraft.getInstance().font;
		double maxTemp = recipe.maxTemp();
		String line;
		if(maxTemp <= Short.MAX_VALUE - 100){
			line = MiscUtil.localize("crossroads.jei.alchemy.temp.dual", CRConfig.formatVal(Math.max(recipe.minTemp(), HeatUtil.ABSOLUTE_ZERO)), CRConfig.formatVal(maxTemp));
		}else{
			line = MiscUtil.localize("crossroads.jei.alchemy.temp", CRConfig.formatVal(Math.max(recipe.minTemp(), HeatUtil.ABSOLUTE_ZERO)));
		}
		fontRenderer.draw(matrix, line, 90 - fontRenderer.width(line) / 2F, 42, 0x404040);
		line = recipe.deltaHeatPer() > 0 ? MiscUtil.localize("crossroads.jei.alchemy.cooling") : recipe.deltaHeatPer() < 0 ? MiscUtil.localize("crossroads.jei.alchemy.heating") : MiscUtil.localize("crossroads.jei.alchemy.no_temp_change");
		fontRenderer.draw(matrix, line, 90 - fontRenderer.width(line) / 2F, 62, 0x404040);

		if(recipe.charged()){
			bolt.draw(matrix, 66, 2);
		}

		if(recipe.isDestructive()){
			blast.draw(matrix, 98, 2);
		}
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRec recipe, IFocusGroup focuses){
		//Inputs
		for(int i = 0; i < recipe.getReagents().length; i++){
			builder.addSlot(RecipeIngredientRole.INPUT, 61 - i * 20, 21).addIngredient(ReagIngr.REAG, new ReagIngr(recipe.getReagents()[i]));
		}

		//Outputs
		for(int i = 0; i < recipe.getProducts().length; i++ ){
			builder.addSlot(RecipeIngredientRole.OUTPUT, 106 + i * 20, 21).addIngredient(ReagIngr.REAG, new ReagIngr(recipe.getProducts()[i]));
		}

		//Catalyst
		if(recipe.getCatalyst() != null){//A catalyst was set in setIngredients
			builder.addSlot(RecipeIngredientRole.CATALYST, 83, 3).addIngredient(ReagIngr.REAG, new ReagIngr(recipe.getCatalyst(), 0));
		}
	}
}
