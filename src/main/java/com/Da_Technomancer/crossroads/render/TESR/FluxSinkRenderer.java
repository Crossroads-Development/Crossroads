package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxSinkTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FluxSinkRenderer extends TileEntityRenderer<FluxSinkTileEntity>{

	private static final ResourceLocation TEXT = new ResourceLocation(Crossroads.MODID, "textures/model/flux_sink_core.png");

	@Override
	public void render(FluxSinkTileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
		if(te == null){
			return;
		}

		double runtime = te.getRunDuration(partialTicks);
		if(runtime <= 0){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.translated(x + 0.5D, y + 0.5D, z + 0.5D);

		Minecraft.getInstance().getTextureManager().bindTexture(TEXT);

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder vb = tes.getBuffer();

		//TODO

		//Render an icosahedron

		//Angle of the axis of symmetry that each segment of the icosahedron is rotated about to form the overall shape
		//Found by brute force. If someone wants to do the geometric proof to correct the fourth decimal point, be my guest

		float scale = (float) Math.min(1D, runtime / 60D) * 2.5F;//Expand slowly to full size when starting up

		GlStateManager.pushMatrix();

		//Wobble effect
		GlStateManager.rotated(8 * Math.sin(runtime / 40D), 1, 0, 0);
		GlStateManager.rotated(8 * Math.cos(runtime / 40D), 0, 0, 1);

		GlStateManager.color4f(1, 1, 1, 0.9F);
		drawIcos(vb, scale, 0, 0);

		CRRenderUtil.setMediumLighting();
		GlStateManager.color4f(1, 1, 1, 0.2F);
		drawIcos(vb, scale + 0.15F + (float) Math.sin(runtime / 20D) * 0.08F, 1, 1);//See-through outer shell

		GlStateManager.popMatrix();


		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	private static final double symmetryAxisAngle = 31.722F;
	private static final double symmetryAxisX = Math.cos(Math.toRadians(symmetryAxisAngle));
	private static final double symmetryAxisY = Math.sin(Math.toRadians(symmetryAxisAngle));
	private static final float goldRatio = (float) (1F + Math.sqrt(5)) / 2F;//The golden ratio

	private static void drawIcos(BufferBuilder vb, float scale, float cornerU, float cornerV){
		final float smallLen = scale / 2;
		final float largeLen = goldRatio * smallLen;

		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 5; j++){
				vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
				vb.pos(0, largeLen, smallLen).tex(cornerU, cornerV).endVertex();
				vb.pos(0, largeLen, -smallLen).tex(1, 0).endVertex();
				vb.pos(largeLen, smallLen, 0).tex(0, 1).endVertex();

				vb.pos(0, largeLen, smallLen).tex(cornerU, cornerV).endVertex();
				vb.pos(0, largeLen, -smallLen).tex(1, 0).endVertex();
				vb.pos(-largeLen, smallLen, 0).tex(0, 1).endVertex();
				Tessellator.getInstance().draw();

				GlStateManager.rotated(72, symmetryAxisX, symmetryAxisY, 0);
			}
			GlStateManager.rotated(36, symmetryAxisX, symmetryAxisY, 0);
			GlStateManager.rotated(180, -symmetryAxisY, symmetryAxisX, 0);
		}
	}
}
