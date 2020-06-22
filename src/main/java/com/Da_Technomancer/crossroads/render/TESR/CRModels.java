package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.awt.*;

public class CRModels{

	//These contain trigonometry, so shouldn't be calculated every frame.
	//In some cases, these are going to be used in place of cos in order to store fewer variables. cos(X) = sin(90 - X), so this is allowed.
	private static final float[] sin24 = new float[5];
	private static final float radius_24 = 11F / 24F;
	static{
		final float buffer_24 = 3E-3F;//3 * 10^-3
		sin24[0] = (float) (Math.sin(Math.toRadians(7.5)) * radius_24) + buffer_24;
		sin24[1] = (float) (Math.sin(Math.toRadians(22.5)) * radius_24) + buffer_24;
		sin24[2] = (float) (Math.sin(Math.toRadians(37.5)) * radius_24) + buffer_24;
		sin24[3] = (float) (Math.sin(Math.toRadians(52.5)) * radius_24) + buffer_24;
		sin24[4] = (float) (Math.sin(Math.toRadians(67.5)) * radius_24) + buffer_24;
	}

	private static void draw24Polygon(MatrixStack matrix, IVertexBuilder builder, int light, int[] col, TextureAtlasSprite sprite){
		//Commented numbers specify the order of vertices on the final polygon, increasing clockwise

		float uSt0 = sprite.getInterpolatedU(8 - 16 * sin24[0]);
		float uSt1 = sprite.getInterpolatedU(8 - 16 * sin24[1]);
		float uSt2 = sprite.getInterpolatedU(8 - 16 * sin24[2]);
		float uSt3 = sprite.getInterpolatedU(8 - 16 * sin24[3]);
		float uSt4 = sprite.getInterpolatedU(8 - 16 * sin24[4]);
		float uStR = sprite.getInterpolatedU(8 - 16 * radius_24);
		float uEn0 = sprite.getInterpolatedU(8 + 16 * sin24[0]);
		float uEn1 = sprite.getInterpolatedU(8 + 16 * sin24[1]);
		float uEn2 = sprite.getInterpolatedU(8 + 16 * sin24[2]);
		float uEn3 = sprite.getInterpolatedU(8 + 16 * sin24[3]);
		float uEn4 = sprite.getInterpolatedU(8 + 16 * sin24[4]);
		float uEnR = sprite.getInterpolatedU(8 + 16 * radius_24);

		float vSt0 = sprite.getInterpolatedV(8 - 16 * sin24[0]);
		float vSt1 = sprite.getInterpolatedV(8 - 16 * sin24[1]);
		float vSt2 = sprite.getInterpolatedV(8 - 16 * sin24[2]);
		float vSt3 = sprite.getInterpolatedV(8 - 16 * sin24[3]);
		float vSt4 = sprite.getInterpolatedV(8 - 16 * sin24[4]);
		float vStR = sprite.getInterpolatedV(8 - 16 * radius_24);
		float vEn0 = sprite.getInterpolatedV(8 + 16 * sin24[0]);
		float vEn1 = sprite.getInterpolatedV(8 + 16 * sin24[1]);
		float vEn2 = sprite.getInterpolatedV(8 + 16 * sin24[2]);
		float vEn3 = sprite.getInterpolatedV(8 + 16 * sin24[3]);
		float vEn4 = sprite.getInterpolatedV(8 + 16 * sin24[4]);
		float vEnR = sprite.getInterpolatedV(8 + 16 * radius_24);

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], 0, radius_24, uEn0, vStR, 0, 1, 0, light, col);//1
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[1], 0, sin24[4], uEn1, vSt4, 0, 1, 0, light, col);//2
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[2], 0, sin24[3], uEn2, vSt3, 0, 1, 0, light, col);//3
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[3], 0, sin24[2], uEn3, vSt2, 0, 1, 0, light, col);//4

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[3], 0, sin24[2], uEn3, vSt2, 0, 1, 0, light, col);//4
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[4], 0, sin24[1], uEn4, vSt1, 0, 1, 0, light, col);//5
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], 0, radius_24, uSt0, vStR, 0, 1, 0, light, col);//24
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], 0, radius_24, uEn0, vStR, 0, 1, 0, light, col);//1

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[4], 0, sin24[1], uEn4, vSt1, 0, 1, 0, light, col);//5
		CRRenderUtil.addVertexBlock(builder, matrix, radius_24, 0, sin24[0], uEnR, vSt0, 0, 1, 0, light, col);//6
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[1], 0, sin24[4], uSt1, vSt4, 0, 1, 0, light, col);//23
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], 0, radius_24, uSt0, vStR, 0, 1, 0, light, col);//24

		CRRenderUtil.addVertexBlock(builder, matrix, radius_24, 0, sin24[0], uEnR, vSt0, 0, 1, 0, light, col);//6
		CRRenderUtil.addVertexBlock(builder, matrix, radius_24, 0, -sin24[0], uEnR, vEn0, 0, 1, 0, light, col);//7
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[2], 0, sin24[3], uSt2, vSt3, 0, 1, 0, light, col);//22
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[1], 0, sin24[4], uSt1, vSt4, 0, 1, 0, light, col);//23

		CRRenderUtil.addVertexBlock(builder, matrix, radius_24, 0, -sin24[0], uEnR, vEn0, 0, 1, 0, light, col);//7
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[4], 0, -sin24[1], uEn4, vEn1, 0, 1, 0, light, col);//8
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[3], 0, sin24[2], uSt3, vSt2, 0, 1, 0, light, col);//21
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[2], 0, sin24[3], uSt2, vSt3, 0, 1, 0, light, col);//22

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[4], 0, -sin24[1], uEn4, vEn1, 0, 1, 0, light, col);//8
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[3], 0, -sin24[2], uEn3, vEn2, 0, 1, 0, light, col);//9
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[4], 0, sin24[1], uSt4, vSt1, 0, 1, 0, light, col);//20
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[3], 0, sin24[2], uSt3, vSt2, 0, 1, 0, light, col);//21

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[3], 0, -sin24[2], uEn3, vEn2, 0, 1, 0, light, col);//9
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[2], 0, -sin24[3], uEn2, vEn3, 0, 1, 0, light, col);//10
		CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, 0, sin24[0], uStR, vSt0, 0, 1, 0, light, col);//19
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[4], 0, sin24[1], uSt4, vSt1, 0, 1, 0, light, col);//20

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[2], 0, -sin24[3], uEn2, vEn3, 0, 1, 0, light, col);//10
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[1], 0, -sin24[4], uEn1, vEn4, 0, 1, 0, light, col);//11
		CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, 0, -sin24[0], uStR, vEn0, 0, 1, 0, light, col);//18
		CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, 0, sin24[0], uStR, vSt0, 0, 1, 0, light, col);//19

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[1], 0, -sin24[4], uEn1, vEn4, 0, 1, 0, light, col);//11
		CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], 0, -radius_24, uEn0, vEnR, 0, 1, 0, light, col);//12
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[4], 0, -sin24[1], uSt4, vEn1, 0, 1, 0, light, col);//17
		CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, 0, -sin24[0], uStR, vEn0, 0, 1, 0, light, col);//18

		CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], 0, -radius_24, uEn0, vEnR, 0, 1, 0, light, col);//12
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], 0, -radius_24, uSt0, vEnR, 0, 1, 0, light, col);//13
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[3], 0, -sin24[2], uSt3, vEn2, 0, 1, 0, light, col);//16
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[4], 0, -sin24[1], uSt4, vEn1, 0, 1, 0, light, col);//17

		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], 0, -radius_24, uSt0, vEnR, 0, 1, 0, light, col);//13
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[1], 0, -sin24[4], uSt1, vEn4, 0, 1, 0, light, col);//14
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[2], 0, -sin24[3], uSt2, vEn3, 0, 1, 0, light, col);//15
		CRRenderUtil.addVertexBlock(builder, matrix, -sin24[3], 0, -sin24[2], uSt3, vEn2, 0, 1, 0, light, col);//16
	}

	/**
	 * Draws a 24 sided gear, at the same scale as a normal small gear.
	 * This needs to be scaled x3 horizontally for most uses
	 * Draws centered at the current position
	 * @param matrix The matrix to render relative to, will not be modified
	 * @param buffer A generic buffer
	 * @param light The combined light value
	 * @param color The color to shade this by
	 */
	public static void draw24Gear(MatrixStack matrix, IRenderTypeBuffer buffer, int light, Color color){

		float top = -0.375F;
		float bottom = -.5F;

		float extend = 25F / 48F;
		float topProng = -.376F;
		float bottomProng = -.495F;
		float widthProng = 1F / 48F;

		int[] col = CRRenderUtil.convertColor(color);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_24_TEXTURE);

		//Top and bottom (sprite)
		matrix.push();
		matrix.translate(0, top, 0);
		draw24Polygon(matrix, builder, light, col, sprite);//Top
		matrix.translate(0, bottom - top, 0);
		matrix.scale(1, -1, 1);//Flip orientation
		draw24Polygon(matrix, builder, light, col, sprite);//Bottom
		matrix.pop();

		//Sides (spriteSide)
		TextureAtlasSprite spriteSide = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_24_RIM_TEXTURE);
		Quaternion rotation = Vector3f.YP.rotationDegrees(15);

		float vSt = spriteSide.getMinV();
		float vEn = spriteSide.getInterpolatedV(1);

		matrix.push();
		for(float i = 0; i < 6; i++){
			matrix.rotate(rotation);//15 deg

			float uSt = spriteSide.getInterpolatedU(i);
			float uEn = spriteSide.getInterpolatedU(i + 1);
			float u0St = spriteSide.getInterpolatedU(5 - i);
			float u0En = spriteSide.getInterpolatedU(6 - i);

			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, bottom, sin24[0], uEn, vSt, 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, bottom, -sin24[0], uEn, vSt, 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, top, -sin24[0], uSt, vEn, 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, top, sin24[0], uSt, vEn, 1, 0, 0, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, top, sin24[0], uEn, vSt, -1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, top, -sin24[0], uEn, vSt, -1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, bottom, -sin24[0], uSt, vEn, -1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -radius_24, bottom, sin24[0], uSt, vEn, -1, 0, 0, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], top, radius_24, u0En, vSt, 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], top, radius_24, u0En, vSt, 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], bottom, radius_24, u0St, vEn, 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], bottom, radius_24, u0St, vEn, 0, 0, 1, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], bottom, -radius_24, u0En, vSt, 0, 0, -1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], bottom, -radius_24, u0En, vSt, 0, 0, -1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, -sin24[0], top, -radius_24, u0St, vEn, 0, 0, -1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, sin24[0], top, -radius_24, u0St, vEn, 0, 0, -1, light, col);
		}
		matrix.pop();

		//Prongs (spriteSide)

		float u1 = spriteSide.getInterpolatedU(1);
		float u2 = spriteSide.getInterpolatedU(2);
		float u3 = spriteSide.getInterpolatedU(3);
		float u4 = spriteSide.getInterpolatedU(4);
		float u5 = spriteSide.getInterpolatedU(5);
		for(int i = 0; i < 24; i++){
			matrix.rotate(rotation);//15 deg

			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomProng, widthProng, u4, vSt, 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomProng, -widthProng, u4, vSt, 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topProng, -widthProng, u3, vEn, 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topProng, widthProng, u3, vEn, 1, 0, 0, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomProng, -widthProng, u4, vSt, 0, 0, -1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, bottomProng, -widthProng, u4, vSt, 0, 0, -1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, topProng, -widthProng, u5, vEn, 0, 0, -1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topProng, -widthProng, u5, vEn, 0, 0, -1, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, topProng, widthProng, u1, vSt, 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, topProng, widthProng, u1, vSt, 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, bottomProng, widthProng, u2, vEn, 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomProng, widthProng, u2, vEn, 0, 0, 1, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, topProng, -widthProng, u2, vSt, 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, topProng, -widthProng, u2, vSt, 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, topProng, widthProng, u3, vEn, 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, topProng, widthProng, u3, vEn, 0, 1, 0, light, col);

			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomProng, widthProng, u3, vEn, 0, -1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, bottomProng, widthProng, u3, vEn, 0, -1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, radius_24, bottomProng, -widthProng, u2, vSt, 0, -1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, extend, bottomProng, -widthProng, u2, vSt, 0, -1, 0, light, col);
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
	 * @param matrix The matrix to render relative to
	 * @param buffer A generic buffer
	 * @param light The combined light value
	 * @param color The color to shade this by
	 */
	public static void drawAxle(MatrixStack matrix, IRenderTypeBuffer buffer, int light, Color color){
		drawAxle(matrix, buffer, light, CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_SIDE_TEXTURE), CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_ENDS_TEXTURE), color);
	}

	/**
	 * Switch to the other method if possible, otherwise stop using axles to draw arbitrary rectangular prisms
	 * @param matrix The matrix to render relative to
	 * @param buffer A generic buffer
	 * @param light The combined light value
	 * @param sides Side texture
	 * @param ends End texture
	 * @param color The color to shade this by
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
