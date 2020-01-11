package com.Da_Technomancer.crossroads.render.TESR.models;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ModelPump{

	public static void renderScrew(){
		//TODO check texture mapping and scaling

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