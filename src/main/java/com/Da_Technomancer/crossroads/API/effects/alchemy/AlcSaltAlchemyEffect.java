package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
		return new TranslatableComponent("effect.salt_alc");
	}
}
