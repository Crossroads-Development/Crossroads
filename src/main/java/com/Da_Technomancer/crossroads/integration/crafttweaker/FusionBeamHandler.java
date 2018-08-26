package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.items.crafting.BeamTransmute;
import com.Da_Technomancer.crossroads.items.crafting.BlockRecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Integration for the (Void) Fusion Beam */
@ZenClass("mods.crossroads.FusionBeam")
public class FusionBeamHandler{

	/**
	 * This method adds a mapping between a blockstate and the created blockstate with the Fusion element beam. If a mapping already exists for that exact Blockstate, the previous one is replaced. 
	 * @param input The required blockstate for conversion. Wildcard metadata is allowed.
	 * @param minPower The minimum beam power (can be 0)
	 * @param output The created blockstate. DO NOT USE WILDCARD META.
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void addRecipe(IItemStack input, int minPower, IItemStack output, boolean voi){
		CraftTweakerAPI.apply(new Add(CraftTweakerMC.getBlock(input), CraftTweakerMC.getItemStack(input).getMetadata(), minPower, CraftTweakerMC.getBlock(output), CraftTweakerMC.getItemStack(output).getMetadata(), voi));
	}
	
	/**
	 * This method removes a mapping between a blockstate and the created blockstate with a (void) fusion beam. 
	 * @param input The input blockstate
	 * @param voi Whether this is for the void-fusion beam or the normal fusion beam
	 */
	@ZenMethod
	public static void removeRecipe(IItemStack input, boolean voi){
		CraftTweakerAPI.apply(new Remove(CraftTweakerMC.getBlock(input), CraftTweakerMC.getItemStack(input).getMetadata(), voi));
	}

	protected static class Add implements IAction{

		private final BlockRecipePredicate input;
		private final int minPower;
		private final IBlockState output;
		private final boolean voi;
		
		@SuppressWarnings("deprecation")
		protected Add(Block input, int inputMeta, int minPower, Block output, int outMeta, boolean voi){
			this.input = new BlockRecipePredicate(inputMeta == OreDictionary.WILDCARD_VALUE ? input.getDefaultState() : input.getStateFromMeta(inputMeta), inputMeta == OreDictionary.WILDCARD_VALUE);
			this.minPower = minPower;
			this.output = output.getStateFromMeta(outMeta);
			this.voi = voi;
		}

		protected Add(IBlockState input, boolean ignoreMeta, int minPower, IBlockState output, boolean voi){
			this.input = new BlockRecipePredicate(input, ignoreMeta);
			this.minPower = minPower;
			this.output = output;
			this.voi = voi;
		}
		
		@Override
		public void apply(){
			if(voi){
				RecipeHolder.vFusionBeamRecipes.put(input, new BeamTransmute(output, minPower));
			}else{
				RecipeHolder.fusionBeamRecipes.put(input, new BeamTransmute(output, minPower));
			}
		}

		@Override
		public String describe(){
			return "Adding " + (voi ? "Void " : "") + "Fusion Beam recipe for " + input.toString();
		}	
	}
	
	protected static class Remove implements IAction{

		private final BlockRecipePredicate input;
		private final boolean voi;

		@SuppressWarnings("deprecation")
		protected Remove(Block input, int inputMeta, boolean voi){
			this.input = new BlockRecipePredicate(inputMeta == OreDictionary.WILDCARD_VALUE ? input.getDefaultState() : input.getStateFromMeta(inputMeta), inputMeta == OreDictionary.WILDCARD_VALUE);
			this.voi = voi;
		}

		protected Remove(IBlockState input, boolean ignoreMeta, boolean voi){
			this.input = new BlockRecipePredicate(input, ignoreMeta);
			this.voi = voi;
		}
		
		@Override
		public void apply(){
			if(voi){
				RecipeHolder.vFusionBeamRecipes.remove(input);
			}else{
				RecipeHolder.fusionBeamRecipes.remove(input);
			}
		}

		@Override
		public String describe(){
			return "Removing " + (voi ? "Void " : "") + "Fusion Beam recipe for " + input.toString();
		}
	}
}
