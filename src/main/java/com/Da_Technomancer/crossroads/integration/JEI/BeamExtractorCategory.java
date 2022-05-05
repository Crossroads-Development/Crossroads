package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamExtractRec;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;


public class BeamExtractorCategory implements IRecipeCategory<BeamExtractRec>{

	public static final RecipeType<BeamExtractRec> TYPE = RecipeType.create(Crossroads.MODID, "beam_extractor", BeamExtractRec.class);
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected BeamExtractorCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CRBlocks.beamExtractor, 1));
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public ResourceLocation getUid(){
		return TYPE.getUid();
	}

	@Override
	public Class<? extends BeamExtractRec> getRecipeClass(){
		return TYPE.getRecipeClass();
	}

	@Override
	public RecipeType<BeamExtractRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.beamExtractor.getName();
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
	public void draw(BeamExtractRec rec, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		slot.draw(matrix, 20, 50);
		arrowStatic.draw(matrix, 46, 50);

		Minecraft minecraft = Minecraft.getInstance();
		ArrayList<String> tt = new ArrayList<>(4);
		if(rec.getOutput().getEnergy() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.energy", rec.getOutput().getEnergy()));
		}else{
			tt.add("");
		}
		if(rec.getOutput().getPotential() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.potential", rec.getOutput().getPotential()));
		}else{
			tt.add("");
		}
		if(rec.getOutput().getStability() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.stability", rec.getOutput().getStability()));
		}else{
			tt.add("");
		}
		if(rec.getOutput().getVoid() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.void", rec.getOutput().getVoid()));
		}else{
			tt.add("");
		}

		//Different localization based on singular or plural for english grammar
		if(rec.getDuration() == 1){
			tt.add(MiscUtil.localize("crossroads.jei.extract.duration.single", rec.getDuration()));//Duration
		}else{
			tt.add(MiscUtil.localize("crossroads.jei.extract.duration.plural", rec.getDuration()));//Duration
		}
		for(int i = 0; i < tt.size(); i++){
			minecraft.font.draw(matrix, tt.get(i), 74, 5 + 20 * i, 0x404040);
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BeamExtractRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 21, 51).addIngredients(recipe.getIngredient());
	}
}
