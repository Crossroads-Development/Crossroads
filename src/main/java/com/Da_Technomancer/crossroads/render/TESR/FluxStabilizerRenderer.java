package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.tileentities.technomancy.AbstractFluxStabilizerTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FluxStabilizerRenderer extends LinkLineRenderer<AbstractFluxStabilizerTE>{

	private static final ResourceLocation TEXTURE = TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM;

	@Override
	public void render(AbstractFluxStabilizerTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos(), false)){
			return;
		}
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		if(!te.clientRunning){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + .5D, z + .5D);
		GlStateManager.disableCull();
		GlStateManager.color(1, 0.15F, 0, 1);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();


		final double hLen = 6D / 16D;
		double prog = (getWorld().getTotalWorldTime() + partialTicks) / 20D;
		prog %= 1D;
		prog -= 0.5D;
		prog *= hLen * 2D;
		final double rad = 1D / 32D;


		for(int i = 0; i < 3; i++){
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			buf.pos(-hLen, prog - rad, -hLen).tex(0, 0).endVertex();
			buf.pos(-hLen, prog - rad, -hLen).tex(0, 1).endVertex();
			buf.pos(hLen, prog + rad, -hLen).tex(1, 1).endVertex();
			buf.pos(hLen, prog + rad, -hLen).tex(1, 0).endVertex();

			buf.pos(-hLen, prog - rad, hLen).tex(0, 0).endVertex();
			buf.pos(hLen, prog - rad, hLen).tex(1, 0).endVertex();
			buf.pos(hLen, prog + rad, hLen).tex(1, 1).endVertex();
			buf.pos(-hLen, prog + rad, hLen).tex(0, 1).endVertex();

			buf.pos(-hLen, prog - rad, -hLen).tex(0, 0).endVertex();
			buf.pos(-hLen, prog - rad, hLen).tex(1, 0).endVertex();
			buf.pos(-hLen, prog + rad, hLen).tex(1, 1).endVertex();
			buf.pos(-hLen, prog + rad, -hLen).tex(0, 1).endVertex();

			buf.pos(hLen, prog - rad, -hLen).tex(0, 0).endVertex();
			buf.pos(hLen, prog + rad, -hLen).tex(0, 1).endVertex();
			buf.pos(hLen, prog + rad, hLen).tex(1, 1).endVertex();
			buf.pos(hLen, prog - rad, hLen).tex(1, 0).endVertex();
			tess.draw();

			GlStateManager.rotate(90, i == 0 ? 1 : 0, 0, i == 1 ? 1 : 0);
		}


		GlStateManager.enableCull();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
