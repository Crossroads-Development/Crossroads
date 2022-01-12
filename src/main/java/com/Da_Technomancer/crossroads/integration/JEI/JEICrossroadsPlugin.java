package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.IOptionalRecipe;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreProfileItem;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.items.technomancy.TechnomancyArmor;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

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
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.stampMill, 1), StampMillCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.heatingCrucible, 1), HeatingCrucibleCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.waterCentrifuge, 1), CentrifugeCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.blastFurnace, 1), BlastFurnaceCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.oreCleanser, 1), OreCleanserCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.beamExtractor, 1), BeamExtractorCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.beamReflector, 1), BeamTransmuteCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.detailedCrafter, 1), DetailedCrafterCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.detailedAutoCrafter, 1), DetailedCrafterCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.reactionChamberGlass, 1), AlchemyCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.reactionChamberCrystal, 1), AlchemyCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.glasswareHolder, 1), AlchemyCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.copshowiumCreationChamber, 1), CopshowiumCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.formulationVat, 1), FormulationVatCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.icebox, 1), IceboxFuelCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.lensFrame, 1), BeamLensCategory.ID);

		//Add relevant crossroads machines to vanilla recipe types
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.smelter, 1), VanillaRecipeCategoryUid.FURNACE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.firebox, 1), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.steamer, 1), VanillaRecipeCategoryUid.SMOKING);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.brewingVat, 1), VanillaRecipeCategoryUid.BREWING);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, DetailedCrafterCategory.ID, 1, 9, 10, 36);
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
		registration.addRecipeTransferHandler(new DetailedAutoCrafterTransfers.DetailedRecipeTransfer(registration.getTransferHelper()), DetailedCrafterCategory.ID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration){
		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

		registration.addRecipes(getRecipes(recipeManager, CRRecipes.MILL_TYPE), MillstoneCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.STAMP_MILL_TYPE), StampMillCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.FLUID_COOLING_TYPE), FluidCoolingCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.CRUCIBLE_TYPE), HeatingCrucibleCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.CENTRIFUGE_TYPE), CentrifugeCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.BLAST_FURNACE_TYPE), BlastFurnaceCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.ORE_CLEANSER_TYPE), OreCleanserCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.BEAM_EXTRACT_TYPE), BeamExtractorCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.BEAM_TRANSMUTE_TYPE), BeamTransmuteCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.DETAILED_TYPE), DetailedCrafterCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.ALCHEMY_TYPE), AlchemyCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.COPSHOWIUM_TYPE), CopshowiumCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.FORMULATION_VAT_TYPE), FormulationVatCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.BEAM_LENS_TYPE), BeamLensCategory.ID);
		registration.addRecipes(ReagentManager.getRegisteredReags(), ReagInfoCategory.ID);
		registration.addRecipes(getRecipes(recipeManager, CRRecipes.COOLING_TYPE), IceboxFuelCategory.ID);

		//Add anvil recipes for Technomancy items
		IVanillaRecipeFactory vanillaFactory = registration.getVanillaRecipeFactory();
		ArrayList<Object> anvilRecipes = new ArrayList<>(4);
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.armorGoggles), ImmutableList.of(new ItemStack(Items.NETHERITE_HELMET)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.armorGoggles, 1), true))));
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.propellerPack), ImmutableList.of(new ItemStack(Items.NETHERITE_CHESTPLATE)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.propellerPack, 1), true))));
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.armorToolbelt), ImmutableList.of(new ItemStack(Items.NETHERITE_LEGGINGS)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.armorToolbelt, 1), true))));
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.armorEnviroBoots), ImmutableList.of(new ItemStack(Items.NETHERITE_BOOTS)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.armorEnviroBoots, 1), true))));
		registration.addRecipes(anvilRecipes, VanillaRecipeCategoryUid.ANVIL);
	}

	private static Collection<?> getRecipes(RecipeManager manage, RecipeType<?> type){
		//Filter to recipes of the passed type, and check that they're enabled if they're IOptionalRecipe
		return manage.getRecipes().parallelStream().filter(rec -> rec.getType() == type && (!(rec instanceof IOptionalRecipe) || ((IOptionalRecipe<?>) rec).isEnabled())).collect(Collectors.toList());
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry){
		ReagIngr.populate();
		registry.register(ReagIngr.REAG, ReagIngr.REAG_TYPES, new ReagentIngredientHelper(), ReagentIngredientRenderer.RENDERER);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry){
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(
				new MillstoneCategory(guiHelper),
				new FluidCoolingCategory(guiHelper),
				new HeatingCrucibleCategory(guiHelper),
				new DetailedCrafterCategory(guiHelper),
				new BeamExtractorCategory(guiHelper),
				new AlchemyCategory(guiHelper),
				new ReagInfoCategory(guiHelper),
				new StampMillCategory(guiHelper),
				new OreCleanserCategory(guiHelper),
				new BlastFurnaceCategory(guiHelper),
				new BeamTransmuteCategory(guiHelper),
				new CentrifugeCategory(guiHelper),
				new CopshowiumCategory(guiHelper),
				new FormulationVatCategory(guiHelper),
				new IceboxFuelCategory(guiHelper),
				new BeamLensCategory(guiHelper)
		);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration){
		//Register item types with a finite number of variants based on NBT

		final IIngredientSubtypeInterpreter<ItemStack> oreProfileInterpreter = (ItemStack stack, UidContext context) -> {
			OreSetup.OreProfile mat = OreProfileItem.getProfile(stack);
			return mat == null ? IIngredientSubtypeInterpreter.NONE : mat.getName();
		};

		registration.registerSubtypeInterpreter(CRItems.oreClump, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.oreGravel, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.largeGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.smallGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.toggleGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.invToggleGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.axle, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.clutch, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(CRItems.invClutch, oreProfileInterpreter);
	}

	protected static IDrawableStatic createFluidOverlay(IGuiHelper helper){
//		guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64)
		return helper.drawableBuilder(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64).setTextureSize(64, 64).build();
	}
}
