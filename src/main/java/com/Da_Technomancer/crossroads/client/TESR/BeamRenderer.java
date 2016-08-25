package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

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
		GlStateManager.disableLighting();
		GlStateManager.translate(x, y, z);
		
		for(int i = 1; i <= trip.getMiddle(); i++){
			GlStateManager.translate(dir == EnumFacing.EAST ? 1 : dir == EnumFacing.WEST ? -1 : 0, dir == EnumFacing.UP ? 1 : dir == EnumFacing.DOWN ? -1 : 0, dir == EnumFacing.SOUTH ? 1 : dir == EnumFacing.NORTH ? -1 : 0);
			//TODO render
		}
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
