package com.Da_Technomancer.crossroads.client.TESR;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class HeatingCrucibleRenderer extends TileEntitySpecialRenderer<HeatingCrucibleTileEntity>{

	@Override
	public void render(HeatingCrucibleTileEntity te, double x, double y, double z, float partialTicks, int destroheightage, float alpha){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos(), false) || te.getActiveTexture() == null){
			return;
		}
		int fullness = te.getWorld().getBlockState(te.getPos()).getValue(Properties.FULLNESS);
		if(fullness == 0){
			return;
		}

		TextureAtlasSprite text = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(te.getActiveTexture());
		if(text == null){
			return;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x, y, z);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();
		
		float xzStart = 2F / 16F;
		float xzEnd = 14F / 16F;
		float height = (float) (2 + 4 * fullness) / 16F;

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xzEnd, height, xzStart).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzStart, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzStart, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzEnd, height, xzStart).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xzStart, height, xzEnd).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzEnd, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzEnd, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzStart, height, xzEnd).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xzEnd, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzEnd, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzEnd, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzEnd, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xzStart, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzStart, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzStart, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		vb.pos(xzStart, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (height * 16))).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xzEnd, height, xzStart).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (xzStart * 16))).endVertex();
		vb.pos(xzStart, height, xzStart).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (xzStart * 16))).endVertex();
		vb.pos(xzStart, height, xzEnd).tex(text.getInterpolatedU(xzStart * 16), text.getInterpolatedV(16 - (xzEnd * 16))).endVertex();
		vb.pos(xzEnd, height, xzEnd).tex(text.getInterpolatedU(xzEnd * 16), text.getInterpolatedV(16 - (xzEnd * 16))).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}
}
