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

	private static final ResourceLocation TEXTURE_24 = new ResourceLocation(Crossroads.MODID, "textures/model/gear_24.png");
	private static final ResourceLocation TEXTURE_24_RIM = new ResourceLocation(Crossroads.MODID, "textures/model/gear_24_rim.png");

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

	//These contain sqrt, so I don't want to calculate them every frame.
	private static final float sHalf8 = 1F / (2F * (1F + (float) Math.sqrt(2F)));//Half the side length of a 1 block sized octagon
	private static final float sHalfT8 = 8F / (1F + (float) Math.sqrt(2F));//Used for texture mapping to an octagon

	/**
	 * Draws an octagon with side-to-side distance (center length) of 1 block
	 * Draws centered at the current position, oriented up
	 * @param builder A vertex builder with BLOCK vertex buffer format
	 * @param matrix The reference matrix
	 * @param color The color to shade by, as a size 4 array
	 * @param light The combined light value
	 * @param sprite The sprite that will be mapped onto the octagon
	 */
	public static void drawOctagon(IVertexBuilder builder, MatrixStack matrix, int[] color, int light, TextureAtlasSprite sprite){
		float lHalf = 0.5F;//Distance from center to side
		//Texture coords
		float lHalfT = 8F;
		float uSSt = sprite.getInterpolatedU(8 - sHalfT8);
		float uSEn = sprite.getInterpolatedU(8 + sHalfT8);
		float uLSt = sprite.getInterpolatedU(8 - lHalfT);
		float uLEn = sprite.getInterpolatedU(8 + lHalfT);
		float vSSt = sprite.getInterpolatedV(8 - sHalfT8);
		float vSEn = sprite.getInterpolatedV(8 + sHalfT8);
		float vLSt = sprite.getInterpolatedV(8 - lHalfT);
		float vLEn = sprite.getInterpolatedV(8 + lHalfT);

		//Because we're in GL_QUADS draw mode, we split the octagon into 3 quadrilaterals
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8, 0, lHalf, uSSt, vLSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8, 0, -lHalf, uSSt, vLEn, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, 0, -sHalf8, uLEn, vSSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, 0, sHalf8, uLSt, vSSt, 0, 1, 0, light, color);

		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8, 0, lHalf, uSSt, vLSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8, 0, lHalf, uSEn, vLSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8, 0, -lHalf, uSEn, vLEn, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8, 0, -lHalf, uSSt, vLEn, 0, 1, 0, light, color);

		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8, 0, lHalf, uSEn, vLSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, 0, sHalf8, uLEn, vSSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, 0, -sHalf8, uLEn, vSEn, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8, 0, -lHalf, uSEn, vLEn, 0, 1, 0, light, color);
	}

	/**
	 * Draws an octagonal prism with side-to-side distance (center length) of 1 block and height of 2 pixels
	 * Draws centered at the current position, oriented up
	 * @param builder A vertex builder with BLOCK vertex buffer format
	 * @param matrix The reference matrix, will not be modified
	 * @param color The color to shade by, as a size 4 array
	 * @param light The combined light value
	 * @param sprite The sprite that will be mapped onto the octagon
	 */
	public static void draw8Core(IVertexBuilder builder, MatrixStack matrix, int[] color, int light, TextureAtlasSprite sprite){
		float top = 0.0625F;//Half of height
		float lHalf = 0.5F;//Half the side length of the octagon
		float sHalf8S = sHalf8;//Scaled version of sHalf8 for gears

		//Draw 2 octagons
		//Top
		matrix.push();
		matrix.scale(2F * lHalf, 1, 2F * lHalf);
		matrix.translate(0, top, 0);
		drawOctagon(builder, matrix, color, light, sprite);
		matrix.pop();

		//Bottom
		matrix.push();
		matrix.scale(2F * lHalf, -1, 2F * lHalf);//The -1 y flips it upside-down
		matrix.translate(0, top, 0);
		drawOctagon(builder, matrix, color, light, sprite);
		matrix.pop();

		//The sides are darker than the prongs and top
		int[] sideCol = new int[] {color[0] - 130, color[1] - 130, color[2] - 130, color[3]};
		float tHeight = 1F / 16F;

		//Texture coords
		float tHeightT = tHeight * 16F;
		float uSt = sprite.getMinU();
		float uEn = sprite.getMaxU();
		float uSSt = sprite.getInterpolatedU(8 - sHalfT8);
		float uSEn = sprite.getInterpolatedU(8 + sHalfT8);
		float uHSt = sprite.getInterpolatedU(tHeightT);
		float uHEn = sprite.getInterpolatedU(16 - tHeightT);
		float vSt = sprite.getMinV();
		float vEn = sprite.getMaxV();
		float vSSt = sprite.getInterpolatedV(8 - sHalfT8);
		float vSEn = sprite.getInterpolatedV(8 + sHalfT8);
		float vHSt = sprite.getInterpolatedV(tHeightT);
		float vHEn = sprite.getInterpolatedV(16 - tHeightT);

		//Sides
		//Can't be done via loop due to distinct texture mapping

		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, -top, sHalf8S, uEn, vSSt, 1, 0, 0, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, -top, -sHalf8S, uEn, vSEn, 1, 0, 0, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, top, -sHalf8S, uHEn, vSEn, 1, 0, 0, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, top, sHalf8S, uHEn, vSSt, 1, 0, 0, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, top, sHalf8S, uHSt, vSSt, -1, 0, 0, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, top, -sHalf8S, uHSt, vSEn, -1, 0, 0, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, -top, -sHalf8S, uSt, vSEn, -1, 0, 0, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, -top, sHalf8S, uSt, vSSt, -1, 0, 0, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, top, lHalf, uSEn, vSt, 0, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, top, lHalf, uSSt, vSt, 0, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, -top, lHalf, uSSt, vHSt, 0, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, -top, lHalf, uSEn, vHSt, 0, 0, 1, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, -top, -lHalf, uSEn, vHEn, 0, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, -top, -lHalf, uSSt, vHEn, 0, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, top, -lHalf, uSSt, vEn, 0, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, top, -lHalf, uSEn, vEn, 0, 0, -1, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, top, -lHalf, uSEn, vEn, 1, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, top, -sHalf8S, uEn, vSEn, 1, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, -top, -sHalf8S, uEn, vSEn, 1, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, -top, -lHalf, uSEn, vEn, 1, 0, -1, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, -top, -lHalf, uSSt, vEn, -1, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, -top, -sHalf8S, uSt, vSEn, -1, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, top, -sHalf8S, uSt, vSEn, -1, 0, -1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, top, -lHalf, uSSt, vEn, -1, 0, -1, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, -top, lHalf, uSEn, vSt, 1, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, -top, sHalf8S, uEn, vSSt, 1, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, lHalf, top, sHalf8S, uEn, vSSt, 1, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, sHalf8S, top, lHalf, uSEn, vSt, 1, 0, 1, light, sideCol);

		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, top, lHalf, uSSt, vSt, -1, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, top, sHalf8S, uSt, vSSt, -1, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -lHalf, -top, sHalf8S, uSt, vSSt, -1, 0, 1, light, sideCol);
		CRRenderUtil.addVertexBlock(builder, matrix, -sHalf8S, -top, lHalf, uSSt, vSt, -1, 0, 1, light, sideCol);
	}

	/**
	 * Draws an 8 sided gear, at the normal scale
	 * Draws centered at the current position
	 * @param matrix The reference matrix
	 * @param builder A vertex builder with BLOCK vertex buffer format
	 * @param color The color to shade by, as a size 4 array
	 * @param light The combined light value
	 */
	public static void draw8Gear(MatrixStack matrix, IVertexBuilder builder, int[] color, int light){
		matrix.push();

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TEXTURE);
		float lHalf = 7F / 16F;//Half the side length of the octagon

		//Renders the core of the gear, leaving only the prongs
		matrix.push();
		matrix.scale(2F * lHalf, 1, 2F * lHalf);
		draw8Core(builder, matrix, color, light, sprite);
		matrix.pop();

		//Prongs
		//Given the option of hand coding 8 orientations for each 5 sided prong or using matrix transformations and a loop, I took the path of sanity retention
		float tHeight = 1F / 16F;
		Quaternion rotation = Vector3f.YP.rotationDegrees(360F / 8F);
		float extend = .5625F;
		float topP = 0.0575F;
		float bottomP = -0.0575F;

		//Texture coords
		float tHeightT = tHeight * 16F;
		float uEn = sprite.getMaxU();
		float uHEn = sprite.getInterpolatedU(16 - tHeightT);
		float vHMSt = sprite.getInterpolatedV(8 - tHeightT);
		float vHMEn = sprite.getInterpolatedV(8 + tHeightT);

		for(int i = 0; i < 8; i++){
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomP, tHeight, uEn, vHMSt, 1, 0, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomP, -tHeight, uEn, vHMEn, 1, 0, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topP, -tHeight, uHEn, vHMEn, 1, 0, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topP, tHeight, uHEn, vHMSt, 1, 0, 0, light, color);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomP, -tHeight, uEn, vHMSt, 0, 0, -1, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, bottomP, -tHeight, uEn, vHMEn, 0, 0, -1, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, topP, -tHeight, uHEn, vHMEn, 0, 0, -1, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topP, -tHeight, uHEn, vHMSt, 0, 0, -1, light, color);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, topP, tHeight, uHEn, vHMSt, 0, 0, 1, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, topP, tHeight, uHEn, vHMEn, 0, 0, 1, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, bottomP, tHeight, uEn, vHMEn, 0, 0, 1, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomP, tHeight, uEn, vHMSt, 0, 0, 1, light, color);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, topP, -tHeight, uHEn, vHMSt, 0, 1, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, topP, -tHeight, uHEn, vHMEn, 0, 1, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, topP, tHeight, uEn, vHMEn, 0, 1, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topP, tHeight, uEn, vHMSt, 0, 1, 0, light, color);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomP, tHeight, uEn, vHMSt, 0, -1, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, bottomP, tHeight, uEn, vHMEn, 0, -1, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, lHalf, bottomP, -tHeight, uHEn, vHMEn, 0, -1, 0, light, color);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomP, -tHeight, uHEn, vHMSt, 0, -1, 0, light, color);

			matrix.rotate(rotation);
		}

		matrix.pop();
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
