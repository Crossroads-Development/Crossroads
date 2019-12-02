package com.Da_Technomancer.crossroads.particles;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ColorParticleType extends ParticleType<ColorParticleData>{

	public ColorParticleType(String name, boolean alwaysShow){
		super(alwaysShow, Deserializer.INSTANCE);
		setRegistryName(new ResourceLocation(Crossroads.MODID, name));
	}

	private static class Deserializer implements IParticleData.IDeserializer<ColorParticleData>{

		private static final Deserializer INSTANCE = new Deserializer();

		@Override
		public ColorParticleData deserialize(ParticleType<ColorParticleData> type, StringReader reader) throws CommandSyntaxException{
			int[] col = new int[4];
			for(int i = 0; i < 4; i++){
				reader.expect(' ');
				col[i] = reader.readInt();
			}
			return new ColorParticleData(type, new Color(col[0], col[1], col[2], col[3]));
		}

		@Override
		public ColorParticleData read(ParticleType<ColorParticleData> type, PacketBuffer buffer){
			return new ColorParticleData(type, new Color(buffer.readInt(), true));
		}
	}
}
