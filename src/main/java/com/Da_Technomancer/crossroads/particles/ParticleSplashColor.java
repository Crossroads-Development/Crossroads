package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticleSplashColor extends SpriteTexturedParticle{

	private final IAnimatedSprite sprite;

	protected ParticleSplashColor(World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, IAnimatedSprite s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		canCollide = true;
		sprite = s;
//		setParticleTextureIndex(17);
		multipleParticleScaleBy(rand.nextFloat() * 0.6F + 0.6F);
		motionX = xSpeed;
		motionY = ySpeed;
		motionZ = zSpeed;
		setMaxAge(20);
		setColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
		setAlphaF(c.getAlpha() / 255F);
		selectSpriteWithAge(sprite);
	}

	@Override
	public IParticleRenderType getRenderType(){
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick(){
		super.tick();
		selectSpriteWithAge(sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<ColorParticleData>{

		private IAnimatedSprite sprite;

		protected Factory(IAnimatedSprite spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle makeParticle(ColorParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticleSplashColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
