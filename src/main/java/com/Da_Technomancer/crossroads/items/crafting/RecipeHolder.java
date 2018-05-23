package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReaction;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ITransparentReaction;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.integration.JEI.*;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class RecipeHolder{

	/**
	 * CraftingStack is input, the array is the outputs. HAVE NO MORE THAN 3 ITEMSTACKS IN THE ARRAY.
	 * 
	 */
	public static final HashMap<RecipePredicate<ItemStack>, ItemStack[]> grindRecipes = new HashMap<RecipePredicate<ItemStack>, ItemStack[]>();

	/**
	 * Block is input, blockstate is the created block, Double1 is heat created, Double2 is the limit, boolean is whether to show this recipe in JEI. 
	 * 
	 */
	public static final HashMap<Block, Pair<Boolean, Triple<IBlockState, Double, Double>>> envirHeatSource = new HashMap<Block, Pair<Boolean, Triple<IBlockState, Double, Double>>>();

	/**
	 * Fluid is input, Integer is the amount required, ItemStack is output,
	 * Double1 is maximum temperature, and Double2 is heat added on craft.
	 * 
	 */
	public static final HashMap<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> fluidCoolingRecipes = new HashMap<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>>();

	/**
	 * Stores the heating crucible recipes. BE CAREFUL, the contained FluidStack is mutable. The ICraftingStack is the ingredient, FluidStack is output, and String is the unmelted texture
	 */
	public static final ArrayList<Triple<RecipePredicate<ItemStack>, FluidStack, String>> heatingCrucibleRecipes = new ArrayList<Triple<RecipePredicate<ItemStack>, FluidStack, String>>();

	/**
	 * Item is input, magic unit is the magic extracted. For the Arcane Extractor
	 */
	public static final HashMap<Item, MagicUnit> magExtractRecipes = new HashMap<Item, MagicUnit>();

	/**
	 * Stores the fusion beam conversion recipes. 
	 */
	public static final PredicateMap<IBlockState, BeamTransmute> fusionBeamRecipes = new PredicateMap<IBlockState, BeamTransmute>();

	/**
	 * Stores the void-fusion beam conversion recipes. 
	 */
	public static final PredicateMap<IBlockState, BeamTransmute> vFusionBeamRecipes = new PredicateMap<IBlockState, BeamTransmute>();

	/**
	 * The recipes for the Detailed Crafter that require technomancy to be unlocked.
	 * Recipes can have a null group (it is unused). 
	 * ONLY USE ShapedOreRecipe and ShapelessOreRecipe. Using any other type require changing the JEI integration (DetailedCrafterRecipeWrapper) or it will crash.
	 */
	public static final ArrayList<IRecipe> technomancyRecipes = new ArrayList<IRecipe>();

	/**
	 * The recipes for the Detailed Crafter that require alchemy to be unlocked.
	 * Recipes can have a null group (it is unused). 
	 * ONLY USE ShapedOreRecipe and ShapelessOreRecipe. Using any other type require changing the JEI integration (DetailedCrafterRecipeWrapper) or it will crash.
	 */
	public static final ArrayList<IRecipe> alchemyRecipes = new ArrayList<IRecipe>();

	public static final HashMap<String, ArrayList<IRecipeWrapper>> JEIWrappers = new HashMap<String, ArrayList<IRecipeWrapper>>();

	/**
	 * Converts the versions of the recipes used internally into fake recipes
	 * for JEI. Not called unless JEI is installed.
	 */
	public static void rebind(){
		ArrayList<IRecipeWrapper> currentRecipes = new ArrayList<IRecipeWrapper>();
		for(Entry<RecipePredicate<ItemStack>, ItemStack[]> rec : grindRecipes.entrySet()){
			currentRecipes.add(new GrindstoneRecipe(rec));
		}
		JEIWrappers.put(GrindstoneCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(Entry<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> rec : fluidCoolingRecipes.entrySet()){
			currentRecipes.add(new FluidCoolingRecipe(rec));
		}
		JEIWrappers.put(FluidCoolingCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(Entry<Block, Pair<Boolean, Triple<IBlockState, Double, Double>>> rec : envirHeatSource.entrySet()){
			if(rec.getValue().getLeft()){
				currentRecipes.add(new HeatExchangerRecipe(rec));
			}
		}
		JEIWrappers.put(HeatExchangerCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(IRecipe rec : technomancyRecipes){
			currentRecipes.add(new DetailedCrafterRecipe(rec, 0));
		}
		for(IRecipe rec : alchemyRecipes){
			currentRecipes.add(new DetailedCrafterRecipe(rec, 1));
		}
		JEIWrappers.put(DetailedCrafterCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(Triple<RecipePredicate<ItemStack>, FluidStack, String> rec : heatingCrucibleRecipes){
			currentRecipes.add(new HeatingCrucibleRecipe(rec.getLeft(), rec.getMiddle()));
		}
		JEIWrappers.put(HeatingCrucibleCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(Entry<Item, MagicUnit> rec : magExtractRecipes.entrySet()){
			currentRecipes.add(new ArcaneExtractorRecipe(new ItemStack(rec.getKey(), 1, OreDictionary.WILDCARD_VALUE), rec.getValue()));
		}
		JEIWrappers.put(ArcaneExtractorCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(Pair<Predicate<IBlockState>, BeamTransmute> rec : fusionBeamRecipes.entrySet()){
			currentRecipes.add(new FusionBeamRecipe(rec.getKey(), rec.getRight().state, rec.getRight().minPower, false));
		}
		for(Pair<Predicate<IBlockState>, BeamTransmute> rec : vFusionBeamRecipes.entrySet()){
			currentRecipes.add(new FusionBeamRecipe(rec.getKey(), rec.getRight().state, rec.getRight().minPower, true));
		}
		JEIWrappers.put(FusionBeamCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(IReaction react : AlchemyCore.REACTIONS){
			if(react instanceof ITransparentReaction){
				currentRecipes.add(new ReactionRecipe((ITransparentReaction) react));
			}
		}
		JEIWrappers.put(ReactionCategory.ID, currentRecipes);

		currentRecipes = new ArrayList<IRecipeWrapper>();
		for(IReagent reag : AlchemyCore.REAGENTS){
			if(reag != null){
				currentRecipes.add(new ReagInfoRecipe(reag));
			}
		}
		JEIWrappers.put(ReagInfoCategory.ID, currentRecipes);
	}
}
