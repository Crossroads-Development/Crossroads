package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.BeamExtractRec;
import com.Da_Technomancer.crossroads.crafting.EmbryoLabModifierRec;
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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;


public class EmbryoModifierCategory implements IRecipeCategory<EmbryoLabModifierRec>{

	public static final RecipeType<EmbryoLabModifierRec> TYPE = RecipeType.create(Crossroads.MODID, "embryo_lab_modifier", EmbryoLabModifierRec.class);
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected EmbryoModifierCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.embryoLab, 1));
		arrowStatic = guiHelper.createDrawable(ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public RecipeType<EmbryoLabModifierRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return Component.translatable("crossroads.jei.embryo_modifier.cat_name");
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
	public void draw(EmbryoLabModifierRec rec, IRecipeSlotsView view, GuiGraphics matrix, double mouseX, double mouseY){
		slot.draw(matrix, 10, 50);
		arrowStatic.draw(matrix, 36, 50);

		Minecraft minecraft = Minecraft.getInstance();
		ArrayList<Component> tt = new ArrayList<>(3);

		IEntityModifier modifier = rec.createModifier(ItemStack.EMPTY);
		if(modifier == null){
			tt.add(Component.literal("ERROR"));
		}else{
			tt.add(modifier.getName(null, minecraft.level));
		}
		if(rec.getComplexity() > 0){
			tt.add(Component.translatable("crossroads.jei.embryo_modifier.complexity.increase", rec.getComplexity()));
		}else if(rec.getComplexity() < 0){
			tt.add(Component.translatable("crossroads.jei.embryo_modifier.complexity.decrease", rec.getComplexity()));
		}else{
			tt.add(Component.literal(""));
		}
		if(rec.getSoulComplexity() > 0){
			tt.add(Component.translatable("crossroads.jei.embryo_modifier.soul_complexity.increase", rec.getSoulComplexity()));
		}else if(rec.getSoulComplexity() < 0){
			tt.add(Component.translatable("crossroads.jei.embryo_modifier.soul_complexity.decrease", rec.getSoulComplexity()));
		}else{
			tt.add(Component.literal(""));
		}

		int yOffset = 0;
		for(Component line : tt){
			for(FormattedCharSequence formattedcharsequence : minecraft.font.split(line, 110)){
				matrix.drawString(minecraft.font, formattedcharsequence, 64, 35 + yOffset, 0x404040, false);
				yOffset += 9;
			}
			yOffset += 4;
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, EmbryoLabModifierRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 11, 51).addIngredients(recipe.getIngr());
	}
}
