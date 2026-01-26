package com.Da_Technomancer.crossroads.ambient.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ColorParticleType extends ParticleType<ColorParticleData>{

	public ColorParticleType(boolean alwaysShow){
		super(alwaysShow);
	}

	@Override
	public MapCodec<ColorParticleData> codec(){
		return ColorParticleData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleData> streamCodec(){
		return ColorParticleData.STREAM_CODEC;
	}

}
