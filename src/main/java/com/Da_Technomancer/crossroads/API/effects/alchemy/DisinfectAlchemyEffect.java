package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.Main;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DisinfectAlchemyEffect implements IAlchEffect{

	private static final Method villConv = MiscUtil.reflectMethod(EntityZombieVillager.class, "startConverting", "func_191991_a");

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE)){
			e.removePotionEffect(MobEffects.POISON);
			e.removePotionEffect(MobEffects.HUNGER);

			if(e instanceof EntityZombieVillager && villConv != null){
				try{
					villConv.invoke(e, null, 4000);
				}catch(IllegalAccessException | InvocationTargetException err){
					Main.logger.error("An error occurred while disinfecting a zombie villager. Report to mod author", err);
				}
			}

			e.addPotionEffect(new PotionEffect(MobEffects.WITHER, 300, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 600, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 1));
			e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 6000, 0));
			e.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 600, 0));//Sprinting is disabled while nauseous. 
			e.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 600, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.POISON, 1200, 0));
		}
	}
}
