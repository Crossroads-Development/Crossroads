package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@OnlyIn(Dist.CLIENT)
public class ParticlePowderColor extends Particle{

	protected ParticlePowderColor(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn){
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AxisAlignedBB(xCoordIn, yCoordIn, zCoordIn, xCoordIn + width, yCoordIn + height, zCoordIn + width));
		canCollide = false;
		particleRed = 1F;
		particleGreen = 1F;
		particleBlue = 1F;
		setParticleTextureIndex(177);
		particleScale *= rand.nextFloat() * 0.6F + 0.6F;
		motionX = xSpeedIn;//Suggestion: 0
		motionY = ySpeedIn;//Suggestion: 0
		motionZ = zSpeedIn;//Suggestion: 0
		particleMaxAge = (int) (7.0D / (Math.random() * 0.8D + 0.2D));
	}

	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		move(motionX, motionY, motionZ);
		motionX *= 0.85D;
		motionY *= 0.85D;
		motionZ *= 0.85D;

		if(particleAge++ >= particleMaxAge){
			setExpired();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory{
		public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_){
			return new ParticlePowderColor(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		}
	}
}
