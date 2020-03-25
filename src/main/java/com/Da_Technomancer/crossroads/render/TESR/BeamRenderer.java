package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.API.templates.IBeamRenderTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * All blocks using BeamRenderer MUST return false to isOpaqueCube 
 */
public class BeamRenderer<T extends TileEntity & IBeamRenderTE> extends TileEntityRenderer<T>{

	@Override
	public void render(T beam, double x, double y, double z, float partialTicks, int destroyStage){
		if(!beam.getWorld().isBlockLoaded(beam.getPos())){
			return;
		}

		int[] packets = beam.getRenderedBeams();

		for(int dir = 0; dir < 6; dir++){
			if(packets[dir] != 0){
				Triple<Color, Integer, Integer> trip = BeamManager.getTriple(packets[dir]);

				GlStateManager.pushMatrix();
				GlStateManager.pushLightingAttributes();
				GlStateManager.translated(x + 0.5F, y + 0.5F, z + 0.5F);
				Minecraft.getInstance().getTextureManager().bindTexture(BeamUtil.BEAM_TEXT);
				GlStateManager.disableLighting();
				GlStateManager.disableCull();

				CRRenderUtil.setBrightLighting();

				switch(dir){
					case 0:
						GlStateManager.rotated(180, 1, 0, 0);
						break;
					case 1:
						break;
					case 2:
						GlStateManager.rotated(-90, 1, 0, 0);
						break;
					case 3:
						GlStateManager.rotated(90, 1, 0, 0);
						break;
					case 4:
						GlStateManager.rotated(90, 0, 0, 1);
						break;
					case 5:
						GlStateManager.rotated(-90, 0, 0, 1);
						break;
				}

				if(CRConfig.rotateBeam.get()){
					GlStateManager.rotated((partialTicks + (float) beam.getWorld().getGameTime()) * 2F, 0, 1, 0);
				}else{
					GlStateManager.rotated(45, 0, 1, 0);//Constant 45* angle
				}
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();

				double halfWidth = trip.getRight().doubleValue() / 16D / Math.sqrt(2);//Convert diagonal radius to side length
				int length = trip.getMiddle();

				float[] col = new float[4];
				col[0] = trip.getLeft().getRed() / 255F;
				col[1] = trip.getLeft().getGreen() / 255F;
				col[2] = trip.getLeft().getBlue() / 255F;
				col[3] = 1F;

				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				//+Z
				buf.pos(-halfWidth, length, halfWidth).tex(1, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, 0, halfWidth).tex(1, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, 0, halfWidth).tex(0, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, length, halfWidth).tex(0, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				//-Z
				buf.pos(halfWidth, length, -halfWidth).tex(1, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, 0, -halfWidth).tex(1, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, 0, -halfWidth).tex(0, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, length, -halfWidth).tex(0, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				//-X
				buf.pos(-halfWidth, length, -halfWidth).tex(1, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, 0, -halfWidth).tex(1, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, 0, halfWidth).tex(0, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, length, halfWidth).tex(0, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				//+X
				buf.pos(halfWidth, length, halfWidth).tex(1, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, 0, halfWidth).tex(1, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, 0, -halfWidth).tex(0, length).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, length, -halfWidth).tex(0, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				tes.draw();

				//Ends
				Minecraft.getInstance().getTextureManager().bindTexture(BeamUtil.BEAM_END_TEXT);

				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				//Out end
				buf.pos(-halfWidth, length, halfWidth).tex(1, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, length, -halfWidth).tex(1, 1).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, length, -halfWidth).tex(0, 1).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, length, halfWidth).tex(0, 0).color(col[0], col[1], col[2], col[3]).endVertex();

				//Start end
				buf.pos(-halfWidth, 0, halfWidth).tex(1, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(-halfWidth, 0, -halfWidth).tex(1, 1).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, 0, -halfWidth).tex(0, 1).color(col[0], col[1], col[2], col[3]).endVertex();
				buf.pos(halfWidth, 0, halfWidth).tex(0, 0).color(col[0], col[1], col[2], col[3]).endVertex();
				tes.draw();

//				CRRenderUtil.restoreLighting(lighting);
				GlStateManager.enableCull();
//				GlStateManager.enableBlend();
				GlStateManager.enableLighting();
				GlStateManager.popAttributes();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean isGlobalRenderer(T te){
		return true;
	}
}
