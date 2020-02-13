package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
@SuppressWarnings("unused")
public class JEICrossroadsPlugin implements IModPlugin{

	private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Crossroads.MODID, "jei_plugin");

	@Override
	public ResourceLocation getPluginUid(){
		return PLUGIN_ID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry){
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.millstone, 1), MillstoneCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.heatingCrucible, 1), HeatingCrucibleCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.detailedCrafter, 1), DetailedCrafterCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.beamExtractor, 1), BeamExtractorCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.beamReflector, 1), FusionBeamCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.reactionChamberGlass, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.reactionChamberCrystal, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.glasswareHolder, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.stampMill, 1), StampMillCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.oreCleanser, 1), OreCleanserCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.blastFurnace, 1), BlastFurnaceCategory.ID);
		//TODO water centrifuge
		//TODO CCC
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, DetailedCrafterCategory.ID, 0, 9, 10, 36);
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, VanillaRecipeCategoryUid.CRAFTING, 0, 9, 10, 36);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration){
		//TODO load the JSON recipes into JEI
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry){
		ReagIngr.populate();
		registry.register(ReagIngr.REAG, ReagIngr.REAG_TYPES, new ReagentIngredientHelper(), ReagentIngredientRenderer.RENDERER);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry){
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new MillstoneCategory(guiHelper), new FluidCoolingCategory(guiHelper), new HeatingCrucibleCategory(guiHelper), new DetailedCrafterCategory(guiHelper), new BeamExtractorCategory(guiHelper), new ReactionCategory(guiHelper), new ReagInfoCategory(guiHelper), new StampMillCategory(guiHelper), new OreCleanserCategory(guiHelper), new BlastFurnaceCategory(guiHelper));
	}
}
