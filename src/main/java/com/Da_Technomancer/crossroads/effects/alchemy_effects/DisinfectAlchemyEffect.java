package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DisinfectAlchemyEffect implements IAlchEffect{

	private static final Method villConv = ReflectionUtil.reflectMethod(CRReflection.CURE_ZOMBIE);

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos.offset(1, 1, 1)), EntitySelector.ENTITY_STILL_ALIVE)){
			e.removeEffect(MobEffects.POISON);
			e.removeEffect(MobEffects.HUNGER);

			if(e instanceof ZombieVillager && villConv != null){
				try{
					villConv.invoke(e, null, 4000);
				}catch(IllegalAccessException | InvocationTargetException err){
					Crossroads.logger.error("An error occurred while disinfecting a zombie villager. Report to mod author", err);
				}
			}
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.disinfect");
	}
}
