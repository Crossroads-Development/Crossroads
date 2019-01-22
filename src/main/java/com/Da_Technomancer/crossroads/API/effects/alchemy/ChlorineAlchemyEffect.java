package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChlorineAlchemyEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE)){
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
