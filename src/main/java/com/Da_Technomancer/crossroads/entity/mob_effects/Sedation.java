package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Sedation extends MobEffect{

	private static final String SEDATION_KEY = "cr_sedation";

	public Sedation(){
		super(MobEffectCategory.HARMFUL, 0x848484);
		setRegistryName(Crossroads.MODID, "sedation");

		//On non-players (and other blacklisted entities):
		//Disables the AI
		//AND allows physics to still apply
		//Normally, disabling mob AI also disables physics (acceleration, velocity, gravity)
		//I do not understand why physics in MC apparently runs on Looney Tunes logic, but the LAWS OF PHYSICS only apply if you know about them

		//Also applies a slowness and mining fatigue effect
		//These effects are meaningless to anything with AI disabled, but will do something to players
		addAttributeModifier(Attributes.MOVEMENT_SPEED, "ABCDEF01-7CE8-4030-940E-514C1F160890", -0.3D, AttributeModifier.Operation.MULTIPLY_TOTAL);
		addAttributeModifier(Attributes.ATTACK_SPEED, "ABCDEF01-E92A-486E-9800-B47F202C4386", -0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}

	private boolean canSedationApplyFully(LivingEntity entity){
		if(entity instanceof Mob){
			//If the entity is already sedated, it must be sedatable
			if(entity.getPersistentData().getBoolean(SEDATION_KEY)){
				//We track if the AI was disabled by sedation (vs another source) using a flag in NBT
				return true;
			}

			//Check against the blacklist
			List<? extends String> blacklist = CRConfig.sedationBlacklist.get();
			if(blacklist.stream().anyMatch(entry -> new ResourceLocation(entry).equals(entity.getType().getRegistryName()))){
				return false;
			}

			//We can NOT fully sedate anything that already had the AI disabled due to something other than sedation
			//Because we assume anything with the AI already disabled is supposed to remain that way
			//We track if the AI was disabled by sedation (vs another source) using a flag in NBT, which is handled above
			return !((Mob) entity).isNoAi();
		}
		return false;
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap p_111185_2_, int p_111185_3_){
		if(canSedationApplyFully(entity)){
			Mob mob = (Mob) entity;
			mob.setNoAi(true);
			mob.getPersistentData().putBoolean(SEDATION_KEY, true);
		}
		super.addAttributeModifiers(entity, p_111185_2_, p_111185_3_);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap p_111187_2_, int p_111187_3_){
		if(canSedationApplyFully(entity)){
			Mob mob = (Mob) entity;
			mob.setNoAi(false);
			mob.getPersistentData().putBoolean(SEDATION_KEY, false);
		}
		super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int p_76394_2_){
		if(canSedationApplyFully(entity)){
			//Force basic physics to apply despite AI being disabled
			//Done by enabling AI, calling the method responsible for physics, then re-disabling AI
			if(!entity.level.isClientSide()){
				Mob mob = (Mob) entity;
				mob.setNoAi(false);
				mob.travel(new Vec3(mob.xxa, mob.yya, mob.zza));
//				mob.aiStep();
				//Entities keep their momentum when the AI stops, and if they were walking, they have a tendency to moonwalk if we don't deteriorate the speed
				//Sedated entities therefore move in a more sluggish manner, losing their momentum more rapidly
				mob.setSpeed(mob.getSpeed() * 0.75F);
				mob.setNoAi(true);
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_){
		return true;
	}
}
