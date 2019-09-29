package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PrototypingTableRenderer extends TileEntityRenderer<PrototypingTableTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/field.png");

	@Override
	public void render(PrototypingTableTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos(), false) || !te.visible){
			return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 100F / 255F, 0, 0.25F);
		float brightX = OpenGlHelper.lastBrightnessX;
		float brightY = OpenGlHelper.lastBrightnessY;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		GlStateManager.rotate(te.getWorld().getBlockState(te.getPos()).get(CrossroadsProperties.HORIZ_FACING).getHorizontalAngle(), 0, 1, 0);
		GlStateManager.translate(0.5D, 0.5D, 0.5D);

		Minecraft.getInstance().renderEngine.bindTexture(TEXTURE);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-0.01D, -0.01D, 0).tex(0D, 0).endVertex();
		vb.pos(16.01D, -0.01D, 0).tex(1, 0).endVertex();
		vb.pos(16.01D, -0.01D, 16.01D).tex(1, 1).endVertex();
		vb.pos(-0.01D, -0.01D, 16.01D).tex(0D, 1).endVertex();

		vb.pos(-0.01D, 16.01D, 0).tex(0D, 0).endVertex();
		vb.pos(16.01D, 16.01D, 0).tex(1, 0).endVertex();
		vb.pos(16.01D, 16.01D, 16.01D).tex(1, 1).endVertex();
		vb.pos(-0.01D, 16.01D, 16.01D).tex(0, 1).endVertex();

		vb.pos(16.01D, -0.01D, 0).tex(0, 0).endVertex();
		vb.pos(16.01D, 16.01D, 0).tex(1, 0).endVertex();
		vb.pos(16.01D, 16.01D, 16.01D).tex(1, 1).endVertex();
		vb.pos(16.01D, -0.01D, 16.01D).tex(0, 1).endVertex();

		vb.pos(-0.01D, -0.01D, 0).tex(0, 0).endVertex();
		vb.pos(-0.01D, 16.01D, 0).tex(1, 0).endVertex();
		vb.pos(-0.01D, 16.01D, 16.01D).tex(1, 1).endVertex();
		vb.pos(-0.01D, -0.01D, 16.01D).tex(0, 1).endVertex();

		vb.pos(-0.01D, -0.01D, 0).tex(0, 0).endVertex();
		vb.pos(16.01D, -0.01D, 0).tex(1, 0).endVertex();
		vb.pos(16.01D, 16.01D, 0).tex(1, 1).endVertex();
		vb.pos(-0.01D, 16.01D, 0).tex(0, 1).endVertex();

		vb.pos(-0.01D, -0.01D, 16.01D).tex(0, 0).endVertex();
		vb.pos(16.01D, -0.01D, 16.01D).tex(1, 0).endVertex();
		vb.pos(16.01D, 16.01D, 16.01D).tex(1, 1).endVertex();
		vb.pos(-0.01D, 16.01D, 16.01D).tex(0, 1).endVertex();

		Tessellator.getInstance().draw();

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}

	@Override
	public boolean isGlobalRenderer(PrototypingTableTileEntity te){
		return te.visible;
	}
}
