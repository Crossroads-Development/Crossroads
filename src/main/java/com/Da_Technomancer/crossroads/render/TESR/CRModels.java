package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CRModels{

	//These contain trigonometry, so shouldn't be calculated every frame.
	//In some cases, these are going to be used in place of cos in order to store fewer variables. cos(X) = sin(90 - X), so this is allowed.
	private static final float[] sin24 = new float[5];
	private static final float radius_24 = 11F / 24F;
	private static final ResourceLocation TEXTURE_SCREW = new ResourceLocation(Crossroads.MODID, "textures/model/pump.png");

	static{
		final float buffer_24 = (float) Math.pow(10, -3) * 3F;
		sin24[0] = (float) (Math.sin(Math.toRadians(7.5)) * radius_24) + buffer_24;
		sin24[1] = (float) (Math.sin(Math.toRadians(22.5)) * radius_24) + buffer_24;
		sin24[2] = (float) (Math.sin(Math.toRadians(37.5)) * radius_24) + buffer_24;
		sin24[3] = (float) (Math.sin(Math.toRadians(52.5)) * radius_24) + buffer_24;
		sin24[4] = (float) (Math.sin(Math.toRadians(67.5)) * radius_24) + buffer_24;
	}

	//These contain sqrt, so I don't want to calculate them every frame.
	private static final float sHalf8 = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private static final float sHalfT8 = .5F / (1F + (float) Math.sqrt(2F));

	public static final ResourceLocation TEXTURE_8 = new ResourceLocation(Crossroads.MODID, "textures/model/gear_oct.png");
	private static final ResourceLocation TEXTURE_24 = new ResourceLocation(Crossroads.MODID, "textures/model/gear_24.png");
	private static final ResourceLocation TEXTURE_24_RIM = new ResourceLocation(Crossroads.MODID, "textures/model/gear_rim.png");
	private static final ResourceLocation TEXTURE_ENDS_AXLE = new ResourceLocation(Crossroads.MODID, "textures/model/axle_end.png");
	private static final ResourceLocation TEXTURE_SIDE_AXLE = new ResourceLocation(Crossroads.MODID, "textures/model/axle.png");

	/**
	 * Draws a 24 sided gear, at the same scale as a normal small gear.
	 * This needs to be scaled x3 for most uses
	 * Draws centered at the current position
	 * @param color The color to shade this by
	 */
	public static void draw24Gear(Color color){

		double top = -0.375F;
		double bottom = -.5F;

		float extend = 25F / 48F;
		float topProng = -.376F;
		float bottomProng = -.495F;
		float widthProng = 1F / 48F;

		Minecraft.getInstance().textureManager.bindTexture(TEXTURE_24);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);

		//Top and bottom
		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sin24[0], bottom, radius_24).tex(.5F - sin24[0], .5 - radius_24).endVertex();
		vb.pos(-sin24[1], bottom, sin24[4]).tex(.5F - sin24[1], .5 - sin24[4]).endVertex();
		vb.pos(-sin24[2], bottom, sin24[3]).tex(.5F - sin24[2], .5 - sin24[3]).endVertex();
		vb.pos(-sin24[3], bottom, sin24[2]).tex(.5F - sin24[3], .5 - sin24[2]).endVertex();
		vb.pos(-sin24[4], bottom, sin24[1]).tex(.5F - sin24[4], .5 - sin24[1]).endVertex();
		vb.pos(-radius_24, bottom, sin24[0]).tex(.5F - radius_24, .5 - sin24[0]).endVertex();
		vb.pos(-radius_24, bottom, -sin24[0]).tex(.5F - radius_24, .5 + sin24[0]).endVertex();
		vb.pos(-sin24[4], bottom, -sin24[1]).tex(.5F - sin24[4], .5 + sin24[1]).endVertex();
		vb.pos(-sin24[3], bottom, -sin24[2]).tex(.5F - sin24[3], .5 + sin24[2]).endVertex();
		vb.pos(-sin24[2], bottom, -sin24[3]).tex(.5F - sin24[2], .5 + sin24[3]).endVertex();
		vb.pos(-sin24[1], bottom, -sin24[4]).tex(.5F - sin24[1], .5 + sin24[4]).endVertex();
		vb.pos(-sin24[0], bottom, -radius_24).tex(.5F - sin24[0], .5 + radius_24).endVertex();
		vb.pos(sin24[0], bottom, -radius_24).tex(.5F + sin24[0], .5 + radius_24).endVertex();
		vb.pos(sin24[1], bottom, -sin24[4]).tex(.5F + sin24[1], .5 + sin24[4]).endVertex();
		vb.pos(sin24[2], bottom, -sin24[3]).tex(.5F + sin24[2], .5 + sin24[3]).endVertex();
		vb.pos(sin24[3], bottom, -sin24[2]).tex(.5F + sin24[3], .5 + sin24[2]).endVertex();
		vb.pos(sin24[4], bottom, -sin24[1]).tex(.5F + sin24[4], .5 + sin24[1]).endVertex();
		vb.pos(radius_24, bottom, -sin24[0]).tex(.5F + radius_24, .5 + sin24[0]).endVertex();
		vb.pos(radius_24, bottom, sin24[0]).tex(.5F + radius_24, .5 - sin24[0]).endVertex();
		vb.pos(sin24[4], bottom, sin24[1]).tex(.5F + sin24[4], .5 - sin24[1]).endVertex();
		vb.pos(sin24[3], bottom, sin24[2]).tex(.5F + sin24[3], .5 - sin24[2]).endVertex();
		vb.pos(sin24[2], bottom, sin24[3]).tex(.5F + sin24[2], .5 - sin24[3]).endVertex();
		vb.pos(sin24[1], bottom, sin24[4]).tex(.5F + sin24[1], .5 - sin24[4]).endVertex();
		vb.pos(sin24[0], bottom, radius_24).tex(.5F + sin24[0], .5 - radius_24).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sin24[0], top, radius_24).tex(.5F + sin24[0], .5 - radius_24).endVertex();
		vb.pos(sin24[1], top, sin24[4]).tex(.5F + sin24[1], .5 - sin24[4]).endVertex();
		vb.pos(sin24[2], top, sin24[3]).tex(.5F + sin24[2], .5 - sin24[3]).endVertex();
		vb.pos(sin24[3], top, sin24[2]).tex(.5F + sin24[3], .5 - sin24[2]).endVertex();
		vb.pos(sin24[4], top, sin24[1]).tex(.5F + sin24[4], .5 - sin24[1]).endVertex();
		vb.pos(radius_24, top, sin24[0]).tex(.5F + radius_24, .5 - sin24[0]).endVertex();
		vb.pos(radius_24, top, -sin24[0]).tex(.5F + radius_24, .5 + sin24[0]).endVertex();
		vb.pos(sin24[4], top, -sin24[1]).tex(.5F + sin24[4], .5 + sin24[1]).endVertex();
		vb.pos(sin24[3], top, -sin24[2]).tex(.5F + sin24[3], .5 + sin24[2]).endVertex();
		vb.pos(sin24[2], top, -sin24[3]).tex(.5F + sin24[2], .5 + sin24[3]).endVertex();
		vb.pos(sin24[1], top, -sin24[4]).tex(.5F + sin24[1], .5 + sin24[4]).endVertex();
		vb.pos(sin24[0], top, -radius_24).tex(.5F + sin24[0], .5 + radius_24).endVertex();
		vb.pos(-sin24[0], top, -radius_24).tex(.5F - sin24[0], .5 + radius_24).endVertex();
		vb.pos(-sin24[1], top, -sin24[4]).tex(.5F - sin24[1], .5 + sin24[4]).endVertex();
		vb.pos(-sin24[2], top, -sin24[3]).tex(.5F - sin24[2], .5 + sin24[3]).endVertex();
		vb.pos(-sin24[3], top, -sin24[2]).tex(.5F - sin24[3], .5 + sin24[2]).endVertex();
		vb.pos(-sin24[4], top, -sin24[1]).tex(.5F - sin24[4], .5 + sin24[1]).endVertex();
		vb.pos(-radius_24, top, -sin24[0]).tex(.5F - radius_24, .5 + sin24[0]).endVertex();
		vb.pos(-radius_24, top, sin24[0]).tex(.5F - radius_24, .5 - sin24[0]).endVertex();
		vb.pos(-sin24[4], top, sin24[1]).tex(.5F - sin24[4], .5 - sin24[1]).endVertex();
		vb.pos(-sin24[3], top, sin24[2]).tex(.5F - sin24[3], .5 - sin24[2]).endVertex();
		vb.pos(-sin24[2], top, sin24[3]).tex(.5F - sin24[2], .5 - sin24[3]).endVertex();
		vb.pos(-sin24[1], top, sin24[4]).tex(.5F - sin24[1], .5 - sin24[4]).endVertex();
		vb.pos(-sin24[0], top, radius_24).tex(.5F - sin24[0], .5 - radius_24).endVertex();
		Tessellator.getInstance().draw();

		//Sides
		Minecraft.getInstance().textureManager.bindTexture(TEXTURE_24_RIM);
		GlStateManager.pushMatrix();
		for(float i = 0; i < 6; i++){
			GlStateManager.rotated(15, 0, 1, 0);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(radius_24, bottom, sin24[0]).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(radius_24, bottom, -sin24[0]).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(radius_24, top, -sin24[0]).tex(i / 16F, 1F / 16F).endVertex();
			vb.pos(radius_24, top, sin24[0]).tex(i / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-radius_24, top, sin24[0]).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(-radius_24, top, -sin24[0]).tex((i + 1F) / 16F, 0).endVertex();
			vb.pos(-radius_24, bottom, -sin24[0]).tex(i / 16F, 1F / 16F).endVertex();
			vb.pos(-radius_24, bottom, sin24[0]).tex(i / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sin24[0], top, radius_24).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sin24[0], top, radius_24).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sin24[0], bottom, radius_24).tex((5F - i) / 16F, 1F / 16F).endVertex();
			vb.pos(sin24[0], bottom, radius_24).tex((5F - i) / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sin24[0], bottom, -radius_24).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sin24[0], bottom, -radius_24).tex((6F - i) / 16F, 0).endVertex();
			vb.pos(-sin24[0], top, -radius_24).tex((5F - i) / 16F, 1F / 16F).endVertex();
			vb.pos(sin24[0], top, -radius_24).tex((5F - i) / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();
		}
		GlStateManager.popMatrix();

		//Prongs
		GlStateManager.color3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		for(int i = 0; i < 24; i++){
			GlStateManager.rotated(15, 0, 1, 0);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, bottomProng, widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(extend, bottomProng, -widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(extend, topProng, -widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, topProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, bottomProng, -widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(radius_24, bottomProng, -widthProng).tex(4F / 16F, 0).endVertex();
			vb.pos(radius_24, topProng, -widthProng).tex(5F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, topProng, -widthProng).tex(5F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, topProng, widthProng).tex(1F / 16F, 0).endVertex();
			vb.pos(radius_24, topProng, widthProng).tex(1F / 16F, 0).endVertex();
			vb.pos(radius_24, bottomProng, widthProng).tex(2F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, bottomProng, widthProng).tex(2F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, topProng, -widthProng).tex(2F / 16F, 0).endVertex();
			vb.pos(radius_24, topProng, -widthProng).tex(2F / 16F, 0).endVertex();
			vb.pos(radius_24, topProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(extend, topProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(extend, bottomProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(radius_24, bottomProng, widthProng).tex(3F / 16F, 1F / 16F).endVertex();
			vb.pos(radius_24, bottomProng, -widthProng).tex(2F / 16F, 0).endVertex();
			vb.pos(extend, bottomProng, -widthProng).tex(2F / 16F, 0).endVertex();
			Tessellator.getInstance().draw();
		}
	}

	/**
	 * Draws an 8 sided gear, at the normal scale
	 * Draws centered at the current position
	 * @param color The color to shade this by
	 */
	public static void draw8Gear(Color color){

		float top = 0.0625F;//-.375F;
		float lHalf = .4375F;

		float lHalfT = .5F;
		float tHeight = 1F / 16F;

		float extend = .5625F;

		float topP = 0.0575F;//-.380F;
		float bottomP = -0.0575F;//-.495F;

		Minecraft.getInstance().textureManager.bindTexture(TEXTURE_8);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);

		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf8, top, -lHalf).tex(.5F + sHalfT8, .5F - (-lHalfT)).endVertex();
		vb.pos(-sHalf8, top, -lHalf).tex(.5F + -sHalfT8, .5F - (-lHalfT)).endVertex();
		vb.pos(-lHalf, top, -sHalf8).tex(.5F + -lHalfT, .5F - (-sHalfT8)).endVertex();
		vb.pos(-lHalf, top, sHalf8).tex(.5F + -lHalfT, .5F - (sHalfT8)).endVertex();
		vb.pos(-sHalf8, top, lHalf).tex(.5F + -sHalfT8, .5F - (lHalfT)).endVertex();
		vb.pos(sHalf8, top, lHalf).tex(.5F + sHalfT8, .5F - (lHalfT)).endVertex();
		vb.pos(lHalf, top, sHalf8).tex(.5F + lHalfT, .5F - (sHalfT8)).endVertex();
		vb.pos(lHalf, top, -sHalf8).tex(.5F + lHalfT, .5F - (-sHalfT8)).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(lHalf, -top, -sHalf8).tex(.5F + lHalfT, .5F - (-sHalfT8)).endVertex();
		vb.pos(lHalf, -top, sHalf8).tex(.5F + lHalfT, .5F - (sHalfT8)).endVertex();
		vb.pos(sHalf8, -top, lHalf).tex(.5F + sHalfT8, .5F - (lHalfT)).endVertex();
		vb.pos(-sHalf8, -top, lHalf).tex(.5F + -sHalfT8, .5F - (lHalfT)).endVertex();
		vb.pos(-lHalf, -top, sHalf8).tex(.5F + -lHalfT, .5F - (sHalfT8)).endVertex();
		vb.pos(-lHalf, -top, -sHalf8).tex(.5F + -lHalfT, .5F - (-sHalfT8)).endVertex();
		vb.pos(-sHalf8, -top, -lHalf).tex(.5F + -sHalfT8, .5F - (-lHalfT)).endVertex();
		vb.pos(sHalf8, -top, -lHalf).tex(.5F + sHalfT8, .5F - (-lHalfT)).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.color3f((color.getRed() - 130F) / 255F, (color.getGreen() - 130F) / 255F, (color.getBlue() - 130F) / 255F);

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(lHalf, -top, sHalf8).tex(1F, .5F + -sHalfT8).endVertex();
		vb.pos(lHalf, -top, -sHalf8).tex(1F, .5F + sHalfT8).endVertex();
		vb.pos(lHalf, top, -sHalf8).tex(1F - tHeight, .5F + sHalfT8).endVertex();
		vb.pos(lHalf, top, sHalf8).tex(1F - tHeight, .5F + -sHalfT8).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-lHalf, top, sHalf8).tex(tHeight, .5F + -sHalfT8).endVertex();
		vb.pos(-lHalf, top, -sHalf8).tex(tHeight, .5F + sHalfT8).endVertex();
		vb.pos(-lHalf, -top, -sHalf8).tex(0, .5F + sHalfT8).endVertex();
		vb.pos(-lHalf, -top, sHalf8).tex(0, .5F + -sHalfT8).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf8, top, lHalf).tex(.5F + sHalfT8, 0).endVertex();
		vb.pos(-sHalf8, top, lHalf).tex(.5F + -sHalfT8, 0).endVertex();
		vb.pos(-sHalf8, -top, lHalf).tex(.5F + -sHalfT8, tHeight).endVertex();
		vb.pos(sHalf8, -top, lHalf).tex(.5F + sHalfT8, tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf8, -top, -lHalf).tex(.5F + sHalfT8, 1F - tHeight).endVertex();
		vb.pos(-sHalf8, -top, -lHalf).tex(.5F + -sHalfT8, 1F - tHeight).endVertex();
		vb.pos(-sHalf8, top, -lHalf).tex(.5F + -sHalfT8, 1).endVertex();
		vb.pos(sHalf8, top, -lHalf).tex(.5F + sHalfT8, 1).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf8, top, -lHalf).tex(.5F + sHalfT8, .5F - -lHalfT).endVertex();
		vb.pos(lHalf, top, -sHalf8).tex(.5F + lHalfT, .5F - -sHalfT8).endVertex();
		vb.pos(lHalf, -top, -sHalf8).tex(.5F + lHalfT, .5F - -sHalfT8).endVertex();
		vb.pos(sHalf8, -top, -lHalf).tex(.5F + sHalfT8, .5F - -lHalfT).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sHalf8, -top, -lHalf).tex(.5F + -sHalfT8, .5F - -lHalfT).endVertex();
		vb.pos(-lHalf, -top, -sHalf8).tex(.5F + -lHalfT, .5F - -sHalfT8).endVertex();
		vb.pos(-lHalf, top, -sHalf8).tex(.5F + -lHalfT, .5F - -sHalfT8).endVertex();
		vb.pos(-sHalf8, top, -lHalf).tex(.5F + -sHalfT8, .5F - -lHalfT).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf8, -top, lHalf).tex(.5F + sHalfT8, .5F - lHalfT).endVertex();
		vb.pos(lHalf, -top, sHalf8).tex(.5F + lHalfT, .5F - sHalfT8).endVertex();
		vb.pos(lHalf, top, sHalf8).tex(.5F + lHalfT, .5F - sHalfT8).endVertex();
		vb.pos(sHalf8, top, lHalf).tex(.5F + sHalfT8, .5F - lHalfT).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sHalf8, top, lHalf).tex(.5F + -sHalfT8, .5F - lHalfT).endVertex();
		vb.pos(-lHalf, top, sHalf8).tex(.5F + -lHalfT, .5F - sHalfT8).endVertex();
		vb.pos(-lHalf, -top, sHalf8).tex(.5F + -lHalfT, .5F - sHalfT8).endVertex();
		vb.pos(-sHalf8, -top, lHalf).tex(.5F + -sHalfT8, .5F - lHalfT).endVertex();
		Tessellator.getInstance().draw();

		//Prongs

		GlStateManager.color3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(extend, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(extend, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, bottomP, -tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(lHalf, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(lHalf, topP, tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(lHalf, topP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(extend, topP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(lHalf, bottomP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(extend, bottomP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//next prong

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-extend, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-lHalf, topP, tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-lHalf, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, -tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, bottomP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, bottomP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, topP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, topP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//next prong

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(tHeight, topP, -extend).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, -extend).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, bottomP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(tHeight, topP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, bottomP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(tHeight, bottomP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, topP, -extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, topP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//next prong

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, topP, extend).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, extend).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(tHeight, bottomP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, topP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, bottomP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, bottomP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, topP, extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(tHeight, topP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		Tessellator.getInstance().draw();


		GlStateManager.pushMatrix();
		GlStateManager.rotated(45, 0, 1, 0);

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(extend, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(extend, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, bottomP, -tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(lHalf, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(lHalf, topP, tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(lHalf, topP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(extend, topP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(lHalf, bottomP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(extend, bottomP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//next prong

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-extend, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-lHalf, topP, tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-extend, topP, tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-lHalf, bottomP, -tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, -tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, bottomP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, bottomP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-lHalf, bottomP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-extend, bottomP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-extend, topP, tHeight).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-lHalf, topP, tHeight).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-lHalf, topP, -tHeight).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-extend, topP, -tHeight).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//next prong

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(tHeight, topP, -extend).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, -extend).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, bottomP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(tHeight, topP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, bottomP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(tHeight, bottomP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, -extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, topP, -extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, topP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, -extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//next prong

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, topP, extend).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, extend).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(tHeight, bottomP, -lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, -lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, topP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-tHeight, bottomP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		vb.pos(-tHeight, bottomP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(tHeight, bottomP, extend).tex(1F, .5F + -tHeight).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(tHeight, topP, extend).tex(1F, .5F + -tHeight).endVertex();
		vb.pos(tHeight, topP, lHalf).tex(1F, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, lHalf).tex(1F - tHeight, .5F + tHeight).endVertex();
		vb.pos(-tHeight, topP, extend).tex(1F - tHeight, .5F + -tHeight).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.color3f(1F, 1F, 1F);

		GlStateManager.popMatrix();
	}

	/**
	 * Draws an axle, at the normal scale
	 * Draws centered at the current position
	 * @param color The color to shade this by
	 */
	public static void drawAxle(Color color){
		drawAxle(TEXTURE_SIDE_AXLE, TEXTURE_ENDS_AXLE, color);
	}

	/**
	 * Switch to the other method if possible, otherwise stop using axles to draw arbitrary rectangular prisms
	 * @param sides Side texture
	 * @param ends End texture
	 * @param color Color
	 */
	@Deprecated
	public static void drawAxle(ResourceLocation sides, ResourceLocation ends, Color color){
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

		Minecraft.getInstance().textureManager.bindTexture(sides);
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

	/**
	 * Draws a vertical screw for pumps and turbines
	 */
	public static void renderScrew(){
		//TODO check texture mapping and scaling
		Minecraft.getInstance().textureManager.bindTexture(TEXTURE_SCREW);

		BufferBuilder vb = Tessellator.getInstance().getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		//Central axis
		final float coreRad = 1F / 16F;
		final float rodHeight = 16.5F / 16F;
		final float bladeWid = 3F / 16F;
		final float bladeRad = coreRad + bladeWid;
		final float incline = 5F / 3F / 16F;

		vb.pos(-coreRad, 0, -coreRad).tex(0, 0).endVertex();
		vb.pos(-coreRad, rodHeight, -coreRad).tex(0, 16).endVertex();
		vb.pos(coreRad, rodHeight, -coreRad).tex(2, 16).endVertex();
		vb.pos(coreRad, 0, -coreRad).tex(2, 0).endVertex();

		vb.pos(-coreRad, 0, coreRad).tex(0, 0).endVertex();
		vb.pos(-coreRad, rodHeight, coreRad).tex(0, 16).endVertex();
		vb.pos(coreRad, rodHeight, coreRad).tex(2, 16).endVertex();
		vb.pos(coreRad, 0, coreRad).tex(2, 0).endVertex();

		vb.pos(-coreRad, 0, -coreRad).tex(0, 0).endVertex();
		vb.pos(-coreRad, rodHeight, -coreRad).tex(0, 16).endVertex();
		vb.pos(-coreRad, rodHeight, coreRad).tex(2, 16).endVertex();
		vb.pos(-coreRad, 0, coreRad).tex(2, 0).endVertex();

		vb.pos(coreRad, 0, -coreRad).tex(0, 0).endVertex();
		vb.pos(coreRad, rodHeight, -coreRad).tex(0, 16).endVertex();
		vb.pos(coreRad, rodHeight, coreRad).tex(2, 16).endVertex();
		vb.pos(coreRad, 0, coreRad).tex(2, 0).endVertex();

		vb.pos(-coreRad, 0, -coreRad).tex(0, 0).endVertex();
		vb.pos(-coreRad, 0, coreRad).tex(0, 2).endVertex();
		vb.pos(coreRad, 0, coreRad).tex(2, 2).endVertex();
		vb.pos(coreRad, 0, -coreRad).tex(2, 0).endVertex();

		vb.pos(-coreRad, rodHeight, -coreRad).tex(0, 0).endVertex();
		vb.pos(-coreRad, rodHeight, coreRad).tex(0, 2).endVertex();
		vb.pos(coreRad, rodHeight, coreRad).tex(2, 2).endVertex();
		vb.pos(coreRad, rodHeight, -coreRad).tex(2, 0).endVertex();

		//Blade 1
		vb.pos(coreRad, 0, -bladeRad).tex(0, 0).endVertex();
		vb.pos(bladeRad, 0, -bladeRad).tex(3, 0).endVertex();
		vb.pos(bladeRad, incline, bladeRad).tex(3, 8).endVertex();
		vb.pos(coreRad, incline, bladeRad).tex(0, 8).endVertex();

		vb.pos(coreRad, 0, -bladeRad).tex(0, 0).endVertex();
		vb.pos(coreRad, incline, bladeRad).tex(0, 8).endVertex();
		vb.pos(bladeRad, incline, bladeRad).tex(3, 8).endVertex();
		vb.pos(bladeRad, 0, -bladeRad).tex(3, 0).endVertex();

		//Blade 2
		vb.pos(-bladeRad, 2 * incline, coreRad).tex(0, 0).endVertex();
		vb.pos(bladeRad, incline, coreRad).tex(8, 0).endVertex();
		vb.pos(bladeRad, incline, coreRad + bladeWid).tex(8, 3).endVertex();
		vb.pos(-bladeRad, 2 * incline, coreRad + bladeWid).tex(0, 3).endVertex();

		vb.pos(-bladeRad, 2 * incline, coreRad).tex(0, 0).endVertex();
		vb.pos(-bladeRad, 2 * incline, coreRad + bladeWid).tex(0, 3).endVertex();
		vb.pos(bladeRad, incline, coreRad + bladeWid).tex(8, 3).endVertex();
		vb.pos(bladeRad, incline, coreRad).tex(8, 0).endVertex();

		//Blade 3
		vb.pos(-bladeRad, 2 * incline, bladeRad).tex(0, 8).endVertex();
		vb.pos(-coreRad, 2 * incline, bladeRad).tex(3, 8).endVertex();
		vb.pos(-coreRad, 3 * incline, -bladeRad).tex(3, 0).endVertex();
		vb.pos(-bladeRad, 3 * incline, -bladeRad).tex(0, 0).endVertex();

		vb.pos(-bladeRad, 2 * incline, bladeRad).tex(0, 8).endVertex();
		vb.pos(-bladeRad, 3 * incline, -bladeRad).tex(0, 0).endVertex();
		vb.pos(-coreRad, 3 * incline, -bladeRad).tex(3, 0).endVertex();
		vb.pos(-coreRad, 2 * incline, bladeRad).tex(3, 8).endVertex();

		Tessellator.getInstance().draw();
	}
}
