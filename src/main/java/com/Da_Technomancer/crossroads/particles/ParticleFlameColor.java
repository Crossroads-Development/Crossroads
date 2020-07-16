package com.Da_Technomancer.crossroads.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ParticleFlameColor extends SpriteTexturedParticle{

	private final IAnimatedSprite sprite;

	private ParticleFlameColor(ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Color c, IAnimatedSprite s){
		super(worldIn, x, y, z);
		setSize(0.02F, 0.02F);
		setBoundingBox(new AxisAlignedBB(x, y, z, x + width, y + height, z + width));
		canCollide = false;
		sprite = s;
//		setParticleTextureIndex(48);
		particleScale *= rand.nextFloat() * 0.6F + 0.2F;
		motionX = xSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.015D
		motionY = ySpeed;//Suggestion: Math.random() * 0.015D
		motionZ = zSpeed;//Suggestion: (Math.random() * 2D - 1D) * 0.015D
		setColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
		setAlphaF(c.getAlpha() / 255F);
		setMaxAge((int) (7.0D / (Math.random() * 0.8D + 0.2D)));
		selectSpriteWithAge(sprite);
	}

	@Override
	public int getBrightnessForRender(float partialTick){
		float f = (age + partialTick) / (float) maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightnessForRender(partialTick);
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
			return new ParticleFlameColor(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getColor(), sprite);
		}
	}
}
