package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DisinfectAlchemyEffect implements IAlchEffect{

	private static final Method villConv = ReflectionUtil.reflectMethod(CRReflection.CURE_ZOMBIE);

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), EntityPredicates.IS_ALIVE)){
			e.removePotionEffect(Effects.POISON);
			e.removePotionEffect(Effects.HUNGER);

			if(e instanceof ZombieVillagerEntity && villConv != null){
				try{
					villConv.invoke(e, null, 4000);
				}catch(IllegalAccessException | InvocationTargetException err){
					Crossroads.logger.error("An error occurred while disinfecting a zombie villager. Report to mod author", err);
				}
			}
		}
	}
}
