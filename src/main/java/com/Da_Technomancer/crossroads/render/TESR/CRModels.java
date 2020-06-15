package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CRModels{

	//These contain trigonometry, so shouldn't be calculated every frame.
	//In some cases, these are going to be used in place of cos in order to store fewer variables. cos(X) = sin(90 - X), so this is allowed.
	private static final float[] sin24 = new float[5];
	private static final float radius_24 = 11F / 24F;
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

		//TODO

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
	public static void drawAxle(MatrixStack matrix, IRenderTypeBuffer buffer, int light, Color color){
		drawAxle(matrix, buffer, light, CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_SIDE_TEXTURE), CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_ENDS_TEXTURE), color);
	}

	/**
	 * Switch to the other method if possible, otherwise stop using axles to draw arbitrary rectangular prisms
	 * @param sides Side texture
	 * @param ends End texture
	 * @param color Color
	 */
	@Deprecated
	public static void drawAxle(MatrixStack matrix, IRenderTypeBuffer buffer, int light, TextureAtlasSprite sides, TextureAtlasSprite ends, Color color){
		float radius = 1F / 16F;
		float len = .4999F;
		int[] col = {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
		float sideUEn = sides.getInterpolatedU(2);

		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());

		//Ends
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, -len, -radius, ends.getMinU(), ends.getMinV(), 0, -1, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, -len, -radius, ends.getMaxU(), ends.getMinV(), 0, -1, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, -len, radius, ends.getMaxU(), ends.getMaxV(), 0, -1, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, -len, radius, ends.getMinU(), ends.getMaxV(), 0, -1, 0, light, col);

		CRRenderUtil.addVertexBlock(builder, matrix, -radius, len, radius, ends.getMinU(), ends.getMaxV(), 0, 1, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, len, radius, ends.getMaxU(), ends.getMaxV(), 0, 1, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, len, -radius, ends.getMaxU(), ends.getMinV(), 0, 1, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, len, -radius, ends.getMinU(), ends.getMinV(), 0, 1, 0, light, col);

		//Sides
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, len, -radius, sides.getMinU(), sides.getMaxV(), 0, 0, -1, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, len, -radius, sideUEn, sides.getMaxV(), 0, 0, -1, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, -len, -radius, sideUEn, sides.getMinV(), 0, 0, -1, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, -len, -radius, sides.getMinU(), sides.getMinV(), 0, 0, -1, light, col);

		CRRenderUtil.addVertexBlock(builder, matrix, -radius, -len, radius, sideUEn, sides.getMinV(), 0, 0, 1, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, -len, radius, sides.getMinU(), sides.getMinV(), 0, 0, 1, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, len, radius, sides.getMinU(), sides.getMaxV(), 0, 0, 1, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, len, radius, sideUEn, sides.getMaxV(), 0, 0, 1, light, col);

		CRRenderUtil.addVertexBlock(builder, matrix, -radius, -len, radius, sides.getMinU(), sides.getMinV(), -1, 0, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, len, radius, sides.getMinU(), sides.getMaxV(), -1, 0, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, len, -radius, sideUEn, sides.getMaxV(), -1, 0, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, -radius, -len, -radius, sideUEn, sides.getMinV(), -1, 0, 0, light, col);

		CRRenderUtil.addVertexBlock(builder, matrix, radius, len, -radius, sides.getMinU(), sides.getMaxV(), 1, 0, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, len, radius, sideUEn, sides.getMaxV(), 1, 0, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, -len, radius, sideUEn, sides.getMinV(), 1, 0, 0, light, col);
		CRRenderUtil.addVertexBlock(builder, matrix, radius, -len, -radius, sides.getMinU(), sides.getMinV(), 1, 0, 0, light, col);
		Tessellator.getInstance().draw();
	}

	/**
	 * Draws a vertical screw for pumps and turbines
	 */
	public static void renderScrew(MatrixStack matrix, IRenderTypeBuffer buffer, int light){
		//Draw central axle
		matrix.push();
		matrix.translate(0, 0.5D, 0);
		drawAxle(matrix, buffer, light, new Color(160, 160, 160));
		matrix.pop();

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);

		IVertexBuilder vb = buffer.getBuffer(RenderType.getSolid());

		Quaternion rotation = Vector3f.YP.rotationDegrees(-90);
		for(int i = 0; i < 8; i++){
			drawTurbineBlade(vb, matrix, i / 16F, light, sprite);
			matrix.rotate(rotation);
		}
	}

	/**
	 * Draws a turbine blade. Does not bind the texture
	 * Draws at a horizontal offset in the +x
	 * @param builder A builder in BLOCK vertex format
	 * @param matrix The matrix to render with
	 * @param height The height of the bottom of the blade
	 * @param light Combined light value
	 * @param sprite The sprite to render with
	 */
	public static void drawTurbineBlade(IVertexBuilder builder, MatrixStack matrix, float height, int light, TextureAtlasSprite sprite){
		final float edgeIn = 1F / 16F;
		final float edgeOut = 4F / 16F;
		final float lenHalf = 3F / 16F;
		final float bottom = height;
		final float mid = height + 1F / 16F;
		final float top = mid + 1F / 16F;
		//Texture coords
		//Top & bottom
		final float uStT = sprite.getInterpolatedU(1F);
		final float vStT = sprite.getInterpolatedV(1F);
		final float uEnT = sprite.getInterpolatedU(7F);
		final float vEnT = sprite.getInterpolatedV(4F);

		final float vEnS = sprite.getInterpolatedV(2);

		//Bottom
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeIn, uStT, vStT, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, bottom, edgeIn, uEnT, vStT, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, bottom, edgeOut, uEnT, vEnT, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeOut, uStT, vEnT, 0, -1, 0, light);
//		vb.pos(-lenHalf, mid, edgeIn).tex(uStT, vStT).endVertex();
//		vb.pos(lenHalf, bottom, edgeIn).tex(uEnT, vStT).endVertex();
//		vb.pos(lenHalf, bottom, edgeOut).tex(uEnT, vEnT).endVertex();
//		vb.pos(-lenHalf, mid, edgeOut).tex(uStT, vEnT).endVertex();
		//Top
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, top, edgeIn, uStT, vStT, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, top, edgeOut, uStT, vEnT, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeOut, uEnT, vEnT, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeIn, uEnT, vStT, 0, 1, 0, light);
//		vb.pos(-lenHalf, top, edgeIn).tex(uStT, vStT).endVertex();
//		vb.pos(-lenHalf, top, edgeOut).tex(uStT, vEnT).endVertex();
//		vb.pos(lenHalf, mid, edgeOut).tex(uEnT, vEnT).endVertex();
//		vb.pos(lenHalf, mid, edgeIn).tex(uEnT, vStT).endVertex();
		//Side
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeOut, uStT, vStT, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, bottom, edgeOut, uEnT, vStT, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, mid, edgeOut, uEnT, vEnS, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, top, edgeOut, uStT, vEnS, 0, 0, 1, light);
//		vb.pos(-lenHalf, mid, edgeOut).tex(uStT, vStT).endVertex();
//		vb.pos(lenHalf, bottom, edgeOut).tex(uEnT, vStT).endVertex();
//		vb.pos(lenHalf, mid, edgeOut).tex(uEnT, vEnS).endVertex();
//		vb.pos(-lenHalf, top, edgeOut).tex(uStT, vEnS).endVertex();
		//Side
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, top, edgeIn, uStT, vStT, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, mid, edgeIn, uEnT, vStT, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, bottom, edgeIn, uEnT, vEnS, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeIn, uStT, vEnS, 0, 0, -1, light);
//		vb.pos(-lenHalf, top, edgeIn).tex(uStT, vStT).endVertex();
//		vb.pos(lenHalf, mid, edgeIn).tex(uEnT, vStT).endVertex();
//		vb.pos(lenHalf, bottom, edgeIn).tex(uEnT, vEnS).endVertex();
//		vb.pos(-lenHalf, mid, edgeIn).tex(uStT, vEnS).endVertex();
		//End
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, top, edgeIn, uStT, vStT, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeIn, uStT, vEnS, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, mid, edgeOut, uEnT, vEnS, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -lenHalf, top, edgeOut, uEnT, vStT, -1, 0, 0, light);
//		vb.pos(-lenHalf, top, edgeIn).tex(uStT, vStT).endVertex();
//		vb.pos(-lenHalf, mid, edgeIn).tex(uStT, vEnS).endVertex();
//		vb.pos(-lenHalf, mid, edgeOut).tex(uEnT, vEnS).endVertex();
//		vb.pos(-lenHalf, top, edgeOut).tex(uEnT, vStT).endVertex();
		//End
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, mid, edgeIn, uStT, vStT, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, mid, edgeOut, uEnT, vStT, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, bottom, edgeOut, uEnT, vEnS, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, lenHalf, bottom, edgeIn, uStT, vEnS, 1, 0, 0, light);
//		vb.pos(lenHalf, mid, edgeIn).tex(uStT, vStT).endVertex();
//		vb.pos(lenHalf, mid, edgeOut).tex(uEnT, vStT).endVertex();
//		vb.pos(lenHalf, bottom, edgeOut).tex(uEnT, vEnS).endVertex();
//		vb.pos(lenHalf, bottom, edgeIn).tex(uStT, vEnS).endVertex();

		Tessellator.getInstance().draw();
	}
}
