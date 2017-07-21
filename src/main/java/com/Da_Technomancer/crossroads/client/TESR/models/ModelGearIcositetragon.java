package com.Da_Technomancer.crossroads.client.TESR.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * An Icositetragon is a 24 sided shape. This is used for the large gear.
 */
public class ModelGearIcositetragon{

	//These contain trigonometry, so shouldn't be calculated every frame.
	//In some cases, these are going to be used in place of cos in order to store fewer variables. cos(X) = sin(90 - X), so this is allowed. 
	private final double buffer = Math.pow(10, -3) * 3D;
	private final double radius = 11F / 24F;
	private final double sinFirst = (float) (Math.sin(Math.toRadians(7.5)) * radius) + buffer;
	private final double sinSecond = (float) (Math.sin(Math.toRadians(22.5)) * radius) + buffer;
	private final double sinThird = (float) (Math.sin(Math.toRadians(37.5)) * radius) + buffer;
	private final double sinFourth = (float) (Math.sin(Math.toRadians(52.5)) * radius) + buffer;
	private final double sinFifth = (float) (Math.sin(Math.toRadians(67.5)) * radius) + buffer;

	/**
	 * This model is the same size as a small gear, and must be scaled to 3x size for a large gear.
	 * The prongs are designed to be the correct size after resizing,
	 * 
	 * Ignore the following instructions, because I have no idea what I am doing.
	 * Translate to position + .5*scale blocks in x,y,z first, then scale, then translate .5*scale - .5 in x,y,z, then rotate facing, then rotate angle. 
	 */
	public void render(ResourceLocation res, ResourceLocation rim, Color color){

		double top = 0;
		double bottom = -.5F;

		float extend = 25F / 48F;
		float topProng = -.376F;
		float bottomProng = -.495F;
		float widthProng = 1F / 48F;

		Minecraft.getMinecraft().renderEngine.bindTexture(res);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);

		//Top and bottom
		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sinFirst, bottom, radius).tex(.5F - sinFirst, .5 - radius).endVertex();
		vb.pos(-sinSecond, bottom, sinFifth).tex(.5F - sinSecond, .5 - sinFifth).endVertex();
		vb.pos(-sinThird, bottom, sinFourth).tex(.5F - sinThird, .5 - sinFourth).endVertex();
		vb.pos(-sinFourth, bottom, sinThird).tex(.5F - sinFourth, .5 - sinThird).endVertex();
		vb.pos(-sinFifth, bottom, sinSecond).tex(.5F - sinFifth, .5 - sinSecond).endVertex();
		vb.pos(-radius, bottom, sinFirst).tex(.5F - radius, .5 - sinFirst).endVertex();
		vb.pos(-radius, bottom, -sinFirst).tex(.5F - radius, .5 + sinFirst).endVertex();
		vb.pos(-sinFifth, bottom, -sinSecond).tex(.5F - sinFifth, .5 + sinSecond).endVertex();
		vb.pos(-sinFourth, bottom, -sinThird).tex(.5F - sinFourth, .5 + sinThird).endVertex();
		vb.pos(-sinThird, bottom, -sinFourth).tex(.5F - sinThird, .5 + sinFourth).endVertex();
		vb.pos(-sinSecond, bottom, -sinFifth).tex(.5F - sinSecond, .5 + sinFifth).endVertex();
		vb.pos(-sinFirst, bottom, -radius).tex(.5F - sinFirst, .5 + radius).endVertex();
		vb.pos(sinFirst, bottom, -radius).tex(.5F + sinFirst, .5 + radius).endVertex();
		vb.pos(sinSecond, bottom, -sinFifth).tex(.5F + sinSecond, .5 + sinFifth).endVertex();
		vb.pos(sinThird, bottom, -sinFourth).tex(.5F + sinThird, .5 + sinFourth).endVertex();
		vb.pos(sinFourth, bottom, -sinThird).tex(.5F + sinFourth, .5 + sinThird).endVertex();
		vb.pos(sinFifth, bottom, -sinSecond).tex(.5F + sinFifth, .5 + sinSecond).endVertex();
		vb.pos(radius, bottom, -sinFirst).tex(.5F + radius, .5 + sinFirst).endVertex();
		vb.pos(radius, bottom, sinFirst).tex(.5F + radius, .5 - sinFirst).endVertex();
		vb.pos(sinFifth, bottom, sinSecond).tex(.5F + sinFifth, .5 - sinSecond).endVertex();
		vb.pos(sinFourth, bottom, sinThird).tex(.5F + sinFourth, .5 - sinThird).endVertex();
		vb.pos(sinThird, bottom, sinFourth).tex(.5F + sinThird, .5 - sinFourth).endVertex();
		vb.pos(sinSecond, bottom, sinFifth).tex(.5F + sinSecond, .5 - sinFifth).endVertex();
		vb.pos(sinFirst, bottom, radius).tex(.5F + sinFirst, .5 - radius).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sinFirst, top, radius).tex(.5F + sinFirst, .5 - radius).endVertex();
		vb.pos(sinSecond, top, sinFifth).tex(.5F + sinSecond, .5 - sinFifth).endVertex();
		vb.pos(sinThird, top, sinFourth).tex(.5F + sinThird, .5 - sinFourth).endVertex();
		vb.pos(sinFourth, top, sinThird).tex(.5F + sinFourth, .5 - sinThird).endVertex();
		vb.pos(sinFifth, top, sinSecond).tex(.5F + sinFifth, .5 - sinSecond).endVertex();
		vb.pos(radius, top, sinFirst).tex(.5F + radius, .5 - sinFirst).endVertex();
		vb.pos(radius, top, -sinFirst).tex(.5F + radius, .5 + sinFirst).endVertex();
		vb.pos(sinFifth, top, -sinSecond).tex(.5F + sinFifth, .5 + sinSecond).endVertex();
		vb.pos(sinFourth, top, -sinThird).tex(.5F + sinFourth, .5 + sinThird).endVertex();
		vb.pos(sinThird, top, -sinFourth).tex(.5F + sinThird, .5 + sinFourth).endVertex();
		vb.pos(sinSecond, top, -sinFifth).tex(.5F + sinSecond, .5 + sinFifth).endVertex();
		vb.pos(sinFirst, top, -radius).tex(.5F + sinFirst, .5 + radius).endVertex();
		vb.pos(-sinFirst, top, -radius).tex(.5F - sinFirst, .5 + radius).endVertex();
		vb.pos(-sinSecond, top, -sinFifth).tex(.5F - sinSecond, .5 + sinFifth).endVertex();
		vb.pos(-sinThird, top, -sinFourth).tex(.5F - sinThird, .5 + sinFourth).endVertex();
		vb.pos(-sinFourth, top, -sinThird).tex(.5F - sinFourth, .5 + sinThird).endVertex();
		vb.pos(-sinFifth, top, -sinSecond).tex(.5F - sinFifth, .5 + sinSecond).endVertex();
		vb.pos(-radius, top, -sinFirst).tex(.5F - radius, .5 + sinFirst).endVertex();
		vb.pos(-radius, top, sinFirst).tex(.5F - radius, .5 - sinFirst).endVertex();
		vb.pos(-sinFifth, top, sinSecond).tex(.5F - sinFifth, .5 - sinSecond).endVertex();
		vb.pos(-sinFourth, top, sinThird).tex(.5F - sinFourth, .5 - sinThird).endVertex();
		vb.pos(-sinThird, top, sinFourth).tex(.5F - sinThird, .5 - sinFourth).endVertex();
		vb.pos(-sinSecond, top, sinFifth).tex(.5F - sinSecond, .5 - sinFifth).endVertex();
		vb.pos(-sinFirst, top, radius).tex(.5F - sinFirst, .5 - radius).endVertex();
		Tessellator.getInstance().draw();

		//Sides
		Minecraft.getMinecraft().renderEngine.bindTexture(rim);
		GlStateManager.pushMatrix();
		for(float i = 0; i < 6; i++){
			GlStateManager.rotate(15, 0, 1, 0);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(radius, bottom, sinFirst).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(radius, bottom, -sinFirst).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(radius, top, -sinFirst).tex(i / 16F, 1F / 16F).endVertex();
			vb.pos(radius, top, sinFirst).tex(i / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-radius, top, sinFirst).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(-radius, top, -sinFirst).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(-radius, bottom, -sinFirst).tex(i / 16F, 1F / 16F).endVertex();
			vb.pos(-radius, bottom, sinFirst).tex(i / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sinFirst, top, radius).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sinFirst, top, radius).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sinFirst, bottom, radius).tex((5F - i) / 16F, 1F / 16F).endVertex();
			vb.pos(sinFirst, bottom, radius).tex((5F - i) / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sinFirst, bottom, -radius).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sinFirst, bottom, -radius).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sinFirst, top, -radius).tex((5F - i) / 16F, 1F / 16F).endVertex();
			vb.pos(sinFirst, top, -radius).tex((5F - i) / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();
		}
		GlStateManager.popMatrix();

		//Prongs
		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		for(int i = 0; i < 24; i++){
			GlStateManager.rotate(15, 0, 1, 0);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, bottomProng, widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(extend, bottomProng, -widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(extend, topProng, -widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, topProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, bottomProng, -widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(radius, bottomProng, -widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(radius, topProng, -widthProng).tex(5F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, topProng, -widthProng).tex(5F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, topProng, widthProng).tex(1F / 16F, 0).endVertex();
			vb.pos(radius, topProng, widthProng).tex(1F / 16F, 0).endVertex();
			vb.pos(radius, bottomProng, widthProng).tex(2F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, bottomProng, widthProng).tex(2F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, topProng, -widthProng).tex(2F / 16F, 0).endVertex();
			vb.pos(radius, topProng, -widthProng).tex(2F / 16F, 0).endVertex();
			vb.pos(radius, topProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, topProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, bottomProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(radius, bottomProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(radius, bottomProng, -widthProng).tex(2F / 16F, 0).endVertex();
			vb.pos(extend, bottomProng, -widthProng).tex(2F / 16F, 0).endVertex();
			Tessellator.getInstance().draw();
		}
	}
}
