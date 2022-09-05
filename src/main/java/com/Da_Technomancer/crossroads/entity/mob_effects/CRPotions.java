package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;

public class CRPotions{

	public static final HashMap<String, MobEffect> toRegisterEffect = new HashMap<>(4);
	public static final HashMap<String, Potion> toRegisterPotion = new HashMap<>();

	//We assume any effect on a mob over this duration was originally a permanent effect; this is not a flawless method
	public static final int PERM_EFFECT_CUTOFF = Integer.MAX_VALUE / 4;

	public static final Sedation SEDATION_EFFECT = registerMobEffect("sedation", new Sedation());
	public static final Curative CURATIVE_EFFECT = registerMobEffect("curative", new Curative());
	public static final HealthPenalty HEALTH_PENALTY_EFFECT = registerMobEffect("health_penalty", new HealthPenalty());
	public static final Transient TRANSIENT_EFFECT = registerMobEffect("transient", new Transient());

	public static final Potion POTION_SEDATION = registerPotion("sedation", "sedation", new MobEffectInstance(SEDATION_EFFECT, 3600));
	public static final Potion POTION_SEDATION_LONG = registerPotion("long_sedation", "sedation", new MobEffectInstance(SEDATION_EFFECT, 9600));
	public static final Potion POTION_CURATIVE = registerPotion("curative", "curative", new MobEffectInstance(CURATIVE_EFFECT, 1));
	public static final Potion POTION_NAUSEA = registerPotion("nausea", "nausea", new MobEffectInstance(MobEffects.CONFUSION, 3600));
	public static final Potion POTION_NAUSEA_LONG = registerPotion("long_nausea", "nausea", new MobEffectInstance(MobEffects.CONFUSION, 9600));
	public static final Potion POTION_BLINDNESS = registerPotion("blindness", "blindness", new MobEffectInstance(MobEffects.BLINDNESS, 3600));
	public static final Potion POTION_BLINDNESS_LONG = registerPotion("long_blindness", "blindness", new MobEffectInstance(MobEffects.BLINDNESS, 9600));
	public static final Potion POTION_TRANSIENT = registerPotion("transient", "transient", new MobEffectInstance(TRANSIENT_EFFECT, 3600));
	public static final Potion POTION_TRANSIENT_LONG = registerPotion("long_transient", "transient", new MobEffectInstance(TRANSIENT_EFFECT, 9600));

	public static void init(){
		//No-op
	}

	private static <T extends MobEffect> T registerMobEffect(String regName, T effect){
		toRegisterEffect.put(regName, effect);
		return effect;
	}

	private static Potion registerPotion(String regName, String potionName, MobEffectInstance effectInstance){
		Potion potion = new Potion(potionName, effectInstance);
		toRegisterPotion.put(regName, potion);
		return potion;
	}

	public static void registerPotionRecipes(){
		//Add the recipes
		//Still not JSON-ed, so we do this through the Forge hook
		//Note: We can't use tags here, as tags are rebound with every world load, and this is currently called once per game initialization
		//This needs to be re-written if we want to use tags

		//Sedation potions
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)), Ingredient.of(new ItemStack(CRItems.mushroomDust)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION));
		//Extend sedation
		Ingredient redstoneIngredient = Ingredient.of(Items.REDSTONE);
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_SEDATION_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_SEDATION_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_SEDATION)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_SEDATION_LONG));
		//Curative potions
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)), Ingredient.of(new ItemStack(CRBlocks.medicinalMushroom)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_CURATIVE));
		//Nausea potions
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.MUNDANE)), Ingredient.of(new ItemStack(CRItems.mushroomDust)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_NAUSEA));
		//Extend nausea
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_NAUSEA)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_NAUSEA_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_NAUSEA)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_NAUSEA_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_NAUSEA)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_NAUSEA_LONG));
		//Blindness potions
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.THICK)), Ingredient.of(new ItemStack(CRItems.mushroomDust)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_BLINDNESS));
		//Extend blindness
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_BLINDNESS)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_BLINDNESS_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_BLINDNESS)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_BLINDNESS_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_BLINDNESS)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_BLINDNESS_LONG));
		//Transient
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)), Ingredient.of(new ItemStack(CRItems.soulCluster)), PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_TRANSIENT));
		//Extend transient
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_TRANSIENT)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.POTION), POTION_TRANSIENT_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_TRANSIENT)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), POTION_TRANSIENT_LONG));
		BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_TRANSIENT)), redstoneIngredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), POTION_TRANSIENT_LONG));

		//Filling empty bottles
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.ICE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
	}

	public static boolean canBePermanentEffect(MobEffectInstance effect){
		if(!effect.getEffect().isInstantenous()){
			//Confirm the effect isn't blacklisted
			ResourceLocation effectRegistryName = MiscUtil.getRegistryName(effect.getEffect(), ForgeRegistries.MOB_EFFECTS);
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
	public static boolean canBeAppliedPermanentlyToTarget(LivingEntity target, MobEffectInstance effect){
		if(canBePermanentEffect(effect)){
			for(MobEffectInstance active : target.getActiveEffects()){
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
	public static boolean applyAsPermanent(LivingEntity target, MobEffectInstance toApply){
		if(canBeAppliedPermanentlyToTarget(target, toApply)){
			//'Permanent' is actually maximum duration, which is ~3.4 years ingame
			target.addEffect(new MobEffectInstance(toApply.getEffect(), Integer.MAX_VALUE, toApply.getAmplifier(), toApply.isAmbient(), toApply.isVisible(), toApply.showIcon()));
			return true;
		}
		return false;
	}
}
