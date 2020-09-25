package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public interface IAlchEffect{

	/**
	 * @param world The world. Virtual server side only
	 * @param pos The blockpos to do this effect at
	 * @param amount The total quantity of this reagent.
	 * @param phase The phase this effect is being performed as. Not necessarily the phase of the reagent
	 * @param contents The full contents of the caller.
	 */
	void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents);

	ITextComponent getName();
}
