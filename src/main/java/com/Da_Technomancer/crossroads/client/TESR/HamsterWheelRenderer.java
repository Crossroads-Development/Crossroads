package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.tileentities.HamsterWheelTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HamsterWheelRenderer extends TileEntitySpecialRenderer<HamsterWheelTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureHam = new ResourceLocation(Main.MODID, "textures/model/hamster.png");
	private final ModelAxle modelAxle = new ModelAxle();
	
	private final float sHalf = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private final float sHalfT = .5F / (1F + (float) Math.sqrt(2F));
	
	@Override
	public void renderTileEntityAt(HamsterWheelTileEntity wheel, double x, double y, double z, float partialTicks, int destroyStage){
		World world = wheel.getWorld();
		BlockPos pos = wheel.getPos();
		if(!world.isBlockLoaded(pos, false) || world.getBlockState(pos).getBlock() != ModBlocks.hamsterWheel){
			return;
		}
		EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + .5D, z + .5D);
		GlStateManager.rotate(-facing.getHorizontalAngle() + 180, 0, 1, 0);
		GlStateManager.rotate(90, 1, 0, 0);
		VertexBuffer vb = Tessellator.getInstance().getBuffer();
		
		float angle = wheel.nextAngle - wheel.angle;
		angle *= partialTicks;
		angle += wheel.angle;
		
		//Feet
		GlStateManager.pushMatrix();
		GlStateManager.translate(-.2D, -.25D, .30D);
		float peakAngle = 60;
		float degreesPerCycle = 50;
		float feetAngle = Math.abs((4 * peakAngle * Math.abs(angle) / degreesPerCycle) % (4 * peakAngle) - (2 * peakAngle)) - peakAngle;
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++){
				GlStateManager.pushMatrix();
				GlStateManager.translate(j == 0 ? 0 : .4D, i == 0 ? -.065D : .065D, 0);
				GlStateManager.scale(.4D, .07D, .49D);
				GlStateManager.rotate(i + j % 2 == 0 ? feetAngle : -feetAngle, 0, 1, 0);
				modelAxle.render(textureHam, textureHam, Color.WHITE);
				GlStateManager.popMatrix();
			}
		}
		
		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1);

		//Wheel
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.rotate(angle, 0F, 1F, 0F);

		//Axle Support
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -.4375D, 0);
		GlStateManager.scale(1, .8D, 1);
		GlStateManager.rotate(90, 1, 0, 0);
		modelAxle.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		Minecraft.getMinecraft().renderEngine.bindTexture(textureAx);


		float top = 0;
		float bottom = -.5F;
		float lHalf = .375F;

		float lHalfT = .5F;
		float tHeight = 1;
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(lHalf, bottom, sHalf).tex(1F, .5F + -sHalfT).endVertex();
		vb.pos(lHalf, bottom, -sHalf).tex(1F, .5F + sHalfT).endVertex();
		vb.pos(lHalf, top, -sHalf).tex(1F - tHeight, .5F + sHalfT).endVertex();
		vb.pos(lHalf, top, sHalf).tex(1F - tHeight, .5F + -sHalfT).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-lHalf, top, sHalf).tex(tHeight, .5F + -sHalfT).endVertex();
		vb.pos(-lHalf, top, -sHalf).tex(tHeight, .5F + sHalfT).endVertex();
		vb.pos(-lHalf, bottom, -sHalf).tex(0, .5F + sHalfT).endVertex();
		vb.pos(-lHalf, bottom, sHalf).tex(0, .5F + -sHalfT).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, 0).endVertex();
		vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, 0).endVertex();
		vb.pos(-sHalf, bottom, lHalf).tex(.5F + -sHalfT, tHeight).endVertex();
		vb.pos(sHalf, bottom, lHalf).tex(.5F + sHalfT, tHeight).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, bottom, -lHalf).tex(.5F + sHalfT, 1F - tHeight).endVertex();
		vb.pos(-sHalf, bottom, -lHalf).tex(.5F + -sHalfT, 1F - tHeight).endVertex();
		vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, 1).endVertex();
		vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, 1).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
		vb.pos(lHalf, top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(lHalf, bottom, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(sHalf, bottom, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sHalf, bottom, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
		vb.pos(-lHalf, bottom, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(-lHalf, top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
		vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
		Tessellator.getInstance().draw();


		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(sHalf, bottom, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
		vb.pos(lHalf, bottom, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
		vb.pos(lHalf, top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
		vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
		Tessellator.getInstance().draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
		vb.pos(-lHalf, top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
		vb.pos(-lHalf, bottom, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
		vb.pos(-sHalf, bottom, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
		Tessellator.getInstance().draw();
		
		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
