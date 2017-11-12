package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.awt.Color;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ChlorineAlchemyEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, double amount, EnumMatterPhase phase){
		int radius = 5 * (int) Math.pow(amount, 1D / 3D);//This affects a cubic area instead of a spherical one because it's so much easier to do a cube. 
		WorldServer worldS = (WorldServer) world;
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					Color col = AlchemyCore.REAGENTS[21].getColor(EnumMatterPhase.GAS);
					worldS.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F, new int[] {col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()});
				}
			}
		}
		for(EntityLiving e : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)), EntitySelectors.IS_ALIVE)){
			e.addPotionEffect(new PotionEffect(MobEffects.WITHER, 15, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30, 1));
			e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300, 0));
			e.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0));//Night vision + blindness = completely black view of the world. This is a vanilla bug that is being taken advantage of here. 
			e.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 30, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 30, 0));//Sprinting is disabled while nauseous. 
			e.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 30, 3));
			e.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 0));
		}
	}
}
