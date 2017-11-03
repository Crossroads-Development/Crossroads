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
	public static final EnumParticleTypes COLOR_GAS;
	public static final EnumParticleTypes COLOR_LIQUID;

	static{
		COLOR_FIRE = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_fire", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_fire", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
		COLOR_GAS = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_gas", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_gas", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
		COLOR_LIQUID = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_liquid", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_liquid", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
	}

	public static void init(){

	}

	private static IParticleFactory flameFact;
	private static IParticleFactory gasFact;
	private static IParticleFactory liquidFact;
	
	public static void clientInit(){
		flameFact = new ParticleFlame.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_FIRE.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) -> {Particle particle = flameFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_); particle.setRBGColorF((float) xSpeedIn, (float) ySpeedIn, (float) zSpeedIn); return particle;});
		gasFact = new ParticleBubbleColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_GAS.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = gasFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); particle.setRBGColorF((float) xSpeedIn, (float) ySpeedIn, (float) zSpeedIn); particle.setAlphaF(extraArgs.length == 0 ? 1F : ((float) extraArgs[0]) / 255F); return particle;});
		liquidFact = new ParticleDripColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_LIQUID.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = liquidFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); particle.setRBGColorF((float) xSpeedIn, (float) ySpeedIn, (float) zSpeedIn); particle.setAlphaF(extraArgs.length == 0 ? 1F : ((float) extraArgs[0]) / 255F); return particle;});
	}
}