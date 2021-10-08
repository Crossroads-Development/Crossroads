package com.Da_Technomancer.crossroads.ambient.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticleDripColor extends TextureSheetParticle{

	private final SpriteSet sprite;

	private ParticleDripColor(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, SpriteSet s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		hasPhysics = false;
		sprite = s;
//		setParticleTextureIndex(17);
		scale(random.nextFloat() * 0.6F + 0.6F);
		xd = xSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.02D
		yd = ySpeed;//Suggestion: (Math.random() - 1D) * 0.02D
		zd = zSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.02D
		setLifetime((int) (7.0D / (Math.random() * 0.8D + 0.2D)));
		setColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
		setAlpha(c.getAlpha() / 255F);
		setSpriteFromAge(sprite);
	}

	@Override
	public void tick(){
		xo = x;
		yo = y;
		zo = z;
		move(xd, yd, zd);
		xd *= 0.85D;
		yd *= 0.85D;
		zd *= 0.85D;
		setSpriteFromAge(sprite);
		if(age++ >= lifetime){
			remove();
		}
	}

	@Override
	public ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<ColorParticleData>{

		private final SpriteSet sprite;

		protected Factory(SpriteSet spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle createParticle(ColorParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticleDripColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
