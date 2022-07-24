package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class AlcSaltAlchemyEffect extends SaltAlchemyEffect{

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		//Alchemical salt does not do the normal salt effect in flame form, as it already acts to prevent the flame cloud destroying blocks
		if(phase != EnumMatterPhase.FLAME){
			super.doEffect(world, pos, amount, phase, reags);
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.salt_alc");
	}
}
