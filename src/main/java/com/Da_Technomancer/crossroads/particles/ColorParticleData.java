package com.Da_Technomancer.crossroads.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import java.awt.*;
import java.util.Locale;

public class ColorParticleData implements IParticleData{

	protected static final Codec<ColorParticleData> codec = RecordCodecBuilder.create((instance) -> instance.group(Codec.BYTE.fieldOf("type").forGetter(ColorParticleData::getTypeID), Codec.BYTE.fieldOf("r").forGetter((ColorParticleData data) -> (byte) data.getColor().getRed()), Codec.BYTE.fieldOf("g").forGetter((ColorParticleData data) -> (byte) data.getColor().getGreen()), Codec.BYTE.fieldOf("b").forGetter((ColorParticleData data) -> (byte) data.getColor().getBlue()), Codec.BYTE.fieldOf("a").forGetter((ColorParticleData data) -> (byte) data.getColor().getAlpha())).apply(instance, ColorParticleData::new));
	protected static final Deserializer DESERIALIZER = new Deserializer();

	private final ParticleType<ColorParticleData> type;
	private final Color col;

	private ColorParticleData(byte typeID, byte r, byte g, byte b, byte a){
		this(getTypeFromID(typeID), new Color(r, g, b, a));
	}

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

	private byte getTypeID(){
		//Used to allow the codec to encode which particle type this is
		//A bad workaround for a new system (codec) which is poorly understood and seems to be coded like one of those bad calculators that checks for every possible (hard coded) input
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
		switch(id){
			case 0:
				return CRParticles.COLOR_GAS;
			case 1:
				return CRParticles.COLOR_LIQUID;
			case 2:
				return CRParticles.COLOR_SOLID;
			case 3:
				return CRParticles.COLOR_FLAME;
			default:
				return CRParticles.COLOR_SPLASH;
		}
	}

	@Override
	public void writeToNetwork(PacketBuffer buffer){
		buffer.writeInt(col.getRGB());
	}

	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %d %d %d %d", type.getRegistryName(), col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
	}

	private static class Deserializer implements IParticleData.IDeserializer<ColorParticleData>{

		@Override
		public ColorParticleData fromCommand(ParticleType<ColorParticleData> type, StringReader reader) throws CommandSyntaxException{
			int[] col = new int[4];
			for(int i = 0; i < 4; i++){
				reader.expect(' ');
				col[i] = reader.readInt();
			}
			return new ColorParticleData(type, new Color(col[0], col[1], col[2], col[3]));
		}

		@Override
		public ColorParticleData fromNetwork(ParticleType<ColorParticleData> type, PacketBuffer buffer){
			return new ColorParticleData(type, new Color(buffer.readInt(), true));
		}
	}
}
