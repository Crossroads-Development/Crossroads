package com.Da_Technomancer.crossroads.render.TESR.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ModelPyramid {

	/**
	 * Translate to position + .5*scale blocks in x,y,z first, then scale, then translate .5*scale - .5 in x,y,z, then rotate facing, then rotate angle. 
	 */
	public void render(ResourceLocation res, Color color) { 
		float radius = 1F / 16F;
		float height = 1.5F / 16F;
		
		Minecraft.getInstance().renderEngine.bindTexture(res);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
		
		
		vb.pos(0, height, 0).tex(.5F, 0).endVertex();
		vb.pos(radius, -height, -radius).tex(1, 1).endVertex();
		vb.pos(-radius, -height, -radius).tex(0, 1).endVertex();
		
		vb.pos(radius, -height, radius).tex(1, 1).endVertex();
		vb.pos(0, height, 0).tex(.5F, 0).endVertex();
		vb.pos(-radius, -height, radius).tex(0, 1).endVertex();
		
		vb.pos(radius, -height, radius).tex(1, 1).endVertex();
		vb.pos(radius, -height, -radius).tex(0, 1).endVertex();
		vb.pos(0, height, 0).tex(.5F, 0).endVertex();
		
		vb.pos(-radius, -height, -radius).tex(0, 1).endVertex();
		vb.pos(-radius, -height, radius).tex(1, 1).endVertex();
		vb.pos(0, height, 0).tex(.5F, 0).endVertex();
		
		Tessellator.getInstance().draw();
	}
}
