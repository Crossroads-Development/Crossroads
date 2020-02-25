package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BeaconHarnessRenderer extends TileEntityRenderer<BeaconHarnessTileEntity>{

	private static final ResourceLocation INNER_TEXT = new ResourceLocation(Crossroads.MODID, "textures/block/block_copshowium.png");
	private static final ResourceLocation OUTER_TEXT = new ResourceLocation(Crossroads.MODID, "textures/block/block_pure_quartz.png");
	private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

	@Override
	public void render(BeaconHarnessTileEntity beam, double x, double y, double z, float partialTicks, int destroyStage){
		if(!beam.getWorld().isBlockLoaded(beam.getPos())){
			return;
		}

		super.render(beam, x, y, z, partialTicks, destroyStage);

		int[] packet = beam.getRenderedBeams();
		
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();

		//Beams
		for(int dir = 0; dir < 2; ++dir){
			Triple<Color, Integer, Integer> trip = BeamManager.getTriple(packet[dir]);
			if(trip.getMiddle() != 0){
				GlStateManager.pushMatrix();
				GlStateManager.pushLightingAttributes();
				GlStateManager.translated(x, y, z);
				GlStateManager.color3f(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
				Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_BEACON_BEAM);
				GlStateManager.disableLighting();
				CRRenderUtil.setBrightLighting();
				
				switch(dir){
					case 0:
						GlStateManager.rotated(180, 1, 0, 0);
						GlStateManager.translated(.5D, -.5D, -.5D);
						break;
					case 1:
						GlStateManager.translated(.5D, .5D, .5D);
						break;
				}

				if(CRConfig.rotateBeam.get()){
					GlStateManager.rotated(beam.getWorld().getGameTime() * 2, 0, 1, 0);
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

				GlStateManager.color3f(1, 1, 1);
//				CRRenderUtil.restoreLighting(lighting);
				GlStateManager.enableLighting();
				GlStateManager.popAttributes();
				GlStateManager.popMatrix();
			}
		}
		
		if(packet[0] != 0){
			beam.angle += 9F * partialTicks;
		}

		//Revolving rods
		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + 0.5D, y, z + 0.5D);
		CRRenderUtil.setMediumLighting();

		float smallOffset = 0.0928F;
		float largeOffset = 5F / 16F;

		GlStateManager.rotated(beam.angle, 0, 1, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(INNER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, smallOffset, smallOffset);
		addRod(buf, smallOffset, -smallOffset);
		addRod(buf, -smallOffset, -smallOffset);
		addRod(buf, -smallOffset, smallOffset);
		tes.draw();

		GlStateManager.rotated(-2F * beam.angle, 0, 1, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(OUTER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, smallOffset, largeOffset);
		addRod(buf, smallOffset, -largeOffset);
		addRod(buf, -smallOffset, largeOffset);
		addRod(buf, -smallOffset, -largeOffset);
		addRod(buf, largeOffset, smallOffset);
		addRod(buf, largeOffset, -smallOffset);
		addRod(buf, -largeOffset, smallOffset);
		addRod(buf, -largeOffset, -smallOffset);
		tes.draw();

//		CRRenderUtil.restoreLighting(prev);
		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();



		/* Old "colored starburst" renderer


		float ticks = beam.getWorld().getGameTime() % 20 + partialTicks;
		
		if(ticks < 1F && !beam.renderSet){
			beam.renderSet = true;
			float diagMult = 2F;//* (float) Math.sqrt(2D);
			for(int i = 0; i < 8; i++){
				beam.renderOld[i] = beam.renderNew[i];
				beam.renderNew[i] = 0.25F * ((i % 2 == 0) ? rand.nextFloat() / 2F : rand.nextFloat() / diagMult);
			}
		}else if(ticks > 1F && ticks < 2F){
			beam.renderSet = false;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.translated(x, y, z);

		Color col = BeamManager.getTriple(packet[1]).getLeft();
		GlStateManager.color(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F);
		Minecraft.getInstance().getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

		float height = 0.2F * (float) Math.sin(0.05F * beam.getWorld().getGameTime());
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
		GlStateManager.color(1, 1, 1);
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();

		*/
	}

	private void addRod(BufferBuilder buf, double x, double z){
		float rad = 1F / 16F;
		float minY = 1F / 16F;
		float maxY = 15F / 16F;
		buf.pos(x - rad, minY, z - rad).tex(0, 0).endVertex();
		buf.pos(x - rad, maxY, z - rad).tex(0, 1).endVertex();
		buf.pos(x + rad, maxY, z - rad).tex(2F * rad, 1).endVertex();
		buf.pos(x + rad, minY, z - rad).tex(2F * rad, 0).endVertex();

		buf.pos(x - rad, minY, z + rad).tex(0, 0).endVertex();
		buf.pos(x + rad, minY, z + rad).tex(2F * rad, 0).endVertex();
		buf.pos(x + rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
		buf.pos(x - rad, maxY, z + rad).tex(0, 1).endVertex();


		buf.pos(x - rad, minY, z - rad).tex(0, 0).endVertex();
		buf.pos(x - rad, minY, z + rad).tex(2F * rad, 0).endVertex();
		buf.pos(x - rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
		buf.pos(x - rad, maxY, z - rad).tex(0, 1).endVertex();

		buf.pos(x + rad, minY, z - rad).tex(0, 0).endVertex();
		buf.pos(x + rad, maxY, z - rad).tex(0, 1).endVertex();
		buf.pos(x + rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
		buf.pos(x + rad, minY, z + rad).tex(2F * rad, 0).endVertex();
	}

	@Override
	public boolean isGlobalRenderer(BeaconHarnessTileEntity te){
		return true;
	}
}
