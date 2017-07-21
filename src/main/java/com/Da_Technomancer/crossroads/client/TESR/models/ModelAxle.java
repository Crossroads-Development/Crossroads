package com.Da_Technomancer.crossroads.client.TESR.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ModelAxle {

	/**
	 * Translate to position + .5*scale blocks in x,y,z first, then scale, then rotate facing, then rotate angle. 
	 */
	public void render(ResourceLocation side, ResourceLocation ends, Color color) { 
		float radius = 1F / 16F;
		
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		Minecraft.getMinecraft().renderEngine.bindTexture(ends);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-radius, -.5F, -radius).tex(0, 0).endVertex();
		vb.pos(radius, -.5F, -radius).tex(1, 0).endVertex();
		vb.pos(radius, -.5F, radius).tex(1, 1).endVertex();
		vb.pos(-radius, -.5F, radius).tex(0, 1).endVertex();
		
		vb.pos(-radius, .5F, radius).tex(0, 1).endVertex();
		vb.pos(radius, .5F, radius).tex(1, 1).endVertex();
		vb.pos(radius, .5F, -radius).tex(1, 0).endVertex();
		vb.pos(-radius, .5F, -radius).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		Minecraft.getMinecraft().renderEngine.bindTexture(side);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-radius, .5F, -radius).tex(0, 1).endVertex();
		vb.pos(radius, .5F, -radius).tex(1, 1).endVertex();
		vb.pos(radius, -.5F, -radius).tex(1, 0).endVertex();
		vb.pos(-radius, -.5F, -radius).tex(0, 0).endVertex();
		
		vb.pos(-radius, -.5F, radius).tex(1, 0).endVertex();
		vb.pos(radius, -.5F, radius).tex(0, 0).endVertex();
		vb.pos(radius, .5F, radius).tex(0, 1).endVertex();
		vb.pos(-radius, .5F, radius).tex(1, 1).endVertex();

		vb.pos(-radius, -.5F, radius).tex(0, 0).endVertex();
		vb.pos(-radius, .5F, radius).tex(0, 1).endVertex();
		vb.pos(-radius, .5F, -radius).tex(1, 1).endVertex();
		vb.pos(-radius, -.5F, -radius).tex(1, 0).endVertex();

		vb.pos(radius, .5F, -radius).tex(0, 1).endVertex();
		vb.pos(radius, .5F, radius).tex(1, 1).endVertex();
		vb.pos(radius, -.5F, radius).tex(1, 0).endVertex();
		vb.pos(radius, -.5F, -radius).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();
	}
}
