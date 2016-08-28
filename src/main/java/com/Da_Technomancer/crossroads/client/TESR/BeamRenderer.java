package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

/** All blocks using BeamRenderer MUST return false to isOpaqueCube */
public class BeamRenderer extends TileEntitySpecialRenderer<BeamRenderTE>{
	
	@Override
	public void renderTileEntityAt(BeamRenderTE beam, double x, double y, double z, float partialTicks, int destroyStage){
		if(!beam.getWorld().isBlockLoaded(beam.getPos(), false) || beam.getBeam() == null){
			return;
		}

		Triple<Color, Integer, Integer> trip = beam.getBeam();
		EnumFacing dir = beam.getWorld().getBlockState(beam.getPos()).getValue(Properties.FACING);
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);
		GlStateManager.color(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
		GlStateManager.disableLighting();

		switch(dir){
			case DOWN:
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, 0, -1);
				break;
			case UP:
				break;
			case EAST:
				GlStateManager.rotate(-90, 0, 0, 1);
				GlStateManager.translate(-1, 0, 0);
				break;
			case WEST:
				GlStateManager.rotate(90, 0, 0, 1);
				break;
			case NORTH:
				GlStateManager.rotate(-90, 1, 0, 0);
				break;
			case SOUTH:
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.translate(0, 0, -1);
				break;
		}

		Tessellator tes = Tessellator.getInstance();
		VertexBuffer buf = tes.getBuffer();

		final double small = .5D - (trip.getRight().doubleValue() / 16D);
		final double big = .5D + (trip.getRight().doubleValue() / 16D);
		final int length = trip.getMiddle().intValue();

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

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean isGlobalRenderer(BeamRenderTE te){
		return true;
	}
}
