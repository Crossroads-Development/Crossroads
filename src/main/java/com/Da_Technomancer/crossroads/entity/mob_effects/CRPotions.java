package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import java.util.HashMap;
import java.util.List;

public class CRPotions{

	public static final HashMap<String, MobEffect> toRegisterEffect = new HashMap<>(4);
	public static final HashMap<String, Potion> toRegisterPotion = new HashMap<>();

	//We assume any effect on a mob over this duration was originally a permanent effect; this is not a flawless method
	public static final int PERM_EFFECT_CUTOFF = Integer.MAX_VALUE / 4;

	public static final Holder<MobEffect> SEDATION_EFFECT = registerMobEffect("sedation", new Sedation());
	public static final Holder<MobEffect> CURATIVE_EFFECT = registerMobEffect("curative", new Curative());
	public static final Holder<MobEffect> HEALTH_PENALTY_EFFECT = registerMobEffect("health_penalty", new HealthPenalty());
	public static final Holder<MobEffect> TRANSIENT_EFFECT = registerMobEffect("transient", new Transient());

	public static final Holder<Potion> POTION_SEDATION = registerPotion("sedation", "sedation", new MobEffectInstance(SEDATION_EFFECT, 3600));
	public static final Holder<Potion> POTION_SEDATION_LONG = registerPotion("long_sedation", "sedation", new MobEffectInstance(SEDATION_EFFECT, 9600));
	public static final Holder<Potion> POTION_CURATIVE = registerPotion("curative", "curative", new MobEffectInstance(CURATIVE_EFFECT, 1));
	public static final Holder<Potion> POTION_NAUSEA = registerPotion("nausea", "nausea", new MobEffectInstance(MobEffects.CONFUSION, 3600));
	public static final Holder<Potion> POTION_NAUSEA_LONG = registerPotion("long_nausea", "nausea", new MobEffectInstance(MobEffects.CONFUSION, 9600));
	public static final Holder<Potion> POTION_BLINDNESS = registerPotion("blindness", "blindness", new MobEffectInstance(MobEffects.BLINDNESS, 3600));
	public static final Holder<Potion> POTION_BLINDNESS_LONG = registerPotion("long_blindness", "blindness", new MobEffectInstance(MobEffects.BLINDNESS, 9600));
	public static final Holder<Potion> POTION_TRANSIENT = registerPotion("transient", "transient", new MobEffectInstance(TRANSIENT_EFFECT, 3600));
	public static final Holder<Potion> POTION_TRANSIENT_LONG = registerPotion("long_transient", "transient", new MobEffectInstance(TRANSIENT_EFFECT, 9600));

	public static void init(){
		//No-op
	}

	private static <T extends MobEffect> Holder<T> registerMobEffect(String regName, T effect){
		toRegisterEffect.put(regName, effect);
		return Holder.direct(effect);
	}

	private static Holder<Potion> registerPotion(String regName, String potionName, MobEffectInstance effectInstance){
		Potion potion = new Potion(potionName, effectInstance);
		toRegisterPotion.put(regName, potion);
		return Holder.direct(potion);
	}

	public static void registerPotionRecipes(RegisterBrewingRecipesEvent e){
		//Add the recipes
		//Still not JSON-ed, so we do this through the Forge hook
		//Note: We can't use tags here, as tags are rebound with every world load, and this is currently called once per game initialization
		//This needs to be re-written if we want to use tags

		PotionBrewing.Builder builder = e.getBuilder();

		//Sedation potions
		builder.addStartMix(CRItems.mushroomDust, POTION_SEDATION);
		//Extend sedation
		builder.addMix(POTION_SEDATION, Items.REDSTONE, POTION_SEDATION_LONG);
		//Curative potions
		builder.addStartMix(CRBlocks.medicinalMushroom.asItem(), POTION_CURATIVE);
		//Nausea potions
		builder.addMix(Potions.MUNDANE, CRItems.mushroomDust, POTION_NAUSEA);
		//Extend nausea
		builder.addMix(POTION_NAUSEA, Items.REDSTONE, POTION_NAUSEA_LONG);
		//Blindness potions
		builder.addMix(Potions.THICK, CRItems.mushroomDust, POTION_BLINDNESS);
		//Extend blindness
		builder.addMix(POTION_BLINDNESS, Items.REDSTONE, POTION_BLINDNESS_LONG);
		//Transient
		builder.addStartMix(CRItems.soulCluster, POTION_TRANSIENT);
		//Extend transient
		builder.addMix(POTION_TRANSIENT, Items.REDSTONE, POTION_TRANSIENT_LONG);
		//Filling empty bottles
		builder.addRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.ICE), PotionContents.createItemStack(Items.POTION, Potions.WATER));
	}

	public static boolean canBePermanentEffect(MobEffectInstance effect){
		if(!effect.getEffect().value().isInstantenous()){
			//Confirm the effect isn't blacklisted
			ResourceLocation effectRegistryName = MiscUtil.getRegistryName(effect.getEffect().value(), BuiltInRegistries.MOB_EFFECT);
			List<? extends String> blacklist = CRConfig.permanentEffectBlacklist.get();
			return blacklist.stream().noneMatch(entry -> ResourceLocation.withDefaultNamespace(entry).equals(effectRegistryName));
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
			boolean limitPower = toApply.getEffect() != CRPotions.HEALTH_PENALTY_EFFECT && CRConfig.limitPermanentPotionStrength.get();

			//'Permanent' is actually maximum duration, which is ~3.4 years ingame
			target.addEffect(new MobEffectInstance(toApply.getEffect(), Integer.MAX_VALUE, limitPower ? Math.min(toApply.getAmplifier(), 0) : toApply.getAmplifier(), toApply.isAmbient(), CRConfig.permanentPotionParticles.get() && toApply.isVisible(), toApply.showIcon()));
			return true;
		}
		return false;
	}
}
