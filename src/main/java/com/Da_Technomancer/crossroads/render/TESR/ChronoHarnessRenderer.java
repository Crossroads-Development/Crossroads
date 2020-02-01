package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChronoHarnessTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class ChronoHarnessRenderer extends LinkLineRenderer<ChronoHarnessTileEntity>{

	private static final ResourceLocation INNER_TEXT = new ResourceLocation(Crossroads.MODID, "textures/blocks/block_copshowium.png");
	private static final ResourceLocation OUTER_TEXT = new ResourceLocation(Crossroads.MODID, "textures/blocks/block_cast_iron.png");
	
	@Override
	public void render(ChronoHarnessTileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
		if(!te.getWorld().isBlockLoaded(te.getPos())){
			return;
		}
//		super.func_199341_a(te, x, y, z, partialTicks, destroyStage);
		super.render(te, x, y, z, partialTicks, destroyStage);

		float angle = te.getRenderAngle(partialTicks);

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();

		//Revolving rods
		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + 0.5D, y, z + 0.5D);
		Pair<Float, Float> lighting = CRRenderUtil.setMediumLighting();

		float smallOffset = 0.0928F;
		float largeOffset = 5F / 16F;

		GlStateManager.rotated(angle, 0, 1, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(INNER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, smallOffset, smallOffset);
		addRod(buf, smallOffset, -smallOffset);
		addRod(buf, -smallOffset, -smallOffset);
		addRod(buf, -smallOffset, smallOffset);
		tes.draw();

		GlStateManager.rotated(-2F * angle, 0, 1, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(OUTER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, smallOffset, largeOffset);
		addRod(buf, smallOffset, -largeOffset);
		addRod(buf, -smallOffset, largeOffset);
		addRod(buf, -smallOffset, -largeOffset);
		addRod(buf, largeOffset, smallOffset);
		addRod(buf, largeOffset, -smallOffset);
		addRod(buf, -largeOffset, smallOffset);
		addRod(buf, -largeOffset, -smallOffset);
		tes.draw();

		CRRenderUtil.enableLighting(lighting);
		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}

	private void addRod(BufferBuilder buf, double x, double z){
		float rad = 1F / 16F;
		float minY = 2F / 16F;
		float maxY = 14F / 16F;
		buf.pos(x - rad, minY, z - rad).tex(0, 0).endVertex();
		buf.pos(x - rad, maxY, z - rad).tex(0, 1).endVertex();
		buf.pos(x + rad, maxY, z - rad).tex(2F * rad, 1).endVertex();
		buf.pos(x + rad, minY, z - rad).tex(2F * rad, 0).endVertex();

		buf.pos(x - rad, minY, z + rad).tex(0, 0).endVertex();
		buf.pos(x + rad, minY, z + rad).tex(2F * rad, 0).endVertex();
		buf.pos(x + rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
		buf.pos(x - rad, maxY, z + rad).tex(0, 1).endVertex();


		buf.pos(x - rad, minY, z - rad).tex(0, 0).endVertex();
		buf.pos(x - rad, minY, z + rad).tex(2F * rad, 0).endVertex();
		buf.pos(x - rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
		buf.pos(x - rad, maxY, z - rad).tex(0, 1).endVertex();

		buf.pos(x + rad, minY, z - rad).tex(0, 0).endVertex();
		buf.pos(x + rad, maxY, z - rad).tex(0, 1).endVertex();
		buf.pos(x + rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
		buf.pos(x + rad, minY, z + rad).tex(2F * rad, 0).endVertex();
	}
}
