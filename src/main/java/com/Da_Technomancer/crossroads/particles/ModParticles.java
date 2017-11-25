package com.Da_Technomancer.crossroads.particles;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ModParticles{

	protected static final ResourceLocation PARTICLE_1_TEXTURE = new ResourceLocation(Main.MODID, "textures/particles/sheet_1.png");
	protected static final ResourceLocation BASE_PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
	
	public static final EnumParticleTypes COLOR_FLAME;
	public static final EnumParticleTypes COLOR_GAS;
	public static final EnumParticleTypes COLOR_LIQUID;
	public static final EnumParticleTypes COLOR_SOLID;

	static{
		COLOR_FLAME = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_flame", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_fire", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
		COLOR_GAS = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_gas", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_gas", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
		COLOR_LIQUID = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_liquid", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_liquid", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
		COLOR_SOLID = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_liquid", new Class<?>[] {String.class, int.class, boolean.class}, Main.MODID + "_color_liquid", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false);
	}

	public static void init(){

	}

	private static IParticleFactory flameFact;
	private static IParticleFactory gasFact;
	private static IParticleFactory liquidFact;
	private static IParticleFactory solidFact;

	public static void clientInit(){
		flameFact = new ParticleFlameColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_FLAME.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = flameFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});

		gasFact = new ParticleBubbleColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_GAS.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = gasFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});

		liquidFact = new ParticleDripColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_LIQUID.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = liquidFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});
		
		solidFact = new ParticlePowderColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_SOLID.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = solidFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});
	}
}