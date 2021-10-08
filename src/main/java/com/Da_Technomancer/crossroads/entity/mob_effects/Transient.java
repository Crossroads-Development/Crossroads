package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Transient extends MobEffect{

	public Transient(){
		super(MobEffectCategory.HARMFUL, 0xB7FFF1);
		setRegistryName(Crossroads.MODID, "transient");
	}
}
