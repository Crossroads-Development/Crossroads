package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Transient extends Effect{

	public Transient(){
		super(EffectType.HARMFUL, 0xB7FFF1);
		setRegistryName(Crossroads.MODID, "transient");
	}

	//TODO mob drop effects
}
