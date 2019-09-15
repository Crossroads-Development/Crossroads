package com.Da_Technomancer.crossroads.render.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.API.templates.BeamRenderTEBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/** 
 * All blocks using BeamRenderer MUST return false to isOpaqueCube 
 */
public class BeamRenderer extends TileEntityRenderer<BeamRenderTEBase>{

	@Override
	public void render(BeamRenderTEBase beam, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!beam.getWorld().isBlockLoaded(beam.getPos(), false)){
			return;
		}

		int[] packets = beam.getRenderedBeams();
		float brightX = OpenGlHelper.lastBrightnessX;
		float brightY = OpenGlHelper.lastBrightnessY;
		
		for(int dir = 0; dir < 6; ++dir){
			if(packets[dir] != 0){
				Triple<Color, Integer, Integer> trip = BeamManager.getTriple(packets[dir]);
				
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.translate(x, y, z);
				GlStateManager.color(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
				Minecraft.getInstance().getTextureManager().bindTexture(BeaconTileEntityRenderer.TEXTURE_BEACON_BEAM);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				
				switch(dir){
					case 0:
						GlStateManager.rotate(180, 1, 0, 0);
						GlStateManager.translate(.5D, -.5D, -.5D);
						break;
					case 1:
						GlStateManager.translate(.5D, .5D, .5D);
						break;
					case 5:
						GlStateManager.rotate(-90, 0, 0, 1);
						GlStateManager.translate(-.5D, .5D, .5D);
						break;
					case 4:
						GlStateManager.rotate(90, 0, 0, 1);
						GlStateManager.translate(.5D, -.5D, .5D);
						break;
					case 2:
						GlStateManager.rotate(-90, 1, 0, 0);
						GlStateManager.translate(.5D, -.5D, .5D);
						break;
					case 3:
						GlStateManager.rotate(90, 1, 0, 0);
						GlStateManager.translate(.5D, .5D, -.5D);
						break;
				}

				if(CrossroadsConfig.rotateBeam.getBoolean()){
					GlStateManager.rotate((partialTicks + (float) beam.getWorld().getTotalWorldTime()) * 2F, 0, 1, 0);
				}
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();

				double halfWidth = trip.getRight().doubleValue() / (Math.sqrt(2D) * 16D);
				int length = trip.getMiddle();

				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				//+Z
				buf.pos(-halfWidth, length, halfWidth).tex(1, 0).endVertex();
				buf.pos(-halfWidth, 0, halfWidth).tex(1, length).endVertex();
				buf.pos(halfWidth, 0, halfWidth).tex(0, length).endVertex();
				buf.pos(halfWidth, length, halfWidth).tex(0, 0).endVertex();
				//-Z
				buf.pos(halfWidth, length, -halfWidth).tex(1, 0).endVertex();
				buf.pos(halfWidth, 0, -halfWidth).tex(1, length).endVertex();
				buf.pos(-halfWidth, 0, -halfWidth).tex(0, length).endVertex();
				buf.pos(-halfWidth, length, -halfWidth).tex(0, 0).endVertex();
				//-X
				buf.pos(-halfWidth, length, -halfWidth).tex(1, 0).endVertex();
				buf.pos(-halfWidth, 0, -halfWidth).tex(1, length).endVertex();
				buf.pos(-halfWidth, 0, halfWidth).tex(0, length).endVertex();
				buf.pos(-halfWidth, length, halfWidth).tex(0, 0).endVertex();
				//+X
				buf.pos(halfWidth, length, halfWidth).tex(1, 0).endVertex();
				buf.pos(halfWidth, 0, halfWidth).tex(1, length).endVertex();
				buf.pos(halfWidth, 0, -halfWidth).tex(0, length).endVertex();
				buf.pos(halfWidth, length, -halfWidth).tex(0, 0).endVertex();
				tes.draw();
				
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
				GlStateManager.enableLighting();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean isGlobalRenderer(BeamRenderTEBase te){
		return true;
	}
}
