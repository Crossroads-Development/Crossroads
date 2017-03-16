package com.Da_Technomancer.crossroads.client.TESR;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;

public class GatewayFrameRenderer extends TileEntitySpecialRenderer<GatewayFrameTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/model/gateway.png");

	@Override
	public void renderTileEntityAt(GatewayFrameTileEntity frame, double x, double y, double z, float partialTicks, int destroyStage){
		if(frame == null || !frame.getWorld().isBlockLoaded(frame.getPos(), false) || frame.getWorld().getBlockState(frame.getPos()) != ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP)){
			return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.color(1, 1, 1, frame.alpha);
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5D, 0, .5D);
		if(frame.getAlignment() == Axis.Z){
			GlStateManager.rotate(90, 0, 1, 0);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		VertexBuffer vb = Tessellator.getInstance().getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(1.5D, -3, 0).tex(1, 0).endVertex();
		vb.pos(-1.5D, -3, 0).tex(0, 0).endVertex();
		vb.pos(-1.5D, 0, 0).tex(0, 1).endVertex();
		vb.pos(1.5D, 0, 0).tex(1, 1).endVertex();
		Tessellator.getInstance().draw();
		
		GlStateManager.color(1, 1, 1);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.popMatrix();

	}
}
