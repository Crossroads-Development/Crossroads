package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;
import java.util.Random;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.tileentities.magic.BeaconHarnessTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/** All blocks using BeamRenderer MUST return false to isOpaqueCube */
public class BeaconHarnessRenderer extends TileEntitySpecialRenderer<BeaconHarnessTileEntity>{

	private static final Random rand = new Random();
	
	@Override
	public void renderTileEntityAt(BeaconHarnessTileEntity beam, double x, double y, double z, float partialTicks, int destroyStage){
		if(!beam.getWorld().isBlockLoaded(beam.getPos(), false) || beam.getBeam() == null){
			return;
		}

		Triple<Color, Integer, Integer>[] trip = beam.getBeam();
		float brightX = OpenGlHelper.lastBrightnessX;
		float brightY = OpenGlHelper.lastBrightnessY;
		
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer buf = tes.getBuffer();
		
		for(int dir = 0; dir < 2; ++dir){

			if(trip[dir] != null){
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.translate(x, y, z);
				GlStateManager.color(trip[dir].getLeft().getRed() / 255F, trip[dir].getLeft().getGreen() / 255F, trip[dir].getLeft().getBlue() / 255F);
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

				final double small = 0 - (trip[dir].getRight().doubleValue() / 16D);
				final double big = 0 + (trip[dir].getRight().doubleValue() / 16D);
				final int length = trip[dir].getMiddle().intValue();

				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				//+Z
				buf.pos(small, length, big).tex(1, 0).endVertex();
				buf.pos(small, 0, big).tex(1, length).endVertex();
				buf.pos(big, 0, big).tex(0, length).endVertex();
				buf.pos(big, length, big).tex(0, 0).endVertex();
				//-Z
				buf.pos(big, length, small).tex(1, 0).endVertex();
				buf.pos(big, 0, small).tex(1, length).endVertex();
				buf.pos(small, 0, small).tex(0, length).endVertex();
				buf.pos(small, length, small).tex(0, 0).endVertex();
				//-X
				buf.pos(small, length, small).tex(1, 0).endVertex();
				buf.pos(small, 0, small).tex(1, length).endVertex();
				buf.pos(small, 0, big).tex(0, length).endVertex();
				buf.pos(small, length, big).tex(0, 0).endVertex();
				//+X
				buf.pos(big, length, big).tex(1, 0).endVertex();
				buf.pos(big, 0, big).tex(1, length).endVertex();
				buf.pos(big, 0, small).tex(0, length).endVertex();
				buf.pos(big, length, small).tex(0, 0).endVertex();
				tes.draw();
				
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
				GlStateManager.enableLighting();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
		
		if(trip[1] == null){
			return;
		}
		
		float ticks = beam.getWorld().getTotalWorldTime() % 10;
		
		if(ticks == 0 && !beam.renderSet){
			beam.renderSet = true;
			float diagMult = (float) Math.sqrt(8);
			for(int i = 0; i < 8; i++){
				beam.renderOld[i] = beam.renderNew[i];
				beam.renderNew[i] = (i % 2 == 0) ? rand.nextFloat() / 2F : rand.nextFloat() / diagMult;
			}
		}else if(ticks == 1){
			beam.renderSet = false;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);
		GlStateManager.color(trip[1].getLeft().getRed() / 255F, trip[1].getLeft().getGreen() / 255F, trip[1].getLeft().getBlue() / 255F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
		GlStateManager.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		GlStateManager.disableCull();
		
		float height = .4F + Math.abs(((.02F * beam.getWorld().getTotalWorldTime()) % .4F) - .2F);
		float change = ticks / 10F;
		
		buf.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		buf.pos(.5F - ((beam.renderOld[0] * (1F - change)) + beam.renderNew[0] * change), height, .5F).tex(0, 0).endVertex();
		buf.pos(.5F - ((beam.renderOld[1] * (1F - change)) + beam.renderNew[1] * change), height, .5F - ((beam.renderOld[1] * (1F - change)) + beam.renderNew[1] * change)).tex(0, 0).endVertex();
		buf.pos(.5F, height, .5F - ((beam.renderOld[2] * (1F - change)) + beam.renderNew[2] * change)).tex(0, 0).endVertex();
		buf.pos(.5F + ((beam.renderOld[3] * (1F - change)) + beam.renderNew[3] * change), height, .5F - ((beam.renderOld[3] * (1F - change)) + beam.renderNew[3] * change)).tex(0, 0).endVertex();
		buf.pos(.5F + ((beam.renderOld[4] * (1F - change)) + beam.renderNew[4] * change), height, .5F).tex(0, 0).endVertex();
		buf.pos(.5F + ((beam.renderOld[5] * (1F - change)) + beam.renderNew[5] * change), height, .5F + ((beam.renderOld[5] * (1F - change)) + beam.renderNew[5] * change)).tex(0, 0).endVertex();
		buf.pos(.5F, height, .5F + ((beam.renderOld[6] * (1F - change)) + beam.renderNew[6] * change)).tex(0, 0).endVertex();
		buf.pos(.5F - ((beam.renderOld[7] * (1F - change)) + beam.renderNew[7] * change), height, .5F + ((beam.renderOld[7] * (1F - change)) + beam.renderNew[7] * change)).tex(0, 0).endVertex();
		tes.draw();
		
		GlStateManager.enableCull();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		GlStateManager.enableLighting();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(BeaconHarnessTileEntity te){
		return true;
	}
}
