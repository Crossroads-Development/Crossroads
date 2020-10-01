package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticleDripColor extends SpriteTexturedParticle{

	private final IAnimatedSprite sprite;

	private ParticleDripColor(ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, IAnimatedSprite s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		canCollide = false;
		sprite = s;
//		setParticleTextureIndex(17);
		multiplyParticleScaleBy(rand.nextFloat() * 0.6F + 0.6F);
		motionX = xSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.02D
		motionY = ySpeed;//Suggestion: (Math.random() - 1D) * 0.02D
		motionZ = zSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.02D
		setMaxAge((int) (7.0D / (Math.random() * 0.8D + 0.2D)));
		setColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
		setAlphaF(c.getAlpha() / 255F);
		selectSpriteWithAge(sprite);
	}

	@Override
	public void tick(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		move(motionX, motionY, motionZ);
		motionX *= 0.85D;
		motionY *= 0.85D;
		motionZ *= 0.85D;
		selectSpriteWithAge(sprite);
		if(age++ >= maxAge){
			setExpired();
		}
	}

	@Override
	public IParticleRenderType getRenderType(){
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<ColorParticleData>{

		private final IAnimatedSprite sprite;

		protected Factory(IAnimatedSprite spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle makeParticle(ColorParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticleDripColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
