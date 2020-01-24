package com.Da_Technomancer.crossroads.render.TESR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FluxStabilizerRenderer extends TileEntityRenderer<AbstractStabilizerTileEntity>{

	private static final ResourceLocation TEXTURE = BeaconTileEntityRenderer.TEXTURE_BEACON_BEAM;

	@Override
	public void render(AbstractStabilizerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos())){
			return;
		}
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		if(!te.clientRunning){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + .5D, y + .5D, z + .5D);
		GlStateManager.disableCull();
		GlStateManager.color(1, 0.15F, 0, 1);

		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();


		final double hLen = 6D / 16D;
		double prog = (getWorld().getGameTime() + partialTicks) / 20D;
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

			GlStateManager.rotated(90, i == 0 ? 1 : 0, 0, i == 1 ? 1 : 0);
		}


		GlStateManager.enableCull();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}
}
