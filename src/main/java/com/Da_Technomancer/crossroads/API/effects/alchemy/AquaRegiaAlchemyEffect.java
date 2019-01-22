package com.Da_Technomancer.crossroads.API.effects.alchemy;

public class AquaRegiaAlchemyEffect extends AcidAlchemyEffect{

	@Override
	protected boolean isRegia(){
		return true;
	}

	@Override
	protected int getDamage(){
		return 12;
	}
}
