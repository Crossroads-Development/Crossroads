package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import net.minecraft.network.chat.Component;

public class AquaRegiaAlchemyEffect extends AcidAlchemyEffect{

	@Override
	protected boolean isRegia(){
		return true;
	}

	@Override
	protected int getDamage(){
		return 12;
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.acid_gold");
	}
}
