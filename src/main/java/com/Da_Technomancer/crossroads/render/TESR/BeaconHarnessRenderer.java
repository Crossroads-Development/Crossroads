package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BeaconHarnessRenderer extends LinkLineRenderer<BeaconHarnessTileEntity>{

	private static final ResourceLocation INNER_TEXT = new ResourceLocation(Crossroads.MODID, "textures/block/block_copshowium.png");
	private static final ResourceLocation OUTER_TEXT = new ResourceLocation(Crossroads.MODID, "textures/block/block_pure_quartz.png");

	@Override
	public void render(BeaconHarnessTileEntity beam, double x, double y, double z, float partialTicks, int destroyStage){
		if(!beam.getWorld().isBlockLoaded(beam.getPos())){
			return;
		}

		super.render(beam, x, y, z, partialTicks, destroyStage);

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();

		float angle = 0;

		//Render output beam
		int[] beamPacket = beam.getRenderedBeams();
		//Beacon harness only outputs beams down
		Triple<Color, Integer, Integer> trip = BeamManager.getTriple(beamPacket[0]);
		if(trip.getRight() != 0){
			//We are running. Calculate angle for rods
			angle = calcAngle(beam, partialTicks);

			GlStateManager.pushMatrix();
			GlStateManager.pushLightingAttributes();
			GlStateManager.translated(x + 0.5D, y + 0.5D, z + 0.5D);
			GlStateManager.color3f(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
			GlStateManager.disableLighting();
			GlStateManager.rotated(180, 1, 0, 0);
//			GlStateManager.translated(.5D, -.5D, -.5D);

			CRRenderUtil.setBrightLighting();
			Minecraft.getInstance().getTextureManager().bindTexture(BeamUtil.BEAM_TEXT);

			if(CRConfig.rotateBeam.get()){
				GlStateManager.rotated((beam.getWorld().getGameTime() + partialTicks) * 2F, 0, 1, 0);
			}

			final double rad = trip.getRight().doubleValue() / 16D / Math.sqrt(2);//Convert diagonal radius to side length
			final int length = trip.getMiddle();
			final float stOffset = 7F / 16F;

			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			//+Z
			buf.pos(-rad, length, rad).tex(1, 0).endVertex();
			buf.pos(-rad, stOffset, rad).tex(1, length - stOffset).endVertex();
			buf.pos(rad, stOffset, rad).tex(0, length - stOffset).endVertex();
			buf.pos(rad, length, rad).tex(0, 0).endVertex();
			//-Z
			buf.pos(rad, length, -rad).tex(1, 0).endVertex();
			buf.pos(rad, stOffset, -rad).tex(1, length - stOffset).endVertex();
			buf.pos(-rad, stOffset, -rad).tex(0, length - stOffset).endVertex();
			buf.pos(-rad, length, -rad).tex(0, 0).endVertex();
			//-X
			buf.pos(-rad, length, -rad).tex(1, 0).endVertex();
			buf.pos(-rad, stOffset, -rad).tex(1, length - stOffset).endVertex();
			buf.pos(-rad, stOffset, rad).tex(0, length - stOffset).endVertex();
			buf.pos(-rad, length, rad).tex(0, 0).endVertex();
			//+X
			buf.pos(rad, length, rad).tex(1, 0).endVertex();
			buf.pos(rad, stOffset, rad).tex(1, length - stOffset).endVertex();
			buf.pos(rad, stOffset, -rad).tex(0, length - stOffset).endVertex();
			buf.pos(rad, length, -rad).tex(0, 0).endVertex();
			tes.draw();

			//Ends
			Minecraft.getInstance().getTextureManager().bindTexture(BeamUtil.BEAM_END_TEXT);
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			//Out end
			buf.pos(-rad, length, rad).tex(1, 0).endVertex();
			buf.pos(-rad, length, -rad).tex(1, 1).endVertex();
			buf.pos(rad, length, -rad).tex(0, 1).endVertex();
			buf.pos(rad, length, rad).tex(0, 0).endVertex();

			//Start end
			buf.pos(-rad, stOffset, rad).tex(1, 0).endVertex();
			buf.pos(-rad, stOffset, -rad).tex(1, 1).endVertex();
			buf.pos(rad, stOffset, -rad).tex(0, 1).endVertex();
			buf.pos(rad, stOffset, rad).tex(0, 0).endVertex();
			tes.draw();

			GlStateManager.color3f(1, 1, 1);
//				CRRenderUtil.restoreLighting(lighting);
			GlStateManager.enableLighting();
			GlStateManager.popAttributes();
			GlStateManager.popMatrix();
		}

		//Revolving rods
		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + 0.5D, y, z + 0.5D);
//		CRRenderUtil.setMediumLighting();

		float smallOffset = 0.0928F;
		float medOffset = 4F / 16;
		float largeOffset = 6F / 16F;

		GlStateManager.rotated(angle, 0, 1, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(INNER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, smallOffset, smallOffset);
		addRod(buf, smallOffset, -smallOffset);
		addRod(buf, -smallOffset, -smallOffset);
		addRod(buf, -smallOffset, smallOffset);
		tes.draw();

		GlStateManager.rotated(-2F * angle, 0, 1, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(OUTER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, medOffset, largeOffset);
		addRod(buf, medOffset, -largeOffset);
		addRod(buf, -medOffset, largeOffset);
		addRod(buf, -medOffset, -largeOffset);
		addRod(buf, largeOffset, medOffset);
		addRod(buf, largeOffset, -medOffset);
		addRod(buf, -largeOffset, medOffset);
		addRod(buf, -largeOffset, -medOffset);
		tes.draw();

//		CRRenderUtil.restoreLighting(prev);
		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}

	private static void addRod(BufferBuilder buf, double x, double z){
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

	private static float calcAngle(BeaconHarnessTileEntity te, float partialTicks){
		return (float) Math.toDegrees((te.getWorld().getGameTime() % 20 + partialTicks) * (float) Math.PI / 20F);
	}

	@Override
	public boolean isGlobalRenderer(BeaconHarnessTileEntity te){
		return true;
	}
}
