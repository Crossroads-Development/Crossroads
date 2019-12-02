package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticlePowderColor extends SpriteTexturedParticle{

	private final IAnimatedSprite sprite;

	private ParticlePowderColor(World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, IAnimatedSprite s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AxisAlignedBB(x, y, z, x + width, y + height, z + width));
		canCollide = false;
		sprite = s;
//		setParticleTextureIndex(177);
		multipleParticleScaleBy(rand.nextFloat() * 0.6F + 0.6F);
		motionX = xSpeed;//Suggestion: 0
		motionY = ySpeed;//Suggestion: 0
		motionZ = zSpeed;//Suggestion: 0
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

		private IAnimatedSprite sprite;

		protected Factory(IAnimatedSprite spriteIn){
			sprite = spriteIn;
		}

		@Nullable
		@Override
		public Particle makeParticle(ColorParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new ParticlePowderColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
