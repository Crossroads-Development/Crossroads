package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.crossroads.AdvFusionBeam")
public class AdvFusionBeamHandler{

	/**
	 * This method adds a mapping between a blockstate and the created blockstate with the Fusion element beam. If a mapping already exists for that exact Blockstate, the previous one is replaced. 
	 * @param input The required blockstate for conversion. Wildcard metadata is allowed.
	 * @param minPower The minimum beam power (can be 0)
	 * @param output The created blockstate. DO NOT USE WILDCARD META.
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void addRecipe(IItemStack input, int minPower, IItemStack output, boolean voi){
		CraftTweakerAPI.apply(new FusionBeamHandler.Add(CraftTweakerMC.getBlock(input), CraftTweakerMC.getItemStack(input).getMetadata(), minPower, CraftTweakerMC.getBlock(output), CraftTweakerMC.getItemStack(output).getMetadata(), voi));
	}

	/**
	 * This method adds a mapping between a blockstate and the created blockstate with the Fusion element beam. If a mapping already exists for that exact Blockstate, the previous one is replaced.
	 * @param input The required blockstate for conversion, using contenttweaker blockstates
	 * @param ignoreMeta Whether to ignore metadata on the input
	 * @param minPower The minimum beam power (can be 0)
	 * @param output The created blockstate, using contenttweaker blockstates
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void addRecipe(ICTBlockState input, boolean ignoreMeta, int minPower, ICTBlockState output, boolean voi){
		CraftTweakerAPI.apply(new FusionBeamHandler.Add(input.getInternal(), ignoreMeta, minPower, output.getInternal(), voi));
	}

	/**
	 * This method adds a mapping between a blockstate and the created blockstate with the Fusion element beam. If a mapping already exists for that exact Blockstate, the previous one is replaced.
	 * @param input The required blockstate for conversion. Wildcard metadata is allowed.
	 * @param minPower The minimum beam power (can be 0)
	 * @param output The created blockstate, using contenttweaker blockstates
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	@SuppressWarnings("deprecation")
	public static void addRecipe(IItemStack input, int minPower, ICTBlockState output, boolean voi){
		CraftTweakerAPI.apply(new FusionBeamHandler.Add(CraftTweakerMC.getBlock(input).getStateFromMeta(input.getMetadata() == OreDictionary.WILDCARD_VALUE ? 0 : input.getMetadata()), input.getMetadata() == OreDictionary.WILDCARD_VALUE, minPower, output.getInternal(), voi));
	}

	/**
	 * This method adds a mapping between a blockstate and the created blockstate with the Fusion element beam. If a mapping already exists for that exact Blockstate, the previous one is replaced.
	 * @param input The required blockstate for conversion, using contenttweaker blockstates
	 * @param ignoreMeta Whether to ignore metadata on the input
	 * @param minPower The minimum beam power (can be 0)
	 * @param output The created blockstate. DO NOT USE WILDCARD META.
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	@SuppressWarnings("deprecation")
	public static void addRecipe(ICTBlockState input, boolean ignoreMeta, int minPower, IItemStack output, boolean voi){
		CraftTweakerAPI.apply(new FusionBeamHandler.Add(input.getInternal(), ignoreMeta, minPower, CraftTweakerMC.getBlock(output).getStateFromMeta(output.getMetadata()), voi));
	}

	/**
	 * This method removes a mapping between a blockstate and the created blockstate with a (void) fusion beam. 
	 * @param input The input blockstate
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void removeRecipe(IItemStack input, boolean voi){
		CraftTweakerAPI.apply(new FusionBeamHandler.Remove(CraftTweakerMC.getBlock(input), CraftTweakerMC.getItemStack(input).getMetadata(), voi));
	}

	/**
	 * This method removes a mapping between a blockstate and the created blockstate with a (void) fusion beam.
	 * @param input The input blockstate, using contenttweaker blockstates
	 * @param ignoreMeta Whether the metadata on the input is ignored
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void removeRecipe(ICTBlockState input, boolean ignoreMeta, boolean voi){
		CraftTweakerAPI.apply(new FusionBeamHandler.Remove(input.getInternal(), ignoreMeta, voi));

	}
}
