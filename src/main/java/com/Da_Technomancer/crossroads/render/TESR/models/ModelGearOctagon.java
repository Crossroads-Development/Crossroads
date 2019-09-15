package com.Da_Technomancer.crossroads.render.TESR.models;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Crossroads;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ModelGearOctagon{

	public static final ResourceLocation RESOURCE = new ResourceLocation(Crossroads.MODID, "textures/model/gear_oct.png");

	//These contain sqrt, so I don't want to calculate them every frame.
	private static final float sHalf = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private static final float sHalfT = .5F / (1F + (float) Math.sqrt(2F));

	public static void render(Color c){

		float top = 0.0625F;//-.375F;
		float lHalf = .4375F;

		float lHalfT = .5F;
		float tHeight = 1F / 16F;

		float extend = .5625F;

		float topP = 0.0575F;//-.380F;
		float bottomP = -0.0575F;//-.495F;

		Minecraft.getInstance().renderEngine.bindTexture(RESOURCE);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);

		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, .5F - (-lHalfT)).endVertex();
		vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, .5F - (-lHalfT)).endVertex();
		vb.pos(-lHalf, top, -sHalf).tex(.5F + -lHalfT, .5F - (-sHalfT)).endVertex();
		vb.pos(-lHalf, top, sHalf).tex(.5F + -lHalfT, .5F - (sHalfT)).endVertex();
		vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, .5F - (lHalfT)).endVertex();
		vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, .5F - (lHalfT)).endVertex();
		vb.pos(lHalf, top, sHalf).tex(.5F + lHalfT, .5F - (sHalfT)).endVertex();
		vb.pos(lHalf, top, -sHalf).tex(.5F + lHalfT, .5F - (-sHalfT)).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		vb.pos(lHalf, -top, -sHalf).tex(.5F + lHalfT, .5F - (-sHalfT)).endVertex();
		vb.pos(lHalf, -top, sHalf).tex(.5F + lHalfT, .5F - (sHalfT)).endVertex();
		vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, .5F - (lHalfT)).endVertex();
		vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, .5F - (lHalfT)).endVertex();
		vb.pos(-lHalf, -top, sHalf).tex(.5F + -lHalfT, .5F - (sHalfT)).endVertex();
		vb.pos(-lHalf, -top, -sHalf).tex(.5F + -lHalfT, .5F - (-sHalfT)).endVertex();
		vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, .5F - (-lHalfT)).endVertex();
		vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, .5F - (-lHalfT)).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.color((c.getRed() - 130F) / 255F, (c.getGreen() - 130F) / 255F, (c.getBlue() - 130F) / 255F);

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(lHalf, -top, sHalf).tex(1F, .5F + -sHalfT).endVertex();
		vb.pos(lHalf, -top, -sHalf).tex(1F, .5F + sHalfT).endVertex();
		vb.pos(lHalf, top, -sHalf).tex(1F - tHeight, .5F + sHalfT).endVertex();
		vb.pos(lHalf, top, sHalf).tex(1F - tHeight, .5F + -sHalfT).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-lHalf, top, sHalf).tex(tHeight, .5F + -sHalfT).endVertex();
		vb.pos(-lHalf, top, -sHalf).tex(tHeight, .5F + sHalfT).endVertex();
		vb.pos(-lHalf, -top, -sHalf).tex(0, .5F + sHalfT).endVertex();
		vb.pos(-lHalf, -top, sHalf).tex(0, .5F + -sHalfT).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, 0).endVertex();
		vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, 0).endVertex();
		vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, tHeight).endVertex();
		vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, tHeight).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, 1F - tHeight).endVertex();
		vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, 1F - tHeight).endVertex();
		vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, 1).endVertex();
		vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, 1).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
		vb.pos(lHalf, top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(lHalf, -top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
		vb.pos(-lHalf, -top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(-lHalf, top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
		//Tessellator.getInstance().draw();


		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
		vb.pos(lHalf, -top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
		vb.pos(lHalf, top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
		vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
		//Tessellator.getInstance().draw();

		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
		vb.pos(-lHalf, top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
		vb.pos(-lHalf, -top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
		vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
		Tessellator.getInstance().draw();

		//Prongs

		GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);

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
		GlStateManager.rotate(45, 0, 1, 0);

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

		GlStateManager.color(1F, 1F, 1F);

		GlStateManager.popMatrix();
	}
}
