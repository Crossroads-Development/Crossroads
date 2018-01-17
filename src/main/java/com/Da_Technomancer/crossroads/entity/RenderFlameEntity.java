package com.Da_Technomancer.crossroads.entity;

import java.awt.Color;

import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderFlameEntity extends Render<EntityFlame>{

	protected RenderFlameEntity(RenderManager renderManager){
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFlame entity){
		return ModParticles.PARTICLE_1_TEXTURE;
	}

	@Override
	public void doRender(EntityFlame entity, double x, double y, double z, float entityYaw, float partialTicks){
		if(!this.renderOutlines){
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y, (float) z);
			this.bindEntityTexture(entity);
			RenderHelper.enableStandardItemLighting();
			float brightX = OpenGlHelper.lastBrightnessX;
			float brightY = OpenGlHelper.lastBrightnessY;

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

			float f = 0F;
			float f1 = f + 0.0624375F;
			float f2 = 3F / 16.0F;
			float f3 = f2 + 0.0624375F;

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(0.0F, 0.1F, 0.0F);
			GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(0.3F, 0.3F, 0.3F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			Color col = entity.getColor();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
			bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(f, f3).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(f1, f3).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(f1, f2).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(f, f2).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}
	}
}
