package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRNBTIngredient;
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

	public static final Potion POTION_SEDATION = new Potion("sedation", new EffectInstance(SEDATION_EFFECT, 3600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "sedation"));
	public static final Potion POTION_SEDATION_LONG = new Potion("sedation", new EffectInstance(SEDATION_EFFECT, 9600)).setRegistryName(new ResourceLocation(Crossroads.MODID, "long_sedation"));

	public static void registerEffects(IForgeRegistry<Effect> reg){
		reg.register(SEDATION_EFFECT);
	}

	public static void registerPotions(IForgeRegistry<Potion> reg){
		reg.register(POTION_SEDATION);
		reg.register(POTION_SEDATION_LONG);

		registerPotionRecipes();
	}

	public static void registerPotionRecipes(){
		//Add the recipes
		//Still not JSON-ed, so we do this through the Forge hook
		//Note: We can't use tags here, as tags are rebound with every world load, and this is currently called once per game initialization
		//This needs to be re-written if we want to use tags

		//Sedation potions
		//TODO
		//Extend sedation
		Ingredient redstoneIngredient = Ingredient.of(Items.REDSTONE);
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_SEDATION_LONG));
		BrewingRecipeRegistry.addRecipe(new CRNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_SEDATION_LONG));
		//Filling empty bottles
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.ICE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
	}
}
