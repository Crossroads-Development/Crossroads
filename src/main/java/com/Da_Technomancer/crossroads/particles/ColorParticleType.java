package com.Da_Technomancer.crossroads.particles;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;

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
