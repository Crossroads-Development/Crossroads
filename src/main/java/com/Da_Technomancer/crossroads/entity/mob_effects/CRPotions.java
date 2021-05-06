package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRNBTIngredient;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class CRPotions{

	public static final Sedation SEDATION_EFFECT = new Sedation();
	public static final Curative CURATIVE_EFFECT = new Curative();
	public static final HealthPenalty HEALTH_PENALTY_EFFECT = new HealthPenalty();

	public static final Potion POTION_SEDATION = new Potion("sedation", new EffectInstance(SEDATION_EFFECT, 3600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "sedation"));
	public static final Potion POTION_SEDATION_LONG = new Potion("sedation", new EffectInstance(SEDATION_EFFECT, 9600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "long_sedation"));
	public static final Potion POTION_CURATIVE = new Potion("curative", new EffectInstance(CURATIVE_EFFECT, 1)).setRegistryName(new ResourceLocation(Crossroads.MODID, "curative"));
	public static final Potion POTION_NAUSEA = new Potion("nausea", new EffectInstance(Effects.CONFUSION, 3600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "nausea"));
	public static final Potion POTION_NAUSEA_LONG = new Potion("nausea", new EffectInstance(Effects.CONFUSION, 9600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "long_nausea"));
	public static final Potion POTION_BLINDNESS = new Potion("blindness", new EffectInstance(Effects.BLINDNESS, 3600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "blindness"));
	public static final Potion POTION_BLINDNESS_LONG = new Potion("blindness", new EffectInstance(Effects.BLINDNESS, 9600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "long_blindness"));

	public static void registerEffects(IForgeRegistry<Effect> reg){
		reg.register(SEDATION_EFFECT);
		reg.register(CURATIVE_EFFECT);
		reg.register(HEALTH_PENALTY_EFFECT);
	}

	public static void registerPotions(IForgeRegistry<Potion> reg){
		reg.register(POTION_SEDATION);
		reg.register(POTION_SEDATION_LONG);
		reg.register(POTION_CURATIVE);
		reg.register(POTION_NAUSEA);
		reg.register(POTION_NAUSEA_LONG);
		reg.register(POTION_BLINDNESS);
		reg.register(POTION_BLINDNESS_LONG);

		registerPotionRecipes();
	}

	public static void registerPotionRecipes(){
		//Add the recipes
		//Still not JSON-ed, so we do this through the Forge hook
		//Note: We can't use tags here, as tags are rebound with every world load, and this is currently called once per game initialization
		//This needs to be re-written if we want to use tags

		//Sedation potions
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)), Ingredient.of(new ItemStack(CRItems.mushroomDust)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION));
		//Extend sedation
		Ingredient redstoneIngredient = Ingredient.of(Items.REDSTONE);
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_SEDATION_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_SEDATION_LONG));
		//Curative potions
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)), Ingredient.of(new ItemStack(CRBlocks.medicinalMushroom)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_CURATIVE));
		//Nausea potions
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.MUNDANE)), Ingredient.of(new ItemStack(CRItems.mushroomDust)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_NAUSEA));
		//Extend nausea
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_NAUSEA)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_NAUSEA_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_NAUSEA)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_NAUSEA_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_NAUSEA)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_NAUSEA_LONG));
		//Blindness potions
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.THICK)), Ingredient.of(new ItemStack(CRItems.mushroomDust)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_BLINDNESS));
		//Extend blindness
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_BLINDNESS)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_BLINDNESS_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_BLINDNESS)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_BLINDNESS_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_BLINDNESS)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_BLINDNESS_LONG));
		//Filling empty bottles
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.ICE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
	}
}
