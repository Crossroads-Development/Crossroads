package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.crafting.recipes.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public final class CRRecipes{

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
	@ObjectHolder("centrifuge")
	public static IRecipeSerializer<CentrifugeRec> CENTRIFUGE_SERIAL = null;
	@ObjectHolder("alchemy")
	public static IRecipeSerializer<AlchemyRec> ALCHEMY_SERIAL = null;
	@ObjectHolder("cr_blast_furnace")
	public static IRecipeSerializer<BlastFurnaceRec> BLAST_FURNACE_SERIAL = null;
	@ObjectHolder("fluid_cooling")
	public static IRecipeSerializer<FluidCoolingRec> FLUID_COOLING_SERIAL = null;
	@ObjectHolder("crucible")
	public static IRecipeSerializer<CrucibleRec> CRUCIBLE_SERIAL = null;
	@ObjectHolder("detailed_crafter")
	public static IRecipeSerializer<DetailedCrafterRec> DETAILED_SERIAL = null;
	@ObjectHolder("beam_transmute")
	public static IRecipeSerializer<BeamTransmuteRec> BEAM_TRANSMUTE_SERIAL = null;
	@ObjectHolder("bobo")
	public static IRecipeSerializer<BoboRec> BOBO_SERIAL = null;
	@ObjectHolder("copshowium")
	public static IRecipeSerializer<CopshowiumRec> COPSHOWIUM_SERIAL = null;

	//Forge hasn't replaced the IRecipeType registry (yet). We use the vanilla registry as a result.
	public static IRecipeType<MillRec> MILL_TYPE = IRecipeType.register(Crossroads.MODID + ":mill");
	public static IRecipeType<StampMillRec> STAMP_MILL_TYPE = IRecipeType.register(Crossroads.MODID + ":stamp_mill");
	public static IRecipeType<OreCleanserRec> ORE_CLEANSER_TYPE = IRecipeType.register(Crossroads.MODID + ":ore_cleanser");
	public static IRecipeType<BeamExtractRec> BEAM_EXTRACT_TYPE = IRecipeType.register(Crossroads.MODID + ":beam_extract");
	public static IRecipeType<IceboxRec> COOLING_TYPE = IRecipeType.register(Crossroads.MODID + ":cooling");
	public static IRecipeType<CentrifugeRec> CENTRIFUGE_TYPE = IRecipeType.register(Crossroads.MODID + ":centrifuge");
	public static IRecipeType<AlchemyRec> ALCHEMY_TYPE = IRecipeType.register(Crossroads.MODID + ":alchemy");
	public static IRecipeType<BlastFurnaceRec> BLAST_FURNACE_TYPE = IRecipeType.register(Crossroads.MODID + ":cr_blast_furnace");
	public static IRecipeType<FluidCoolingRec> FLUID_COOLING_TYPE = IRecipeType.register(Crossroads.MODID + ":fluid_cooling");
	public static IRecipeType<CrucibleRec> CRUCIBLE_TYPE = IRecipeType.register(Crossroads.MODID + ":crucible");
	public static IRecipeType<DetailedCrafterRec> DETAILED_TYPE = IRecipeType.register(Crossroads.MODID + ":detailed_crafter");
	public static IRecipeType<BeamTransmuteRec> BEAM_TRANSMUTE_TYPE = IRecipeType.register(Crossroads.MODID + ":beam_transmute");
	public static IRecipeType<BoboRec> BOBO_TYPE = IRecipeType.register(Crossroads.MODID + ":bobo");
	public static IRecipeType<CopshowiumRec> COPSHOWIUM_TYPE = IRecipeType.register(Crossroads.MODID + ":copshowium");

}
