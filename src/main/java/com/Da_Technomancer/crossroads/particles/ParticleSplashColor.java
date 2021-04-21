package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticleSplashColor extends SpriteTexturedParticle{

	private final IAnimatedSprite sprite;

	protected ParticleSplashColor(ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, IAnimatedSprite s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		hasPhysics = true;
		sprite = s;
//		setParticleTextureIndex(17);
		scale(random.nextFloat() * 0.6F + 0.6F);
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		setLifetime(20);
		setColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
		setAlpha(c.getAlpha() / 255F);
		setSpriteFromAge(sprite);
	}

	@Override
	public IParticleRenderType getRenderType(){
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick(){
		super.tick();
		setSpriteFromAge(sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<ColorParticleData>{

		private final IAnimatedSprite sprite;

		protected Factory(IAnimatedSprite spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle createParticle(ColorParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticleSplashColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
