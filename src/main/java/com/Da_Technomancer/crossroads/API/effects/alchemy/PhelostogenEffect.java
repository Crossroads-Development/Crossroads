package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.util.function.Function;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import scala.Int;

public class PhelostogenEffect implements IAlchEffect{

	private final Function<Integer, Integer> RADIUS_FINDER;
	
	public PhelostogenEffect(Function<Integer, Integer> radiusFinder){
		this.RADIUS_FINDER = radiusFinder;
	}
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, int amount, double temp, EnumMatterPhase phase, ReagentMap contents){
		EntityFlameCore coreFlame = new EntityFlameCore(world);
		coreFlame.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		world.spawnEntity(coreFlame);
		coreFlame.setInitialValues(contents, temp, RADIUS_FINDER.apply(amount));
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, double temp, EnumMatterPhase phase){
		doEffectAdv(world, pos, amount, temp, phase, null);
	}
}
