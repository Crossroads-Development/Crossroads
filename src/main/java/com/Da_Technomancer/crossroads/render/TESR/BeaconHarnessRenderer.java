package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

public class BeaconHarnessRenderer extends TileEntitySpecialRenderer<BeaconHarnessTileEntity>{

	private static final Random rand = new Random();
	
	@Override
	public void render(BeaconHarnessTileEntity beam, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!beam.getWorld().isBlockLoaded(beam.getPos(), false)){
			return;
		}

		int[] packet = beam.getRenderedBeams();
		float brightX = OpenGlHelper.lastBrightnessX;
		float brightY = OpenGlHelper.lastBrightnessY;
		
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();
		
		for(int dir = 0; dir < 2; ++dir){
			Triple<Color, Integer, Integer> trip = BeamManager.getTriple(packet[dir]);
			if(trip.getMiddle() != 0){
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.translate(x, y, z);
				GlStateManager.color(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
				Minecraft.getMinecraft().getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
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
				}

				if(ModConfig.rotateBeam.getBoolean()){
					GlStateManager.rotate(beam.getWorld().getTotalWorldTime() * 2, 0, 1, 0);
				}

				final double rad = trip.getRight().doubleValue() / 16D / Math.sqrt(2D);
				final int length = trip.getMiddle();

				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				//+Z
				buf.pos(-rad, length, rad).tex(1, 0).endVertex();
				buf.pos(-rad, 0, rad).tex(1, length).endVertex();
				buf.pos(rad, 0, rad).tex(0, length).endVertex();
				buf.pos(rad, length, rad).tex(0, 0).endVertex();
				//-Z
				buf.pos(rad, length, -rad).tex(1, 0).endVertex();
				buf.pos(rad, 0, -rad).tex(1, length).endVertex();
				buf.pos(-rad, 0, -rad).tex(0, length).endVertex();
				buf.pos(-rad, length, -rad).tex(0, 0).endVertex();
				//-X
				buf.pos(-rad, length, -rad).tex(1, 0).endVertex();
				buf.pos(-rad, 0, -rad).tex(1, length).endVertex();
				buf.pos(-rad, 0, rad).tex(0, length).endVertex();
				buf.pos(-rad, length, rad).tex(0, 0).endVertex();
				//+X
				buf.pos(rad, length, rad).tex(1, 0).endVertex();
				buf.pos(rad, 0, rad).tex(1, length).endVertex();
				buf.pos(rad, 0, -rad).tex(0, length).endVertex();
				buf.pos(rad, length, -rad).tex(0, 0).endVertex();
				tes.draw();
				
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
				GlStateManager.enableLighting();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
		
		if(packet[1] == 0){
			return;
		}
		
		float ticks = beam.getWorld().getTotalWorldTime() % 20 + partialTicks;
		
		if(ticks < 1F && !beam.renderSet){
			beam.renderSet = true;
			float diagMult = 2F;//* (float) Math.sqrt(2D);
			for(int i = 0; i < 8; i++){
				beam.renderOld[i] = beam.renderNew[i];
				beam.renderNew[i] = 2F + 6F * ((i % 2 == 0) ? rand.nextFloat() / 2F : rand.nextFloat() / diagMult);
			}
		}else if(ticks > 1F && ticks < 2F){
			beam.renderSet = false;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);

		Color col = BeamManager.getTriple(packet[1]).getLeft();
		GlStateManager.color(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F, 0.25F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		float height = 0.2F * (float) Math.sin(0.05F * beam.getWorld().getTotalWorldTime());
		float change = ticks / 20F;

		buf.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		buf.pos(.5F - ((beam.renderOld[0] * (1F - change)) + beam.renderNew[0] * change), 0.5F + height, .5F).tex(0, 0).endVertex();
		buf.pos(.5F - ((beam.renderOld[1] * (1F - change)) + beam.renderNew[1] * change), 0.5F + height, .5F - ((beam.renderOld[1] * (1F - change)) + beam.renderNew[1] * change)).tex(0, 0).endVertex();
		buf.pos(.5F, 0.5F + height, .5F - ((beam.renderOld[2] * (1F - change)) + beam.renderNew[2] * change)).tex(0, 0).endVertex();
		buf.pos(.5F + ((beam.renderOld[3] * (1F - change)) + beam.renderNew[3] * change), 0.5F + height, .5F - ((beam.renderOld[3] * (1F - change)) + beam.renderNew[3] * change)).tex(0, 0).endVertex();
		buf.pos(.5F + ((beam.renderOld[4] * (1F - change)) + beam.renderNew[4] * change), 0.5F + height, .5F).tex(0, 0).endVertex();
		buf.pos(.5F + ((beam.renderOld[5] * (1F - change)) + beam.renderNew[5] * change), 0.5F + height, .5F + ((beam.renderOld[5] * (1F - change)) + beam.renderNew[5] * change)).tex(0, 0).endVertex();
		buf.pos(.5F, 0.5F + height, .5F + ((beam.renderOld[6] * (1F - change)) + beam.renderNew[6] * change)).tex(0, 0).endVertex();
		buf.pos(.5F - ((beam.renderOld[7] * (1F - change)) + beam.renderNew[7] * change), 0.5F + height, .5F + ((beam.renderOld[7] * (1F - change)) + beam.renderNew[7] * change)).tex(0, 0).endVertex();
		tes.draw();

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(BeaconHarnessTileEntity te){
		return true;
	}
}
