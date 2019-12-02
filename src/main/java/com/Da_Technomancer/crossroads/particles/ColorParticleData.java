package com.Da_Technomancer.crossroads.particles;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import java.awt.*;
import java.util.Locale;

public class ColorParticleData implements IParticleData{

	private final ParticleType<ColorParticleData> type;
	private final Color col;

	public ColorParticleData(ParticleType<ColorParticleData> type, Color col){
		this.type = type;
		this.col = col;
	}

	public Color getColor(){
		return col;
	}

	@Override
	public ParticleType<?> getType(){
		return type;
	}

	@Override
	public void write(PacketBuffer buffer){
		buffer.writeInt(col.getRGB());
	}

	@Override
	public String getParameters(){
		return String.format(Locale.ROOT, "%s %d %d %d %d", type.getRegistryName(), col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
	}
}
