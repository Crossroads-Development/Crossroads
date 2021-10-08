package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.recipes.AlchemyRec;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AlchemyCategory implements IRecipeCategory<AlchemyRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "reaction");
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
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRItems.florenceFlaskCrystal, 1));
		bolt = guiHelper.createDrawable(AlchemyCategory.ICONS, 16, 0, 16, 16);
		blast = guiHelper.createDrawable(AlchemyCategory.ICONS, 64, 0, 16, 16);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends AlchemyRec> getRecipeClass(){
		return AlchemyRec.class;
	}

	@Override
	public String getTitle(){
		return "Alchemical Reaction";
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(AlchemyRec recipe, IIngredients ingredients){
		List<ReagIngr> reagents = new ArrayList<>(recipe.getReagents().length);
		for(ReagentStack reag : recipe.getReagents()){
			reagents.add(new ReagIngr(reag));
		}

		if(recipe.getCatalyst() != null){
			reagents.add(new ReagIngr(recipe.getCatalyst(), 0));
		}

		List<ReagIngr> products = new ArrayList<>(recipe.getProducts().length);
		for(ReagentStack prod : recipe.getProducts()){
			products.add(new ReagIngr(prod));
		}
		ingredients.setInputs(ReagIngr.REAG, reagents);
		ingredients.setOutputs(ReagIngr.REAG, products);
	}

	@Override
	public void draw(AlchemyRec recipe, PoseStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		arrowStatic.draw(matrix, 78, 22);
		arrow.draw(matrix, 78, 22);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();

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
//			GlStateManager.color(1, 1, 1);
			bolt.draw(matrix, 66, 2);
		}

		if(recipe.isDestructive()){
//			GlStateManager.color(1, 1, 1);
			blast.draw(matrix, 98, 2);
		}

//		if(recipe.getCatalyst() != null){
//			GlStateManager.color(1, 1, 1);
//			ReagentIngredientRenderer.RENDERER.render(matrix, 82, 2, new ReagIngr(recipe.getCatalyst(), 0));
//		}
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, AlchemyRec recipe, IIngredients ingredients){
		IGuiIngredientGroup<ReagIngr> reagGroup = layout.getIngredientsGroup(ReagIngr.REAG);

		int inCount = recipe.getReagents().length;
//		List<List<ReagIngr>> reags = ingredients.getInputs(ReagIngr.REAG);
		for(int i = 0; i < inCount; i++){
			reagGroup.init(i, true, 60 - i * 20, 20);
//			reagGroup.set(i, reags.get(i));
		}

		//Catalyst
		if(recipe.getCatalyst() != null){//A catalyst was set in setIngredients
			reagGroup.init(inCount, true, 82, 2);
//			reagGroup.set(inCount, reags.get(inCount));
			inCount += 1;
		}

		int outCount = recipe.getProducts().length;
//		List<List<ReagIngr>> prods = ingredients.getOutputs(ReagIngr.REAG);
		for(int i = 0; i < outCount; i++ ){
			reagGroup.init(i + inCount, false, 105 + i * 20, 20);
//			reagGroup.set(i + inCount, prods.get(i));
		}

		reagGroup.set(ingredients);
	}

//	@Override
//	public List<ITextComponent> getTooltipStrings(AlchemyRec recipe, double mouseX, double mouseY){
//		IReagent catalyst = ReagentManager.getReagent(recipe.getCatalyst());
//		if(catalyst != null && mouseX >= 82 && mouseX <= 98 && mouseY >= 2 && mouseY <= 18){
//			return ImmutableList.of(new StringTextComponent(catalyst.getName()));
//		}
//		return Collections.emptyList();
//	}
}
