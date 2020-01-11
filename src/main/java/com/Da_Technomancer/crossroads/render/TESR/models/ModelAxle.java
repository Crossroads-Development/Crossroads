package com.Da_Technomancer.crossroads.render.TESR.models;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ModelAxle {

	private static final ModelAxle INSTANCE = new ModelAxle();
	private static final ResourceLocation RESOURCE_ENDS = new ResourceLocation(Crossroads.MODID, "textures/model/axle_end.png");
	private static final ResourceLocation RESOURCE_SIDE = new ResourceLocation(Crossroads.MODID, "textures/model/axle.png");

	public static void render(Color c){
		render(RESOURCE_SIDE, RESOURCE_ENDS, c);
	}

	public static void render(ResourceLocation sides, ResourceLocation ends, Color c){
		INSTANCE.draw(sides, ends, c);
	}

	/**
	 * Translate to position + .5*scale blocks in x,y,z first, then scale, then rotate facing, then rotate angle.
	 * @deprecated Use one of the static methods instead
	 */
	@Deprecated
	public void draw(ResourceLocation side, ResourceLocation ends, Color color) {
		float radius = 1F / 16F;
		
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		Minecraft.getInstance().textureManager.bindTexture(ends);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-radius, -.4999F, -radius).tex(0, 0).endVertex();
		vb.pos(radius, -.4999F, -radius).tex(1, 0).endVertex();
		vb.pos(radius, -.4999F, radius).tex(1, 1).endVertex();
		vb.pos(-radius, -.4999F, radius).tex(0, 1).endVertex();
		
		vb.pos(-radius, .4999F, radius).tex(0, 1).endVertex();
		vb.pos(radius, .4999F, radius).tex(1, 1).endVertex();
		vb.pos(radius, .4999F, -radius).tex(1, 0).endVertex();
		vb.pos(-radius, .4999F, -radius).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		Minecraft.getInstance().textureManager.bindTexture(side);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-radius, .4999F, -radius).tex(0, 1).endVertex();
		vb.pos(radius, .4999F, -radius).tex(0.125D, 1).endVertex();
		vb.pos(radius, -.4999F, -radius).tex(0.125D, 0).endVertex();
		vb.pos(-radius, -.4999F, -radius).tex(0, 0).endVertex();
		
		vb.pos(-radius, -.4999F, radius).tex(0.125D, 0).endVertex();
		vb.pos(radius, -.4999F, radius).tex(0, 0).endVertex();
		vb.pos(radius, .4999F, radius).tex(0, 1).endVertex();
		vb.pos(-radius, .4999F, radius).tex(0.125D, 1).endVertex();

		vb.pos(-radius, -.4999F, radius).tex(0, 0).endVertex();
		vb.pos(-radius, .4999F, radius).tex(0, 1).endVertex();
		vb.pos(-radius, .4999F, -radius).tex(0.125D, 1).endVertex();
		vb.pos(-radius, -.4999F, -radius).tex(0.125D, 0).endVertex();

		vb.pos(radius, .4999F, -radius).tex(0, 1).endVertex();
		vb.pos(radius, .4999F, radius).tex(0.125D, 1).endVertex();
		vb.pos(radius, -.4999F, radius).tex(0.125D, 0).endVertex();
		vb.pos(radius, -.4999F, -radius).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();
	}
}
