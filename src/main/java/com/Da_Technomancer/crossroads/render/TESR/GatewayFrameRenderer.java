package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GatewayFrameRenderer extends TileEntityRenderer<GatewayFrameTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/gateway.png");

	@Override
	public void render(GatewayFrameTileEntity frame, double x, double y, double z, float partialTicks, int destroyStage){
		if(frame == null || !frame.getWorld().isBlockLoaded(frame.getPos(), false) || frame.getWorld().getBlockState(frame.getPos()).get(EssentialsProperties.FACING) != Direction.UP){
			return;
		}
		super.render(frame, x, y, z, partialTicks, destroyStage, alpha);
		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, (float) Math.sin((frame.getWorld().getGameTime() + partialTicks) / 10D) / 6F + 5F / 6F);
		float brightX = OpenGlHelper.lastBrightnessX;
		float brightY = OpenGlHelper.lastBrightnessY;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GlStateManager.translated(x + .5D, y, z + .5D);
		if(frame.getAlignment() == Axis.Z){
			GlStateManager.rotated(90, 0, 1, 0);
		}
		Minecraft.getInstance().textureManager.bindTexture(TEXTURE);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-1.5D, 0, 0).tex(0, 0).endVertex();
		vb.pos(1.5D, 0, 0).tex(1, 0).endVertex();
		vb.pos(1.5D, -3, 0).tex(1, 1).endVertex();
		vb.pos(-1.5D, -3, 0).tex(0, 1).endVertex();
		Tessellator.getInstance().draw();
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}
}
