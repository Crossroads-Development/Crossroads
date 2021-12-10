package com.Da_Technomancer.crossroads.ambient.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.awt.*;

public class ParticleBubbleColor extends TextureSheetParticle{

	private final SpriteSet sprite;

	protected ParticleBubbleColor(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, SpriteSet s){
		super(worldIn, x, y, z);
		this.sprite = s;
		setSize(0.02F, 0.02F);
		setBoundingBox(new AABB(x, y, z, x + bbWidth, y + bbHeight, z + bbWidth));
		hasPhysics = false;
		rCol = 1F;
		gCol = 1F;
		bCol = 1F;
//		setParticleTextureIndex(133);
		quadSize *= random.nextFloat() * 0.6F + 0.2F;
		xd = xSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.015D
		yd = ySpeed;//Suggestion: Math.random() * 0.015D
		zd = zSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.015D
		setColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
		setAlpha(c.getAlpha() / 255F);
		setLifetime((int) (7.0D / (Math.random() * 0.8D + 0.2D)));
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

	public static class Factory implements ParticleProvider<ColorParticleData>{

		private final SpriteSet sprite;

		protected Factory(SpriteSet spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle createParticle(ColorParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticleBubbleColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
