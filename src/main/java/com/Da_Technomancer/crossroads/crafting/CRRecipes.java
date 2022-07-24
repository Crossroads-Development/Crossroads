package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.HashMap;

public final class CRRecipes{
	
	public static RecipeSerializer<?> STAMP_MILL_SERIAL = null;
	public static RecipeSerializer<?> MILL_SERIAL = null;
	public static RecipeSerializer<?> ORE_CLEANSER_SERIAL = null;
	public static RecipeSerializer<?> BEAM_EXTRACT_SERIAL = null;
	public static RecipeSerializer<?> COOLING_SERIAL = null;
	public static RecipeSerializer<?> CENTRIFUGE_SERIAL = null;
	public static RecipeSerializer<?> ALCHEMY_SERIAL = null;
	public static RecipeSerializer<?> BLAST_FURNACE_SERIAL = null;
	public static RecipeSerializer<?> FLUID_COOLING_SERIAL = null;
	public static RecipeSerializer<?> CRUCIBLE_SERIAL = null;
	public static RecipeSerializer<?> DETAILED_SERIAL = null;
	public static RecipeSerializer<?> BEAM_TRANSMUTE_SERIAL = null;
	public static RecipeSerializer<?> BOBO_SERIAL = null;
	public static RecipeSerializer<?> COPSHOWIUM_SERIAL = null;
	public static RecipeSerializer<?> REAGENT_SERIAL = null;
	public static RecipeSerializer<?> FORMULATION_VAT_SERIAL = null;
	public static RecipeSerializer<?> BEAM_LENS_SERIAL = null;
	public static RecipeSerializer<?> EMBRYO_LAB_MORPH_SERIAL = null;

	public static final RecipeType<MillRec> MILL_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "mill"));
	public static final RecipeType<StampMillRec> STAMP_MILL_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "stamp_mill"));
	public static final RecipeType<OreCleanserRec> ORE_CLEANSER_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "ore_cleanser"));
	public static final RecipeType<BeamExtractRec> BEAM_EXTRACT_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "beam_extract"));
	public static final RecipeType<IceboxRec> COOLING_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "cooling"));
	public static final RecipeType<CentrifugeRec> CENTRIFUGE_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "centrifuge"));
	public static final RecipeType<AlchemyRec> ALCHEMY_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "alchemy"));
	public static final RecipeType<BlastFurnaceRec> BLAST_FURNACE_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "cr_blast_furnace"));
	public static final RecipeType<FluidCoolingRec> FLUID_COOLING_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "fluid_cooling"));
	public static final RecipeType<CrucibleRec> CRUCIBLE_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "crucible"));
	public static final RecipeType<DetailedCrafterRec> DETAILED_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "detailed_crafter"));
	public static final RecipeType<BeamTransmuteRec> BEAM_TRANSMUTE_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "beam_transmute"));
	public static final RecipeType<BoboRec> BOBO_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "bobo"));
	public static final RecipeType<CopshowiumRec> COPSHOWIUM_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "copshowium"));
	public static final RecipeType<ReagentRec> REAGENT_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "reagents"));
	public static final RecipeType<FormulationVatRec> FORMULATION_VAT_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "formulation_vat"));
	public static final RecipeType<BeamLensRec> BEAM_LENS_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "beam_lens"));
	public static final RecipeType<EmbryoLabMorphRec> EMBRYO_LAB_MORPH_TYPE = RecipeType.simple(new ResourceLocation(Crossroads.MODID, "embryo_lab_morph"));

	private static boolean hasInit = false;

	public static void init(){
		if(hasInit){
			return;
		}
		hasInit = true;

		toRegisterType.put("mill", MILL_TYPE);
		toRegisterType.put("stamp_mill", STAMP_MILL_TYPE);
		toRegisterType.put("ore_cleanser", ORE_CLEANSER_TYPE);
		toRegisterType.put("beam_extract", BEAM_EXTRACT_TYPE);
		toRegisterType.put("cooling", COOLING_TYPE);
		toRegisterType.put("centrifuge", CENTRIFUGE_TYPE);
		toRegisterType.put("alchemy", ALCHEMY_TYPE);
		toRegisterType.put("cr_blast_furnace", BLAST_FURNACE_TYPE);
		toRegisterType.put("fluid_cooling", FLUID_COOLING_TYPE);
		toRegisterType.put("crucible", CRUCIBLE_TYPE);
		toRegisterType.put("detailed_crafter", DETAILED_TYPE);
		toRegisterType.put("beam_transmute", BEAM_TRANSMUTE_TYPE);
		toRegisterType.put("bobo", BOBO_TYPE);
		toRegisterType.put("copshowium", COPSHOWIUM_TYPE);
		toRegisterType.put("reagents", REAGENT_TYPE);
		toRegisterType.put("formulation_vat", FORMULATION_VAT_TYPE);
		toRegisterType.put("beam_lens", BEAM_LENS_TYPE);
		toRegisterType.put("embryo_lab_morpth", EMBRYO_LAB_MORPH_TYPE);

		STAMP_MILL_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "stamp_mill", new SingleIngrRecipe.SingleRecipeSerializer<>(StampMillRec::new));
		MILL_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "mill", new MillRec.Serializer());
		ORE_CLEANSER_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "ore_cleanser", new SingleIngrRecipe.SingleRecipeSerializer<>(OreCleanserRec::new));
		BEAM_EXTRACT_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "beam_extract", new BeamExtractRec.Serializer());
		COOLING_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "cooling", new IceboxRec.Serializer());
		CENTRIFUGE_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "centrifuge", new CentrifugeRec.Serializer());
		ALCHEMY_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "alchemy", new AlchemyRec.Serializer());
		BLAST_FURNACE_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "cr_blast_furnace", new BlastFurnaceRec.Serializer());
		FLUID_COOLING_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "fluid_cooling", new FluidCoolingRec.Serializer());
		CRUCIBLE_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "crucible", new CrucibleRec.Serializer());
		DETAILED_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "detailed_crafter", new DetailedCrafterRec.Serializer());
		BEAM_TRANSMUTE_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "beam_transmute", new BeamTransmuteRec.Serializer());
		BOBO_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "bobo", new BoboRec.Serializer());
		COPSHOWIUM_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "copshowium", new CopshowiumRec.Serializer());
		REAGENT_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "reagents", new ReagentRec.Serializer());
		FORMULATION_VAT_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "formulation_vat", new FormulationVatRec.Serializer());
		BEAM_LENS_SERIAL = MiscUtil.putReturn(toRegisterSerializer, ("beam_lens"), new BeamLensRec.Serializer());
		EMBRYO_LAB_MORPH_SERIAL = MiscUtil.putReturn(toRegisterSerializer, "embryo_lab_morph", new EmbryoLabMorphRec.Serializer());
	}
	
	public static final HashMap<String, RecipeSerializer<?>> toRegisterSerializer = new HashMap<>();
	public static final HashMap<String, RecipeType<?>> toRegisterType = new HashMap<>();
}
