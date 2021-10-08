package com.Da_Technomancer.crossroads.ambient.particles;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

public class ColorParticleType extends ParticleType<ColorParticleData>{

	public ColorParticleType(String name, boolean alwaysShow){
		super(alwaysShow, ColorParticleData.DESERIALIZER);
		setRegistryName(new ResourceLocation(Crossroads.MODID, name));
	}

	@Override
	public Codec<ColorParticleData> codec(){
		return ColorParticleData.codec;
	}
}
