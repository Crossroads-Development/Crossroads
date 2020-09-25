package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ChlorineAlchemyEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), EntityPredicates.IS_ALIVE)){
			e.addPotionEffect(new EffectInstance(Effects.WITHER, 300, 3));
			e.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 600, 3));
			e.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 600, 1));
			e.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 6000, 0));
			e.addPotionEffect(new EffectInstance(Effects.HUNGER, 600, 3));
			e.addPotionEffect(new EffectInstance(Effects.NAUSEA, 600, 0));//Sprinting is disabled while nauseous.
			e.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 600, 3));
			e.addPotionEffect(new EffectInstance(Effects.POISON, 1200, 0));
		}
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.poison");
	}
}
