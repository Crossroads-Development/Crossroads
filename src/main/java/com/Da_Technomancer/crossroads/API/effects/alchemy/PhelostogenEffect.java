package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.entity.EntityFlame;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhelostogenEffect implements IAlchEffect{

	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, EnumMatterPhase phase, ReagentStack[] contents){
		int radius = Math.min(8, (int) Math.round(amount / 2D));
		
		for(int y = -radius; y <= radius; y++){
			int elevationRadius = (int) Math.round(Math.sqrt(radius * radius - y * y));
			double pitch = Math.asin((double) y / (double) radius);
			
			double angleIncrements = 1D / (double) elevationRadius;//Goes infinite
			for(double yaw = 0D; yaw < 2D * Math.PI; yaw += angleIncrements){
				EntityFlame flame = new EntityFlame(world, radius, false, false, 0, 0);//TODO
				flame.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				//Moves 1 block/second (velocity is in blocks/tick)
				flame.motionX = Math.cos(yaw) * Math.cos(pitch) / 20D;
				flame.motionY = Math.sin(pitch) / 20D;
				flame.motionZ = Math.sin(yaw) * Math.cos(pitch) / 20D;
				world.spawnEntity(flame);
			}
		}
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEffect(World world, BlockPos pos, double amount, EnumMatterPhase phase){
		doEffectAdv(world, pos, amount, phase, null);
	}
}
