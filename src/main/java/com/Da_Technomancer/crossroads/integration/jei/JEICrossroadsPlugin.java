package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.item_sets.OreProfileItem;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.items.technomancy.TechnomancyArmor;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
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
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.millstone, 1), MillstoneCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.stampMill, 1), StampMillCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.heatingCrucible, 1), HeatingCrucibleCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.waterCentrifuge, 1), CentrifugeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.blastFurnace, 1), BlastFurnaceCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.oreCleanser, 1), OreCleanserCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.beamExtractor, 1), BeamExtractorCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.beamReflector, 1), BeamTransmuteCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.detailedCrafter, 1), DetailedCrafterCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.detailedAutoCrafter, 1), DetailedCrafterCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.reactionChamberGlass, 1), AlchemyCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.reactionChamberCrystal, 1), AlchemyCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.glasswareHolder, 1), AlchemyCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.copshowiumCreationChamber, 1), CopshowiumCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.formulationVat, 1), FormulationVatCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.icebox, 1), IceboxFuelCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.lensFrame, 1), BeamLensCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.incubator, 1), IncubatorCategory.TYPE);

		//Add relevant crossroads machines to vanilla recipe types
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.smelter, 1), RecipeTypes.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.firebox, 1), RecipeTypes.FUELING);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.steamer, 1), RecipeTypes.SMOKING);
		registry.addRecipeCatalyst(new ItemStack(CRBlocks.brewingVat, 1), RecipeTypes.BREWING);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, null, DetailedCrafterCategory.TYPE, 1, 9, 10, 36);
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, null, RecipeTypes.CRAFTING, 1, 9, 10, 36);
		registration.addRecipeTransferHandler(new DetailedAutoCrafterTransfers.DetailedRecipeTransfer(registration.getTransferHelper()), DetailedCrafterCategory.TYPE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration){
		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

		registration.addRecipes(MillstoneCategory.TYPE, getRecipes(recipeManager, CRRecipes.MILL_TYPE));
		registration.addRecipes(StampMillCategory.TYPE, getRecipes(recipeManager, CRRecipes.STAMP_MILL_TYPE));
		registration.addRecipes(FluidCoolingCategory.TYPE, getRecipes(recipeManager, CRRecipes.FLUID_COOLING_TYPE));
		registration.addRecipes(HeatingCrucibleCategory.TYPE, getRecipes(recipeManager, CRRecipes.CRUCIBLE_TYPE));
		registration.addRecipes(CentrifugeCategory.TYPE, getRecipes(recipeManager, CRRecipes.CENTRIFUGE_TYPE));
		registration.addRecipes(BlastFurnaceCategory.TYPE, getRecipes(recipeManager, CRRecipes.BLAST_FURNACE_TYPE));
		registration.addRecipes(OreCleanserCategory.TYPE, getRecipes(recipeManager, CRRecipes.ORE_CLEANSER_TYPE));
		registration.addRecipes(BeamExtractorCategory.TYPE, getRecipes(recipeManager, CRRecipes.BEAM_EXTRACT_TYPE));
		registration.addRecipes(BeamTransmuteCategory.TYPE, getRecipes(recipeManager, CRRecipes.BEAM_TRANSMUTE_TYPE));
		registration.addRecipes(DetailedCrafterCategory.TYPE, getRecipes(recipeManager, CRRecipes.DETAILED_TYPE));
		registration.addRecipes(AlchemyCategory.TYPE, getRecipes(recipeManager, CRRecipes.ALCHEMY_TYPE));
		registration.addRecipes(CopshowiumCategory.TYPE, getRecipes(recipeManager, CRRecipes.COPSHOWIUM_TYPE));
		registration.addRecipes(FormulationVatCategory.TYPE, getRecipes(recipeManager, CRRecipes.FORMULATION_VAT_TYPE));
		registration.addRecipes(BeamLensCategory.TYPE, getRecipes(recipeManager, CRRecipes.BEAM_LENS_TYPE));
		registration.addRecipes(ReagInfoCategory.TYPE, new ArrayList<>(ReagentManager.getRegisteredReags()));
		registration.addRecipes(IceboxFuelCategory.TYPE, getRecipes(recipeManager, CRRecipes.COOLING_TYPE));
		registration.addRecipes(IncubatorCategory.TYPE, getRecipes(recipeManager, CRRecipes.INCUBATOR_TYPE));

		//Add anvil recipes for Technomancy items
		IVanillaRecipeFactory vanillaFactory = registration.getVanillaRecipeFactory();
		ArrayList<IJeiAnvilRecipe> anvilRecipes = new ArrayList<>(4);
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.armorGoggles), ImmutableList.of(new ItemStack(Items.NETHERITE_HELMET)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.armorGoggles, 1), true))));
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.propellerPack), ImmutableList.of(new ItemStack(Items.NETHERITE_CHESTPLATE)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.propellerPack, 1), true))));
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.armorToolbelt), ImmutableList.of(new ItemStack(Items.NETHERITE_LEGGINGS)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.armorToolbelt, 1), true))));
		anvilRecipes.add(vanillaFactory.createAnvilRecipe(new ItemStack(CRItems.armorEnviroBoots), ImmutableList.of(new ItemStack(Items.NETHERITE_BOOTS)), ImmutableList.of(TechnomancyArmor.setReinforced(new ItemStack(CRItems.armorEnviroBoots, 1), true))));
		registration.addRecipes(RecipeTypes.ANVIL, anvilRecipes);
	}

	private static <T extends Recipe<?>> List<T> getRecipes(RecipeManager manage, RecipeType<T> type){
		//Filter to recipes of the passed type, and check that they're enabled if they're IOptionalRecipe
		return manage.getRecipes().parallelStream().filter(rec -> rec.getType() == type && (!(rec instanceof IOptionalRecipe) || ((IOptionalRecipe<?>) rec).isEnabled())).map(recipe -> (T) recipe).collect(Collectors.toList());
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
				new BeamLensCategory(guiHelper),
				new IncubatorCategory(guiHelper)
		);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration){
		//Register item types with a finite number of variants based on NBT

		final IIngredientSubtypeInterpreter<ItemStack> oreProfileInterpreter = (ItemStack stack, UidContext context) -> {
			CRMaterialLibrary.OreProfile mat = OreProfileItem.getProfile(stack);
			return mat == null ? IIngredientSubtypeInterpreter.NONE : mat.getName();
		};

		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.oreClump, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.oreGravel, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.largeGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.smallGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.toggleGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.invToggleGear, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.axle, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.clutch, oreProfileInterpreter);
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CRItems.invClutch, oreProfileInterpreter);
	}

	protected static IDrawableStatic createFluidOverlay(IGuiHelper helper){
//		guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64)
		return helper.drawableBuilder(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64).setTextureSize(64, 64).build();
	}
}
