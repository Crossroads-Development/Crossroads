package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.integration.JEI.*;
import com.Da_Technomancer.crossroads.items.crafting.recipes.*;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

@ObjectHolder(Crossroads.MODID)
public final class RecipeHolder{

	@ObjectHolder("stamp_mill")
	public static IRecipeSerializer<StampMillRec> STAMP_MILL_SERIAL = null;
	@ObjectHolder("mill")
	public static IRecipeSerializer<MillRec> MILL_SERIAL = null;
	@ObjectHolder("ore_cleanser")
	public static IRecipeSerializer<OreCleanserRec> ORE_CLEANSER_SERIAL = null;
	@ObjectHolder("beam_extract")
	public static IRecipeSerializer<BeamExtractRec> BEAM_EXTRACT_SERIAL = null;
	@ObjectHolder("cooling")
	public static IRecipeSerializer<IceboxRec> COOLING_SERIAL = null;
	@ObjectHolder("dirty_water")
	public static IRecipeSerializer<DirtyWaterRec> DIRTY_WATER_SERIAL = null;
	@ObjectHolder("alchemy")
	public static IRecipeSerializer<AlchemyRec> ALCHEMY_SERIAL = null;
	@ObjectHolder("cr_blast_furnace")
	public static IRecipeSerializer<BlastFurnaceRec> BLAST_FURNACE_SERIAL = null;
	@ObjectHolder("fluid_cooling")
	public static IRecipeSerializer<FluidCoolingRec> FLUID_COOLING_SERIAL = null;
	@ObjectHolder("crucible")
	public static IRecipeSerializer<FluidCoolingRec> CRUCIBLE_SERIAL = null;
	@ObjectHolder("detailed_crafter")
	public static IRecipeSerializer<DetailedCrafterRec> DETAILED_SERIAL = null;

	//Forge hasn't replaced the IRecipeType registry (yet). We use the vanilla registry as a result.
	public static IRecipeType<MillRec> MILL_TYPE = IRecipeType.register(Crossroads.MODID + ":mill");
	public static IRecipeType<StampMillRec> STAMP_MILL_TYPE = IRecipeType.register(Crossroads.MODID + ":stamp_mill");
	public static IRecipeType<OreCleanserRec> ORE_CLEANSER_TYPE = IRecipeType.register(Crossroads.MODID + ":ore_cleanser");
	public static IRecipeType<BeamExtractRec> BEAM_EXTRACT_TYPE = IRecipeType.register(Crossroads.MODID + ":beam_extract");
	public static IRecipeType<IceboxRec> COOLING_TYPE = IRecipeType.register(Crossroads.MODID + ":cooling");
	public static IRecipeType<DirtyWaterRec> DIRTY_WATER_TYPE = IRecipeType.register(Crossroads.MODID + ":dirty_water");
	public static IRecipeType<AlchemyRec> ALCHEMY_TYPE = IRecipeType.register(Crossroads.MODID + ":alchemy");
	public static IRecipeType<BlastFurnaceRec> BLAST_FURNACE_TYPE = IRecipeType.register(Crossroads.MODID + ":cr_blast_furnace");
	public static IRecipeType<FluidCoolingRec> FLUID_COOLING_TYPE = IRecipeType.register(Crossroads.MODID + ":fluid_cooling");
	public static IRecipeType<CrucibleRec> CRUCIBLE_TYPE = IRecipeType.register(Crossroads.MODID + ":crucible");
	public static IRecipeType<DetailedCrafterRec> DETAILED_TYPE = IRecipeType.register(Crossroads.MODID + ":detailed_crafter");

	//TODO every time recipes are loaded/reloaded, this value needs to be (re-)calculated
	public static int totalDirtyWaterWeight = 0;

	//TODO all recipes below this line need to be replaced with JSON

	/**
	 * Stores the fusion beam conversion recipes. 
	 */
	public static final PredicateMap<BlockState, BeamTransmute> fusionBeamRecipes = new PredicateMap<>();

	/**
	 * Stores the void-fusion beam conversion recipes. 
	 */
	public static final PredicateMap<BlockState, BeamTransmute> vFusionBeamRecipes = new PredicateMap<>();

}
