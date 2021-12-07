package com.Da_Technomancer.crossroads.ambient.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticlePowderColor extends TextureSheetParticle{

	private final SpriteSet sprite;

	private ParticlePowderColor(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, SpriteSet s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AABB(x, y, z, x + bbWidth, y + bbHeight, z + bbWidth));
		hasPhysics = false;
		sprite = s;
//		setParticleTextureIndex(177);
		scale(random.nextFloat() * 0.6F + 0.6F);
		xd = xSpeed;//Suggestion: 0
		yd = ySpeed;//Suggestion: 0
		zd = zSpeed;//Suggestion: 0
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
			return new ParticlePowderColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
