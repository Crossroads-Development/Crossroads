package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ChlorineAlchemyEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos, pos.offset(1, 1, 1)), EntityPredicates.ENTITY_STILL_ALIVE)){
			e.addEffect(new EffectInstance(Effects.WITHER, 300, 3));
			e.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 600, 3));
			e.addEffect(new EffectInstance(Effects.WEAKNESS, 600, 1));
			e.addEffect(new EffectInstance(Effects.BLINDNESS, 6000, 0));
			e.addEffect(new EffectInstance(Effects.HUNGER, 600, 3));
			e.addEffect(new EffectInstance(Effects.CONFUSION, 600, 0));//Sprinting is disabled while nauseous.
			e.addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, 600, 3));
			e.addEffect(new EffectInstance(Effects.POISON, 1200, 0));
		}
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.poison");
	}
}
