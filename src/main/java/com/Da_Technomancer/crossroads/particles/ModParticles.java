package com.Da_Technomancer.crossroads.particles;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ModParticles{

	public static final EnumParticleTypes COLOR_FIRE;

	static{
		COLOR_FIRE = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_fire", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_fire", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
	}

	public static void init(){

	}

	private static IParticleFactory flameFact;
	
	public static void clientInit(){
		flameFact = new ParticleFlame.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_FIRE.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) -> {Particle particle = flameFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_); particle.setRBGColorF((float) xSpeedIn, (float) ySpeedIn, (float) zSpeedIn); return particle;});
	}
}