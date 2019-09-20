package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.technomancy.TemporalAcceleratorTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
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
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TemporalAcceleratorRenderer extends TileEntityRenderer<TemporalAcceleratorTileEntity>{

	private static final float sHalf = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private static final float sHalfT = .5F / (1F + (float) Math.sqrt(2F));
	private static final ResourceLocation AREA = new ResourceLocation(Crossroads.MODID, "textures/gui/field.png");

	@Override
	public void render(TemporalAcceleratorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(te == null || !te.getWorld().isBlockLoaded(te.getPos(), false)){
			return;
		}
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		Direction dir = te.getWorld().getBlockState(te.getPos()).get(EssentialsProperties.FACING);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		//Area of effect overlay when holding wrench
		if(EssentialsConfig.isWrench(Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND), true) || EssentialsConfig.isWrench(Minecraft.getInstance().player.getHeldItem(Hand.OFF_HAND), true)){
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

			GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
			if(dir == Direction.DOWN){
				GlStateManager.rotate(180, 1, 0, 0);
			}else if(dir != Direction.UP){
				GlStateManager.rotate(dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 90 : -90, dir.getAxis() == Axis.Z ? 1 : 0, 0, dir.getAxis() == Axis.X ? -1 : 0);
			}

			float radius = te.getSize() / 2F + 0.01F;
			GlStateManager.translate(0, radius + 0.5 - 0.001F, 0);


			Minecraft.getInstance().renderEngine.bindTexture(AREA);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-radius, -radius, -radius).tex(0D, 0).endVertex();
			vb.pos(radius, -radius, -radius).tex(1, 0).endVertex();
			vb.pos(radius, -radius, radius).tex(1, 1).endVertex();
			vb.pos(-radius, -radius, radius).tex(0D, 1).endVertex();

			vb.pos(-radius, radius, -radius).tex(0D, 0).endVertex();
			vb.pos(radius, radius, -radius).tex(1, 0).endVertex();
			vb.pos(radius, radius, radius).tex(1, 1).endVertex();
			vb.pos(-radius, radius, radius).tex(0, 1).endVertex();

			vb.pos(radius, -radius, -radius).tex(0, 0).endVertex();
			vb.pos(radius, radius, -radius).tex(1, 0).endVertex();
			vb.pos(radius, radius, radius).tex(1, 1).endVertex();
			vb.pos(radius, -radius, radius).tex(0, 1).endVertex();

			vb.pos(-radius, -radius, -radius).tex(0, 0).endVertex();
			vb.pos(-radius, radius, -radius).tex(1, 0).endVertex();
			vb.pos(-radius, radius, radius).tex(1, 1).endVertex();
			vb.pos(-radius, -radius, radius).tex(0, 1).endVertex();

			vb.pos(-radius, -radius, -radius).tex(0, 0).endVertex();
			vb.pos(radius, -radius, -radius).tex(1, 0).endVertex();
			vb.pos(radius, radius, -radius).tex(1, 1).endVertex();
			vb.pos(-radius, radius, -radius).tex(0, 1).endVertex();

			vb.pos(-radius, -radius, radius).tex(0, 0).endVertex();
			vb.pos(radius, -radius, radius).tex(1, 0).endVertex();
			vb.pos(radius, radius, radius).tex(1, 1).endVertex();
			vb.pos(-radius, radius, radius).tex(0, 1).endVertex();

			Tessellator.getInstance().draw();

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);

		if(dir == Direction.DOWN){
			GlStateManager.rotate(180, 1, 0, 0);
		}else if(dir != Direction.UP){
			GlStateManager.rotate(dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 90 : -90, dir.getAxis() == Axis.Z ? 1 : 0, 0, dir.getAxis() == Axis.X ? -1 : 0);
		}

		float angle = te.getWorld().getGameTime() + partialTicks;

		GlStateManager.rotate(angle, 0, 1, 0);
		GlStateManager.translate(0, 5F / 16F, 0);

		Color c = GearFactory.findMaterial("copshowium").getColor();
		float top = 0.0625F;//-.375F;
		float lHalf = .4375F;
		float lHalfT = .5F;
		float tHeight = 1F / 16F;

		Minecraft.getInstance().renderEngine.bindTexture(ModelGearOctagon.RESOURCE);

		for(int i = 0; i < 2; i++){
			//i==0: Large gear; i==1: Small gear
			if(i == 1){
				GlStateManager.rotate(-2 * angle, 0, 1, 0);
				GlStateManager.translate(0, -4F / 16F, 0);
				GlStateManager.scale(0.8F, 1, 0.8F);
			}

			GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(lHalf, -top, sHalf).tex(1F, .5F + -sHalfT).endVertex();
			vb.pos(lHalf, -top, -sHalf).tex(1F, .5F + sHalfT).endVertex();
			vb.pos(lHalf, top, -sHalf).tex(1F - tHeight, .5F + sHalfT).endVertex();
			vb.pos(lHalf, top, sHalf).tex(1F - tHeight, .5F + -sHalfT).endVertex();

			vb.pos(-lHalf, top, sHalf).tex(tHeight, .5F + -sHalfT).endVertex();
			vb.pos(-lHalf, top, -sHalf).tex(tHeight, .5F + sHalfT).endVertex();
			vb.pos(-lHalf, -top, -sHalf).tex(0, .5F + sHalfT).endVertex();
			vb.pos(-lHalf, -top, sHalf).tex(0, .5F + -sHalfT).endVertex();

			vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, 0).endVertex();
			vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, 0).endVertex();
			vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, tHeight).endVertex();
			vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, tHeight).endVertex();

			vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, 1F - tHeight).endVertex();
			vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, 1F - tHeight).endVertex();
			vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, 1).endVertex();
			vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, 1).endVertex();

			vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
			vb.pos(lHalf, top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(lHalf, -top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();

			vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
			vb.pos(-lHalf, -top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(-lHalf, top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();

			vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
			vb.pos(lHalf, -top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
			vb.pos(lHalf, top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
			vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();

			vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
			vb.pos(-lHalf, top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
			vb.pos(-lHalf, -top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
			vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
			Tessellator.getInstance().draw();
		}

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
