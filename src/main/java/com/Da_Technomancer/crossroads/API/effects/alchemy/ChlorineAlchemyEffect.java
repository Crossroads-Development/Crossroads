package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.awt.Color;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.entity.EntityLivingBase;
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
		int radius = 2 * (int) Math.pow(amount, 1D / 3D);//This affects a cubic area instead of a spherical one because it's so much easier to do a cube. 
		WorldServer worldS = (WorldServer) world;
		Color col = AlchemyCore.REAGENTS[21].getColor(EnumMatterPhase.GAS);
		
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					worldS.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + x + Math.random(), (float) pos.getY() + y + Math.random(), (float) pos.getZ() + z + Math.random(), 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F, new int[] {col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()});
				}
			}
		}
		for(EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)), EntitySelectors.IS_ALIVE)){
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
