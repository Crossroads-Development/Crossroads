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

public class HamsterWheelRenderer extends TileEntitySpecialRenderer<HamsterWheelTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureHam = new ResourceLocation("textures/blocks/snow.png");
	private final ModelAxle modelAxle = new ModelAxle();
	
	private final float sHalf = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private final float sHalfT = .5F / (1F + (float) Math.sqrt(2F));
	
	@Override
	public void renderTileEntityAt(HamsterWheelTileEntity wheel, double x, double y, double z, float partialTicks, int destroyStage){
		if(!wheel.getWorld().isBlockLoaded(wheel.getPos(), false) || wheel.getWorld().getBlockState(wheel.getPos()).getBlock() != ModBlocks.hamsterWheel){
			return;
		}
		EnumFacing facing = wheel.getWorld().getBlockState(wheel.getPos()).getValue(Properties.FACING);
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5F, y + .5F, z + .5F);
		GlStateManager.rotate(facing.getAxisDirection() == (facing.getAxis() == EnumFacing.Axis.Z ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE) ? -90F : 90F, facing.getAxis() == EnumFacing.Axis.Z ? 1 : 0, 0, facing.getAxis() == EnumFacing.Axis.X ? 1 : 0);
		
		VertexBuffer vb = Tessellator.getInstance().getBuffer();
		
		GlStateManager.pushMatrix();
		if(facing.getAxis() == EnumFacing.Axis.X){
			GlStateManager.rotate(facing.getHorizontalIndex() * 90, 0, 1, 0);
		}else if(facing == EnumFacing.SOUTH){
			GlStateManager.rotate(180, 0, 1, 0);
		}
		
		//Body
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -.25D, .19D);
		GlStateManager.scale(3.5D, .2D, 1.5D);
		modelAxle.render(textureHam, textureHam, Color.WHITE);
		GlStateManager.popMatrix();
		
		//Head
		GlStateManager.pushMatrix();
		GlStateManager.translate(-.2D, -.25D, .12D);
		GlStateManager.scale(1D, .15D, 1D);
		modelAxle.render(textureHam, textureHam, Color.WHITE);
		GlStateManager.popMatrix();
		
		//Eye
		GlStateManager.pushMatrix();
		GlStateManager.translate(-.2D, -.18D, .08D);
		GlStateManager.scale(.2D, .05D, .2D);
		modelAxle.render(textureHam, textureHam, Color.BLACK);
		GlStateManager.popMatrix();

		//Nose
		GlStateManager.pushMatrix();
		GlStateManager.translate(-.27D, -.25D, .12D);
		GlStateManager.scale(.2D, .05D, .2D);
		modelAxle.render(textureHam, textureHam, Color.PINK);
		GlStateManager.popMatrix();

		//Feet
		GlStateManager.pushMatrix();
		GlStateManager.translate(-.2D, -.25D, .30D);
		int peakAngle = 60;
		int ticksPerCycle = 25;
		float feetAngle = Math.abs((4 * peakAngle * wheel.getWorld().getTotalWorldTime() / ticksPerCycle) % (4 * peakAngle) - (2 * peakAngle)) - peakAngle;
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
		
		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1);

		//Wheel
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.rotate(wheel.angle, 0F, 1F, 0F);

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
