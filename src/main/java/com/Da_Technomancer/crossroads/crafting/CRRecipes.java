package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.recipes.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public final class CRRecipes{

	//The serializers are registered in Crossroads::registerRecipeSerializers
	@ObjectHolder("stamp_mill")
	public static RecipeSerializer<StampMillRec> STAMP_MILL_SERIAL = null;
	@ObjectHolder("mill")
	public static RecipeSerializer<MillRec> MILL_SERIAL = null;
	@ObjectHolder("ore_cleanser")
	public static RecipeSerializer<OreCleanserRec> ORE_CLEANSER_SERIAL = null;
	@ObjectHolder("beam_extract")
	public static RecipeSerializer<BeamExtractRec> BEAM_EXTRACT_SERIAL = null;
	@ObjectHolder("cooling")
	public static RecipeSerializer<IceboxRec> COOLING_SERIAL = null;
	@ObjectHolder("centrifuge")
	public static RecipeSerializer<CentrifugeRec> CENTRIFUGE_SERIAL = null;
	@ObjectHolder("alchemy")
	public static RecipeSerializer<AlchemyRec> ALCHEMY_SERIAL = null;
	@ObjectHolder("cr_blast_furnace")
	public static RecipeSerializer<BlastFurnaceRec> BLAST_FURNACE_SERIAL = null;
	@ObjectHolder("fluid_cooling")
	public static RecipeSerializer<FluidCoolingRec> FLUID_COOLING_SERIAL = null;
	@ObjectHolder("crucible")
	public static RecipeSerializer<CrucibleRec> CRUCIBLE_SERIAL = null;
	@ObjectHolder("detailed_crafter")
	public static RecipeSerializer<DetailedCrafterRec> DETAILED_SERIAL = null;
	@ObjectHolder("beam_transmute")
	public static RecipeSerializer<BeamTransmuteRec> BEAM_TRANSMUTE_SERIAL = null;
	@ObjectHolder("bobo")
	public static RecipeSerializer<BoboRec> BOBO_SERIAL = null;
	@ObjectHolder("copshowium")
	public static RecipeSerializer<CopshowiumRec> COPSHOWIUM_SERIAL = null;
	@ObjectHolder("reagents")
	public static RecipeSerializer<ReagentRec> REAGENT_SERIAL = null;
	@ObjectHolder("formulation_vat")
	public static RecipeSerializer<FormulationVatRec> FORMULATION_VAT_SERIAL = null;
	@ObjectHolder("beam_lens")
	public static RecipeSerializer<BeamLensRec> BEAM_LENS_SERIAL = null;
	@ObjectHolder("embryo_lab_morph")
	public static RecipeSerializer<EmbryoLabMorphRec> EMBRYO_LAB_MORPH_SERIAL = null;

	//Forge hasn't replaced the IRecipeType registry (yet). We use the vanilla registry as a result.
	public static RecipeType<MillRec> MILL_TYPE = RecipeType.register(Crossroads.MODID + ":mill");
	public static RecipeType<StampMillRec> STAMP_MILL_TYPE = RecipeType.register(Crossroads.MODID + ":stamp_mill");
	public static RecipeType<OreCleanserRec> ORE_CLEANSER_TYPE = RecipeType.register(Crossroads.MODID + ":ore_cleanser");
	public static RecipeType<BeamExtractRec> BEAM_EXTRACT_TYPE = RecipeType.register(Crossroads.MODID + ":beam_extract");
	public static RecipeType<IceboxRec> COOLING_TYPE = RecipeType.register(Crossroads.MODID + ":cooling");
	public static RecipeType<CentrifugeRec> CENTRIFUGE_TYPE = RecipeType.register(Crossroads.MODID + ":centrifuge");
	public static RecipeType<AlchemyRec> ALCHEMY_TYPE = RecipeType.register(Crossroads.MODID + ":alchemy");
	public static RecipeType<BlastFurnaceRec> BLAST_FURNACE_TYPE = RecipeType.register(Crossroads.MODID + ":cr_blast_furnace");
	public static RecipeType<FluidCoolingRec> FLUID_COOLING_TYPE = RecipeType.register(Crossroads.MODID + ":fluid_cooling");
	public static RecipeType<CrucibleRec> CRUCIBLE_TYPE = RecipeType.register(Crossroads.MODID + ":crucible");
	public static RecipeType<DetailedCrafterRec> DETAILED_TYPE = RecipeType.register(Crossroads.MODID + ":detailed_crafter");
	public static RecipeType<BeamTransmuteRec> BEAM_TRANSMUTE_TYPE = RecipeType.register(Crossroads.MODID + ":beam_transmute");
	public static RecipeType<BoboRec> BOBO_TYPE = RecipeType.register(Crossroads.MODID + ":bobo");
	public static RecipeType<CopshowiumRec> COPSHOWIUM_TYPE = RecipeType.register(Crossroads.MODID + ":copshowium");
	public static RecipeType<ReagentRec> REAGENT_TYPE = RecipeType.register(Crossroads.MODID + ":reagents");
	public static RecipeType<FormulationVatRec> FORMULATION_VAT_TYPE = RecipeType.register(Crossroads.MODID + ":formulation_vat");
	public static RecipeType<BeamLensRec> BEAM_LENS_TYPE = RecipeType.register(Crossroads.MODID + ":beam_lens");
	public static RecipeType<EmbryoLabMorphRec> EMBRYO_LAB_MORPH_TYPE = RecipeType.register(Crossroads.MODID + ":embryo_lab_morph");
}
