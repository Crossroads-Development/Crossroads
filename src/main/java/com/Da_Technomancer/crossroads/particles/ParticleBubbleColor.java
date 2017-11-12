package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBubbleColor extends Particle{

	protected ParticleBubbleColor(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn){
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AxisAlignedBB(xCoordIn, yCoordIn, zCoordIn, xCoordIn + width, yCoordIn + height, zCoordIn + width));
		canCollide = false;
		particleRed = 1F;
		particleGreen = 1F;
		particleBlue = 1F;
		setParticleTextureIndex(133);
		particleScale *= rand.nextFloat() * 0.6F + 0.2F;
		motionX = xSpeedIn;//Suggestion: (Math.random() * 2D - 1D) * 0.015D
		motionY = ySpeedIn;//Suggestion: Math.random() * 0.015D
		motionZ = zSpeedIn;//Suggestion: (Math.random() * 2D - 1D) * 0.015D
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

		if(particleMaxAge-- <= 0){
			setExpired();
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Factory implements IParticleFactory{
		public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_){
			return new ParticleBubbleColor(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		}
	}
}
