package com.Da_Technomancer.crossroads.ambient.particles;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.awt.*;

public record ColorParticleData(ParticleType<ColorParticleData> type, Color color) implements ParticleOptions{

	public static final MapCodec<ColorParticleData> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			Codec.BYTE.xmap(ColorParticleData::getTypeFromID, ColorParticleData::getTypeID).fieldOf("type").forGetter(ColorParticleData::type),
			CraftingUtil.COLOR_CODEC.fieldOf("color").forGetter(ColorParticleData::color)
	).apply(instance, ColorParticleData::new));
	public static final StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE.map(ColorParticleData::getTypeFromID, ColorParticleData::getTypeID), ColorParticleData::type,
			CraftingUtil.COLOR_STREAM_CODEC, ColorParticleData::color,
			ColorParticleData::new
	);

	@Override
	public ParticleType<?> getType(){
		return type;
	}

	private static byte getTypeID(ParticleType<ColorParticleData> type){
		if(type == CRParticles.COLOR_GAS){
			return 0;
		}else if(type == CRParticles.COLOR_LIQUID){
			return 1;
		}else if(type == CRParticles.COLOR_SOLID){
			return 2;
		}else if(type == CRParticles.COLOR_FLAME){
			return 3;
		}else{
			return 4;
		}
	}

	private static ParticleType<ColorParticleData> getTypeFromID(byte id){
		return switch(id){
			case 0 -> CRParticles.COLOR_GAS;
			case 1 -> CRParticles.COLOR_LIQUID;
			case 2 -> CRParticles.COLOR_SOLID;
			case 3 -> CRParticles.COLOR_FLAME;
			default -> CRParticles.COLOR_SPLASH;
		};
	}
}
