package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HeatingCrucibleRenderer extends TileEntityRenderer<HeatingCrucibleTileEntity>{

	@Override
	public void render(HeatingCrucibleTileEntity te, double x, double y, double z, float partialTicks, int destroStage){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos()) || te.getActiveTexture().isEmpty()){
			return;
		}
		int fullness = te.getWorld().getBlockState(te.getPos()).get(CRProperties.FULLNESS);
		if(fullness == 0){
			return;
		}

		TextureAtlasSprite text = Minecraft.getInstance().getTextureMap().getAtlasSprite(te.getActiveTexture());
		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();
		Color col = te.getCol();
		GlStateManager.color4f(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F, 1);
		GlStateManager.translated(x, y, z);
		Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		CRRenderUtil.setMediumLighting();

		float xzStart = 2F / 16F;
		float xzEnd = 14F / 16F;
		float height = (float) (2 + 4 * fullness) / 16F;

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xzEnd, height, xzStart).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (xzStart * 16))).endVertex();
		vb.pos(xzStart, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (xzStart * 16))).endVertex();
		vb.pos(xzStart, height, xzEnd).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (xzEnd * 16))).endVertex();
		vb.pos(xzEnd, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (xzEnd * 16))).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();

	}
}
