package com.Da_Technomancer.crossroads.ambient.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import javax.annotation.Nullable;
import java.awt.*;

public class ParticleSplashColor extends TextureSheetParticle{

	private final SpriteSet sprite;

	protected ParticleSplashColor(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, SpriteSet s){
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
	public ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick(){
		super.tick();
		setSpriteFromAge(sprite);
	}

	public static class Factory implements ParticleProvider<ColorParticleData>{

		private final SpriteSet sprite;

		protected Factory(SpriteSet spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle createParticle(ColorParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticleSplashColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
