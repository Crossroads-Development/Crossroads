package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

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
		return new TranslatableComponent("effect.acid_gold");
	}
}
