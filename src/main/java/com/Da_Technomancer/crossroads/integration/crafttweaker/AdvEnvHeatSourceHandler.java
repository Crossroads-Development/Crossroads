package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.crossroads.AdvEnvHeatSource")
public class AdvEnvHeatSourceHandler{

	/**
	 * This method adds a mapping between a block and heat production. If a mapping already exists for that block, the previous one is replaced.
	 * @param input The required block for conversion. Metadata is ignored
	 * @param created The produced block. Can be null to produce air. Note that the default state will be produced (so don't make this something weird like furnaces)
	 * @param change The temperature change. Positive values raise temperature, negative values lower temperature.
	 * @param limit The temperature limit which this recipe does not function beyond. If change is positive, this is an upper bound. If change is negative, this is a lower bound.
	 */
	@ZenMethod
	public static void addRecipe(IBlock input, IBlock created, double change, double limit){
		CraftTweakerAPI.apply(new EnvHeatSourceHandler.Add(CraftTweakerMC.getBlock(input), CraftTweakerMC.getBlock(created), change, limit));
	}

	/**
	 * This method adds a mapping between a block and heat production. If a mapping already exists for that block, the previous one is replaced.
	 * @param input The required blockstate for conversion. Metadata is ignored. Specified in a script with block: before the modid in the registry name
	 * @param created The produced blockstate. Can be null to produce air. Note that the default state will be produced (so don't make this something weird like furnaces). Specified in a script with block: before the modid in the registry name
	 * @param change The temperature change. Positive values raise temperature, negative values lower temperature.
	 * @param limit The temperature limit which this recipe does not function beyond. If change is positive, this is an upper bound. If change is negative, this is a lower bound.
	 */
	@ZenMethod
	public static void addRecipe(ICTBlockState input, ICTBlockState created, double change, double limit){
		CraftTweakerAPI.apply(new EnvHeatSourceHandler.Add(CraftTweakerMC.getBlock(input.getBlock()), CraftTweakerMC.getBlock(created.getBlock()), change, limit));
	}

	/**
	 * This method adds a mapping between a block and heat production. If a mapping already exists for that block, the previous one is replaced.
	 * @param input The required blockstate for conversion. Metadata is ignored.
	 * @param created The produced blockstate. Can be null to produce air. Note that the default state will be produced (so don't make this something weird like furnaces). Specified in a script with block: before the modid in the registry name
	 * @param change The temperature change. Positive values raise temperature, negative values lower temperature.
	 * @param limit The temperature limit which this recipe does not function beyond. If change is positive, this is an upper bound. If change is negative, this is a lower bound.
	 */
	@ZenMethod
	public static void addRecipe(IBlock input, ICTBlockState created, double change, double limit){
		CraftTweakerAPI.apply(new EnvHeatSourceHandler.Add(CraftTweakerMC.getBlock(input), CraftTweakerMC.getBlock(created.getBlock()), change, limit));
	}

	/**
	 * This method adds a mapping between a block and heat production. If a mapping already exists for that block, the previous one is replaced.
	 * @param input The required blockstate for conversion. Metadata is ignored. Specified in a script with block: before the modid in the registry name
	 * @param created The produced blockstate. Can be null to produce air. Note that the default state will be produced (so don't make this something weird like furnaces).
	 * @param change The temperature change. Positive values raise temperature, negative values lower temperature.
	 * @param limit The temperature limit which this recipe does not function beyond. If change is positive, this is an upper bound. If change is negative, this is a lower bound.
	 */
	@ZenMethod
	public static void addRecipe(ICTBlockState input, IBlock created, double change, double limit){
		CraftTweakerAPI.apply(new EnvHeatSourceHandler.Add(CraftTweakerMC.getBlock(input.getBlock()), CraftTweakerMC.getBlock(created), change, limit));
	}

	/**
	 * This method removes a mapping between a block and the heat production.
	 * @param input The input blockstate
	 */
	@ZenMethod
	public static void removeRecipe(IBlock input){
		CraftTweakerAPI.apply(new EnvHeatSourceHandler.Remove(CraftTweakerMC.getBlock(input)));
	}

	/**
	 * This method removes a mapping between a block and the heat production.
	 * @param input The input blockstate. Specified in a script with block: before the modid in the registry name
	 */
	@ZenMethod
	public static void removeRecipe(ICTBlockState input){
		CraftTweakerAPI.apply(new EnvHeatSourceHandler.Remove(CraftTweakerMC.getBlock(input.getBlock())));
	}
}
