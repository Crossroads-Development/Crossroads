package com.Da_Technomancer.crossroads.particles;

import java.lang.reflect.Field;
import java.util.Map;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModParticles{

	public static final ResourceLocation PARTICLE_1_TEXTURE = new ResourceLocation(Main.MODID, "textures/particles/sheet_1.png");
	protected static final ResourceLocation BASE_PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
	
	public static final EnumParticleTypes COLOR_FLAME;
	public static final EnumParticleTypes COLOR_GAS;
	public static final EnumParticleTypes COLOR_LIQUID;
	public static final EnumParticleTypes COLOR_SOLID;
	public static final EnumParticleTypes COLOR_SPLASH;

	private static final Field INT_TO_PARTICLE;
	private static final Field NAME_TO_PARTICLE;

	static{
		Field holder1 = null;
		Field holder2 = null;
		try{
			for(Field f : EnumParticleTypes.class.getDeclaredFields()){
				if(holder1 == null && ("field_179365_U".equals(f.getName()) || "PARTICLES".equals(f.getName()))){
					holder1 = f;
					holder1.setAccessible(true);
				}else if(holder2 == null && ("field_186837_Z".equals(f.getName()) || "BY_NAME".equals(f.getName()))){
					holder2 = f;
					holder2.setAccessible(true);
				}
			}
			//For no apparent reason ReflectionHelper consistently crashes in an obfus. environment for me with this method, so the above for loop is used instead.
		}catch(Exception e){
			Main.logger.catching(e);
		}
		INT_TO_PARTICLE = holder1;
		NAME_TO_PARTICLE = holder2;
		
		COLOR_FLAME = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_flame", new Class<?>[] {String.class, int.class, boolean.class, int.class}, Main.MODID + "_color_fire", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false, 4);
		COLOR_GAS = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_gas", new Class<?>[] {String.class, int.class, boolean.class, int.class}, Main.MODID + "_color_gas", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false, 4);
		COLOR_LIQUID = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_liquid", new Class<?>[] {String.class, int.class, boolean.class, int.class}, Main.MODID + "_color_liquid", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false, 4);
		COLOR_SOLID = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_solid", new Class<?>[] {String.class, int.class, boolean.class, int.class}, Main.MODID + "_color_solid", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false, 4);
		COLOR_SPLASH = EnumHelper.addEnum(EnumParticleTypes.class, Main.MODID + "_color_splash", new Class<?>[] {String.class, int.class, boolean.class, int.class}, Main.MODID + "_color_splash", EnumParticleTypes.values()[EnumParticleTypes.values().length - 1].getParticleID() + 1, false, 4);
		
		if(INT_TO_PARTICLE == null || NAME_TO_PARTICLE == null){
			Main.logger.error("NULL INT_TO_PARTICLE or NAME_TO_PARTICLE field! Report to mod author. Several Crossroads particles will not work properly!");
		}else{
			try{
				@SuppressWarnings("unchecked")
				Map<Integer, EnumParticleTypes> intMap = (Map<Integer, EnumParticleTypes>) INT_TO_PARTICLE.get(null);
				@SuppressWarnings("unchecked")
				Map<String, EnumParticleTypes> nameMap = (Map<String, EnumParticleTypes>) NAME_TO_PARTICLE.get(null);
				
				intMap.put(COLOR_FLAME.getParticleID(), COLOR_FLAME);
				intMap.put(COLOR_GAS.getParticleID(), COLOR_GAS);
				intMap.put(COLOR_LIQUID.getParticleID(), COLOR_LIQUID);
				intMap.put(COLOR_SOLID.getParticleID(), COLOR_SOLID);
				intMap.put(COLOR_SPLASH.getParticleID(), COLOR_SPLASH);
				
				nameMap.put(COLOR_FLAME.getParticleName(), COLOR_FLAME);
				nameMap.put(COLOR_GAS.getParticleName(), COLOR_GAS);
				nameMap.put(COLOR_LIQUID.getParticleName(), COLOR_LIQUID);
				nameMap.put(COLOR_SOLID.getParticleName(), COLOR_SOLID);
				nameMap.put(COLOR_SPLASH.getParticleName(), COLOR_SPLASH);
				
			}catch(Exception e){
				Main.logger.catching(e);
			}
		}
	}

	private static IParticleFactory flameFact;
	private static IParticleFactory gasFact;
	private static IParticleFactory liquidFact;
	private static IParticleFactory solidFact;
	private static IParticleFactory splashFact;

	@SideOnly(Side.CLIENT)
	public static void clientInit(){
		flameFact = new ParticleFlameColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_FLAME.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = flameFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});

		gasFact = new ParticleBubbleColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_GAS.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = gasFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});

		liquidFact = new ParticleDripColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_LIQUID.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = liquidFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});
		
		solidFact = new ParticlePowderColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_SOLID.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = solidFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});


		splashFact = new ParticleSplashColor.Factory();
		Minecraft.getMinecraft().effectRenderer.registerParticle(COLOR_SPLASH.getParticleID(), (int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... extraArgs) -> {Particle particle = splashFact.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, extraArgs); if(extraArgs.length >= 3) particle.setRBGColorF((float) (extraArgs[0]) / 255F, (float) (extraArgs[1]) / 255F, (float) (extraArgs[2]) / 255F); particle.setAlphaF(extraArgs.length < 4 ? 1F : ((float) extraArgs[3]) / 255F); return particle;});
	}
}