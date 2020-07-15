package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.recipes.BeamExtractRec;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;


public class BeamExtractorCategory implements IRecipeCategory<BeamExtractRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "beam_extractor");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected BeamExtractorCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.beamExtractor, 1));
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends BeamExtractRec> getRecipeClass(){
		return BeamExtractRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.beamExtractor.getNameTextComponent().getString();
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
	public void setIngredients(BeamExtractRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
	}

	@Override
	public void draw(BeamExtractRec rec, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(20, 50);
		arrowStatic.draw(46, 50);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();

		Minecraft minecraft = Minecraft.getInstance();
		ArrayList<String> tt = new ArrayList<>(4);
		if(rec.getOutput().getEnergy() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.energy", rec.getOutput().getEnergy()));
		}
		if(rec.getOutput().getPotential() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.potential", rec.getOutput().getPotential()));
		}
		if(rec.getOutput().getStability() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.stability", rec.getOutput().getStability()));
		}
		if(rec.getOutput().getVoid() != 0){
			tt.add(MiscUtil.localize("crossroads.jei.extract.void", rec.getOutput().getVoid()));
		}

		tt.add("");//Newline
		//Different localization based on singular or plural for english grammar
		if(rec.getDuration() == 1){
			tt.add(MiscUtil.localize("crossroads.jei.extract.duration.single", rec.getDuration()));//Duration
		}else{
			tt.add(MiscUtil.localize("crossroads.jei.extract.duration.plural", rec.getDuration()));//Duration
		}
		for(int i = 0; i < tt.size(); i++){
			minecraft.fontRenderer.drawString(tt.get(i), 80, 5 + 20 * i, 0x404040);
		}
	}

	@Override
	public void setRecipe(IRecipeLayout layout, BeamExtractRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();
		itemGroup.init(0, true, 20, 50);
		itemGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
	}
}
