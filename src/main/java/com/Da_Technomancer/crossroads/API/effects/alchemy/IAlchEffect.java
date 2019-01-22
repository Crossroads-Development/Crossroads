package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAlchEffect{

	/**
	 * @param world The world. Virtual server side only
	 * @param pos The blockpos to do this effect at
	 * @param amount The total quantity of this reagent. It is recommended that gas phase effects ignore this value.
	 * @param phase The current phase of this reagent
	 * @param contents The full contents of the caller.
	 */
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents);
}
