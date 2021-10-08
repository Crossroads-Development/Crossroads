package com.Da_Technomancer.crossroads.ambient.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticleFlameColor extends TextureSheetParticle{

	private final SpriteSet sprite;

	private ParticleFlameColor(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, SpriteSet s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AABB(x, y, z, x + bbWidth, y + bbHeight, z + bbWidth));
		hasPhysics = false;
		sprite = s;
//		setParticleTextureIndex(48);
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
	public int getLightColor(float partialTick){
		float f = (age + partialTick) / (float) lifetime;
		f = Mth.clamp(f, 0.0F, 1.0F);
		int i = super.getLightColor(partialTick);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int) (f * 15.0F * 16.0F);
		if(j > 240){
			j = 240;
		}

		return j | k << 16;
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
			return new ParticleFlameColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
