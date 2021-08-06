package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRNBTIngredient;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class CRPotions{

	//We assume any effect on a mob over this duration was originally a permanent effect; this is not a flawless method
	public static final int PERM_EFFECT_CUTOFF = Integer.MAX_VALUE / 4;

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

	public static boolean canBePermanentEffect(EffectInstance effect){
		if(!effect.getEffect().isInstantenous()){
			//Confirm the effect isn't blacklisted
			ResourceLocation effectRegistryName = effect.getEffect().getRegistryName();
			List<? extends String> blacklist = CRConfig.permanentEffectBlacklist.get();
			return blacklist.stream().noneMatch(entry -> new ResourceLocation(entry).equals(effectRegistryName));
		}
		return false;
	}

	/**
	 * Determines whether a potion effect can be applied permanently to an entity
	 * If the effect is already applied permanently, this will return false
	 * @param target The target entity to apply the effect to
	 * @param effect The effect to be applied
	 * @return Whether a new application of the effect can be applied to the target
	 */
	public static boolean canBeAppliedPermanentlyToTarget(LivingEntity target, EffectInstance effect){
		if(canBePermanentEffect(effect)){
			for(EffectInstance active : target.getActiveEffects()){
				if(active.getEffect() == effect.getEffect() && active.getAmplifier() >= effect.getAmplifier() && active.getDuration() > PERM_EFFECT_CUTOFF){
					return false;//This effect already exists in permanent form in an equal or stronger intensity
				}
			}
			return true;//This is a valid effect type and it is not already applied
		}
		return false;//Invalid effect type
	}

	/**
	 * Applies a non-instantaneous potion effect to be permanent
	 * Instantaneous effects will have no result
	 * @param target The entity to apply it to
	 * @param toApply The effect to apply, but in a permanent form. The passed argument will not be modified
	 * @return Whether this effect was applied
	 */
	public static boolean applyAsPermanent(LivingEntity target, EffectInstance toApply){
		if(canBeAppliedPermanentlyToTarget(target, toApply)){
			//'Permanent' is actually maximum duration, which is ~3.4 years ingame
			target.addEffect(new EffectInstance(toApply.getEffect(), Integer.MAX_VALUE, toApply.getAmplifier(), toApply.isAmbient(), toApply.isVisible(), toApply.showIcon()));
			return true;
		}
		return false;
	}
}
