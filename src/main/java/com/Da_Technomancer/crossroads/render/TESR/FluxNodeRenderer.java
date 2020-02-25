package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxNodeTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FluxNodeRenderer extends LinkLineRenderer<FluxNodeTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/gimbal.png");
	private static final ResourceLocation TEXTURE_COP = new ResourceLocation(Crossroads.MODID, "textures/block/block_copshowium.png");

	@Override
	public void render(FluxNodeTileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos())){
			return;
		}
		super.render(te, x, y, z, partialTicks, destroyStage);

		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();

		GlStateManager.translated(x + .5D, y + .5D, z + .5D);

		float angle = te.getRenderAngle(partialTicks);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();

		GlStateManager.rotated(angle, 0, 1, 0);

		GlStateManager.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		drawGimbal(tess, buf);
		GlStateManager.rotated(90, 0, 0, 1);
		GlStateManager.rotated(angle + 90, 0, 1, 0);
		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(tess, buf);
		GlStateManager.rotated(90, 0, 0, 1);
		GlStateManager.rotated(angle + 90, 0, 1, 0);
		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(tess, buf);

		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_COP);
		GlStateManager.scalef(7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();
		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 1).endVertex();
		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0).endVertex();

		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 0).endVertex();
		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();
		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1).endVertex();

		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
		buf.pos(-0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();
		buf.pos(-0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();

		buf.pos(0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
		buf.pos(0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();
		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();

		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0).endVertex();
		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 1).endVertex();
		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 1).endVertex();

		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 0).endVertex();
		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1).endVertex();
		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 0).endVertex();
		tess.draw();

		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}

	private void drawGimbal(Tessellator tess, BufferBuilder buf){
		float outer = 7F / 16F;
		float inner = 5F / 16F;
		float edge = 1F / 16F;

		float texWidth = 14F / 16F;
		float edgeEnd = 1;
		float innerTex = 2F / 16F;

		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		buf.pos(outer, outer, edge).tex(texWidth, 0).endVertex();
		buf.pos(-outer, outer, edge).tex(0, 0).endVertex();
		buf.pos(-outer, inner, edge).tex(0, innerTex).endVertex();
		buf.pos(outer, inner, edge).tex(texWidth, innerTex).endVertex();

		buf.pos(outer, outer, -edge).tex(texWidth, 0).endVertex();
		buf.pos(outer, inner, -edge).tex(texWidth, innerTex).endVertex();
		buf.pos(-outer, inner, -edge).tex(0, innerTex).endVertex();
		buf.pos(-outer, outer, -edge).tex(0, 0).endVertex();

		buf.pos(outer, -outer, edge).tex(texWidth, 0).endVertex();
		buf.pos(outer, -inner, edge).tex(texWidth, innerTex).endVertex();
		buf.pos(-outer, -inner, edge).tex(0, innerTex).endVertex();
		buf.pos(-outer, -outer, edge).tex(0, 0).endVertex();

		buf.pos(outer, -outer, -edge).tex(texWidth, 0).endVertex();
		buf.pos(-outer, -outer, -edge).tex(0, 0).endVertex();
		buf.pos(-outer, -inner, -edge).tex(0, innerTex).endVertex();
		buf.pos(outer, -inner, -edge).tex(texWidth, innerTex).endVertex();

		buf.pos(outer, inner, edge).tex(0, innerTex).endVertex();
		buf.pos(inner, inner, edge).tex(innerTex, innerTex).endVertex();
		buf.pos(inner, -inner, edge).tex(innerTex, texWidth - innerTex).endVertex();
		buf.pos(outer, -inner, edge).tex(0, texWidth - innerTex).endVertex();

		buf.pos(outer, inner, -edge).tex(0, innerTex).endVertex();
		buf.pos(outer, -inner, -edge).tex(0, texWidth - innerTex).endVertex();
		buf.pos(inner, -inner, -edge).tex(innerTex, texWidth - innerTex).endVertex();
		buf.pos(inner, inner, -edge).tex(innerTex, innerTex).endVertex();

		buf.pos(-outer, inner, edge).tex(0, innerTex).endVertex();
		buf.pos(-outer, -inner, edge).tex(0, texWidth - innerTex).endVertex();
		buf.pos(-inner, -inner, edge).tex(innerTex, texWidth - innerTex).endVertex();
		buf.pos(-inner, inner, edge).tex(innerTex, innerTex).endVertex();

		buf.pos(-outer, inner, -edge).tex(0, innerTex).endVertex();
		buf.pos(-inner, inner, -edge).tex(innerTex, innerTex).endVertex();
		buf.pos(-inner, -inner, -edge).tex(innerTex, texWidth - innerTex).endVertex();
		buf.pos(-outer, -inner, -edge).tex(0, texWidth - innerTex).endVertex();


		buf.pos(-outer, -outer, -edge).tex(texWidth, 0).endVertex();
		buf.pos(outer, -outer, -edge).tex(texWidth, texWidth).endVertex();
		buf.pos(outer, -outer, edge).tex(edgeEnd, texWidth).endVertex();
		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0).endVertex();

		buf.pos(-outer, outer, -edge).tex(texWidth, 0).endVertex();
		buf.pos(-outer, outer, edge).tex(edgeEnd, 0).endVertex();
		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
		buf.pos(outer, outer, -edge).tex(texWidth, texWidth).endVertex();

		buf.pos(-outer, -outer, -edge).tex(texWidth, 0).endVertex();
		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0).endVertex();
		buf.pos(-outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
		buf.pos(-outer, outer, -edge).tex(texWidth, texWidth).endVertex();

		buf.pos(outer, -outer, -edge).tex(texWidth, 0).endVertex();
		buf.pos(outer, outer, -edge).tex(texWidth, texWidth).endVertex();
		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
		buf.pos(outer, -outer, edge).tex(edgeEnd, 0).endVertex();


		buf.pos(-inner, -inner, -edge).tex(texWidth, 0).endVertex();
		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0).endVertex();
		buf.pos(inner, -inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
		buf.pos(inner, -inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();

		buf.pos(-inner, inner, -edge).tex(texWidth, 0).endVertex();
		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();
		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
		buf.pos(-inner, inner, edge).tex(edgeEnd, 0).endVertex();

		buf.pos(-inner, -inner, -edge).tex(texWidth, 0).endVertex();
		buf.pos(-inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();
		buf.pos(-inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0).endVertex();

		buf.pos(inner, -inner, -edge).tex(texWidth, 0).endVertex();
		buf.pos(inner, -inner, edge).tex(edgeEnd, 0).endVertex();
		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();

		tess.draw();
	}
}
