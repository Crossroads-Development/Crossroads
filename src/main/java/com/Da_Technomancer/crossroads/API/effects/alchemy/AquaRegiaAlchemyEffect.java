package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.acid_gold");
	}
}
