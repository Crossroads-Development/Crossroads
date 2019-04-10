package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChronoHarnessTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ChronoHarnessRenderer extends TileEntitySpecialRenderer<ChronoHarnessTileEntity>{

	private static final ResourceLocation INNER_TEXT = new ResourceLocation(Main.MODID, "textures/blocks/block_copshowium.png");
	private static final ResourceLocation OUTER_TEXT = new ResourceLocation(Main.MODID, "textures/blocks/block_cast_iron.png");
	
	@Override
	public void render(ChronoHarnessTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!te.getWorld().isBlockLoaded(te.getPos(), false)){
			return;
		}

		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		if(te.running){
			te.angle += 9F * partialTicks;
		}

		float brightX = OpenGlHelper.lastBrightnessX;
		float brightY = OpenGlHelper.lastBrightnessY;
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();

		//Revolving rods
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + 0.5D, y, z + 0.5D);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 160, 160);

		float smallOffset = 0.0928F;
		float largeOffset = 5F / 16F;

		GlStateManager.rotate(te.angle, 0, 1, 0);

		Minecraft.getMinecraft().getTextureManager().bindTexture(INNER_TEXT);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(buf, smallOffset, smallOffset);
		addRod(buf, smallOffset, -smallOffset);
		addRod(buf, -smallOffset, -smallOffset);
		addRod(buf, -smallOffset, smallOffset);
		tes.draw();

		GlStateManager.rotate(-2F * te.angle, 0, 1, 0);

		Minecraft.getMinecraft().getTextureManager().bindTexture(OUTER_TEXT);
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

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
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
