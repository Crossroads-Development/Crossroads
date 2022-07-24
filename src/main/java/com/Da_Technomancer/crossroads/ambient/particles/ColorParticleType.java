package com.Da_Technomancer.crossroads.ambient.particles;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class ColorParticleType extends ParticleType<ColorParticleData>{

	public ColorParticleType(boolean alwaysShow){
		super(alwaysShow, ColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<ColorParticleData> codec(){
		return ColorParticleData.codec;
	}
}
