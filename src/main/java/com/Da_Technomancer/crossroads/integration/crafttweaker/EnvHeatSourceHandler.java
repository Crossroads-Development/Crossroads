package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.crossroads.EnvHeatSource")
public class EnvHeatSourceHandler{

	/**
	 * This method adds a mapping between a block and heat production. If a mapping already exists for that block, the previous one is replaced.
	 * @param input The required block for conversion. Metadata is ignored
	 * @param created The produced block. Can be null to produce air. Note that the default state will be produced (so don't make this something weird like furnaces)
	 * @param change The temperature change. Positive values raise temperature, negative values lower temperature.
	 * @param limit The temperature limit which this recipe does not function beyond. If change is positive, this is an upper bound. If change is negative, this is a lower bound.
	 */
	@ZenMethod
	public static void addRecipe(IBlock input, IBlock created, double change, double limit){
		CraftTweakerAPI.apply(new Add(CraftTweakerMC.getBlock(input), CraftTweakerMC.getBlock(created), change, limit));
	}

	/**
	 * This method removes a mapping between a block and the heat production.
	 * @param input The input blockstate
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void removeRecipe(IBlock input){
		CraftTweakerAPI.apply(new Remove(CraftTweakerMC.getBlock(input)));
	}

	private static class Add implements IAction{

		private final Block input;
		private final IBlockState created;
		private final double change;
		private final double limit;

		private Add(Block input, Block created, double change, double limit){
			this.input = input;
			this.created = created.getDefaultState();
			this.change = change;
			this.limit = limit;
		}

		@Override
		public void apply(){
			RecipeHolder.envirHeatSource.put(input, Pair.of(true, Triple.of(created, change, limit)));
		}

		@Override
		public String describe(){
			return "Adding Environmental Heat recipe for " + input.getRegistryName();
		}
	}

	private static class Remove implements IAction{

		private final Block input;

		private Remove(Block input){
			this.input = input;
		}

		@Override
		public void apply(){
			RecipeHolder.envirHeatSource.remove(input);
		}

		@Override
		public String describe(){
			return "Removing Environmental Heat recipe for " + input.getRegistryName();
		}
	}
}
