package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
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
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/** 
 * All blocks using BeamRenderer MUST return false to isOpaqueCube 
 */
public class BeamRenderer<T extends TileEntity & IBeamRenderTE> extends TileEntityRenderer<T>{

	protected static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

	@Override
	public void render(T beam, double x, double y, double z, float partialTicks, int destroyStage){
		if(!beam.getWorld().isBlockLoaded(beam.getPos())){
			return;
		}

		int[] packets = beam.getRenderedBeams();

		for(int dir = 0; dir < 6; ++dir){
			if(packets[dir] != 0){
				Triple<Color, Integer, Integer> trip = BeamManager.getTriple(packets[dir]);
				
				GlStateManager.pushMatrix();
				GlStateManager.pushLightingAttributes();
				GlStateManager.translated(x, y, z);
				GlStateManager.color3f(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
				Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_BEACON_BEAM);
				GlStateManager.disableLighting();
				Pair<Float, Float> lighting = CRRenderUtil.disableLighting();

				switch(dir){
					case 0:
						GlStateManager.rotated(180, 1, 0, 0);
						GlStateManager.translated(.5D, -.5D, -.5D);
						break;
					case 1:
						GlStateManager.translated(.5D, .5D, .5D);
						break;
					case 5:
						GlStateManager.rotated(-90, 0, 0, 1);
						GlStateManager.translated(-.5D, .5D, .5D);
						break;
					case 4:
						GlStateManager.rotated(90, 0, 0, 1);
						GlStateManager.translated(.5D, -.5D, .5D);
						break;
					case 2:
						GlStateManager.rotated(-90, 1, 0, 0);
						GlStateManager.translated(.5D, -.5D, .5D);
						break;
					case 3:
						GlStateManager.rotated(90, 1, 0, 0);
						GlStateManager.translated(.5D, .5D, -.5D);
						break;
				}

				if(CRConfig.rotateBeam.get()){
					GlStateManager.rotated((partialTicks + (float) beam.getWorld().getGameTime()) * 2F, 0, 1, 0);
				}else{
					GlStateManager.rotated(45, 0, 1, 0);//Constant 45* angle
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
				
				CRRenderUtil.enableLighting(lighting);
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
