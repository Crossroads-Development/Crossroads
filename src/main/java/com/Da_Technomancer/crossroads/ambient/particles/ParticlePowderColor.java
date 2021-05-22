package com.Da_Technomancer.crossroads.ambient.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticlePowderColor extends SpriteTexturedParticle{

	private final IAnimatedSprite sprite;

	private ParticlePowderColor(ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, IAnimatedSprite s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AxisAlignedBB(x, y, z, x + bbWidth, y + bbHeight, z + bbWidth));
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
		public Particle createParticle(ColorParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticlePowderColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
