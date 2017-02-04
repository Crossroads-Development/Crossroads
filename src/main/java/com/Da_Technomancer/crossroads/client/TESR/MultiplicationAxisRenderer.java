package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelPyramid;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MultiplicationAxisTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class MultiplicationAxisRenderer extends TileEntitySpecialRenderer<MultiplicationAxisTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureGear = new ResourceLocation(Main.MODID, "textures/model/gearOct.png");
	private final ResourceLocation texturePyr = new ResourceLocation(Main.MODID, "textures/model/pyramid.png");
	private final ResourceLocation textureSpr = new ResourceLocation(Main.MODID, "textures/model/spring.png");
	private final ResourceLocation textureBelt = new ResourceLocation(Main.MODID, "textures/model/belt.png");
	private final ModelAxle modelAx = new ModelAxle();
	private final ModelGearOctagon modelGear = new ModelGearOctagon();
	private final ModelPyramid modelPyr = new ModelPyramid();

	@Override
	public void renderTileEntityAt(MultiplicationAxisTileEntity axis, double x, double y, double z, float partialTicks, int destroyStage){

		if(!axis.getWorld().isBlockLoaded(axis.getPos(), false) || axis.getWorld().getBlockState(axis.getPos()).getBlock() != ModBlocks.multiplicationAxis){
			return;
		}

		EnumFacing facing = axis.getWorld().getBlockState(axis.getPos()).getValue(Properties.FACING);
		double speedTwo = axis.lastInTwo;
		float angleOne = (float) axis.angleOne;
		float angleTwo = (float) axis.angleTwo;
		float angleThree = (float) axis.angleThree;
		Color col = GearTypes.COPSHOWIUM.getColor();
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(-facing.getHorizontalAngle() - 90, 0, 1, 0);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		
		//Some of the scaling is a tiny bit less than expected values to avoid z-fighting.
		
		//Axles
		//first carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.125D, .21875D, .5D);
		GlStateManager.scale(1D, .4374D, 1D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.875D, .21875D, .5D);
		GlStateManager.scale(1D, .4374D, 1D);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//first transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .40625D - (speedTwo > 0 ? 0D : Math.max(-8, Math.abs(speedTwo)) * .0234375D), .25D);
		GlStateManager.scale(1D, .3125D, 1D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .40625D - (speedTwo < 0 ? 0D : Math.min(8, speedTwo) * .0234375D), .25D);
		GlStateManager.scale(1D, .3125D, 1D);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//upper height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .75D, .5D);
		GlStateManager.scale(1D, .5D, 1D);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//lower height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(speedTwo <= 0 ? 10 : -10, 0, 1, 0);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .45D, .35D);
		GlStateManager.scale(1D, 1D, .15D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Pyramids
		//first transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .65625D - (speedTwo > 0 ? 0D : Math.max(-8, Math.abs(speedTwo)) * .0234375D), .25D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelPyr.render(texturePyr, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .65625D - (speedTwo < 0 ? 0D : Math.min(8, Math.abs(speedTwo)) * .0234375D), .25D);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		modelPyr.render(texturePyr, Color.WHITE);
		GlStateManager.popMatrix();

		//Gears
		//first carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.125D, .5D, .5D);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//second carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.875D, .5D, .5D);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//upper height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .55D, .5D);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//lower height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(speedTwo > 0 ? 10 : -10, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .49D, .55D);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.rotate(-angleTwo + 45F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();
		
		//transformer shifter
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(speedTwo > 0 ? 10 : -10, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .51D, .55D);
		GlStateManager.scale(.15D, .625D, .15D);
		GlStateManager.rotate(-angleTwo + 45F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();
		
		//in
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, 1.24D, .5D);
		GlStateManager.scale(.125D, .625D, .125D);
		GlStateManager.rotate(angleOne + 45F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//out
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .3125D, .5D);
		GlStateManager.scale(.125D, .625D, .125D);
		GlStateManager.rotate(-angleThree + 45F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//Springs
		//first transformer bottom
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .16D + (speedTwo > 0 ? 0D : Math.max(-8, speedTwo) * .01171875D), .25D);
		GlStateManager.scale(1D, .1875D + (speedTwo > 0 ? 0D : Math.max(-8, speedTwo) * .0234375D), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer bottom
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .16D - (speedTwo < 0 ? 0D : Math.min(8, speedTwo) * .01171875D), .25D);
		GlStateManager.scale(1D, .1875D - (speedTwo < 0 ? 0D : Math.min(8, speedTwo) * .0234375D), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//first transformer top
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .84D + (speedTwo > 0 ? 0D : Math.max(-8, speedTwo) * .01171875D), .25D);
		GlStateManager.scale(1D, .1875D - (speedTwo > 0 ? 0D : Math.max(-8, speedTwo) * .0234375D), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer top
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .84D - (speedTwo < 0 ? 0D : Math.min(8, speedTwo) * .01171875D), .25D);
		GlStateManager.scale(1D, .1875D + (speedTwo < 0 ? 0D : Math.min(8, speedTwo) * .0234375D), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();
		
		//Belts
		GlStateManager.disableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(textureBelt);
		VertexBuffer vb = Tessellator.getInstance().getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		//In->Transformer
		//in front
		vb.pos(.035D, .25D, .4075D).tex(0, 0).endVertex();
		vb.pos(.035D, .35D, .4075D).tex(0, 1).endVertex();
		vb.pos(.035D, .35D, .5925D).tex(1, 1).endVertex();
		vb.pos(.035D, .25D, .5925D).tex(1, 0).endVertex();

		//in back
		vb.pos(.035D, .25D, .5925D).tex(0, 0).endVertex();
		vb.pos(.035D, .35D, .5925D).tex(0, 1).endVertex();
		vb.pos(.2175D, .35D, .5925D).tex(1, 1).endVertex();
		vb.pos(.2175D, .25D, .5925D).tex(1, 0).endVertex();

		//transformer back
		vb.pos(.405D, .25D, .1575D).tex(0, 0).endVertex();
		vb.pos(.405D, .35D, .1575D).tex(0, 1).endVertex();
		vb.pos(.405D, .35D, .3425D).tex(1, 1).endVertex();
		vb.pos(.405D, .25D, .3425D).tex(1, 0).endVertex();

		//transformer front
		vb.pos(.405D, .25D, .1575D).tex(0, 0).endVertex();
		vb.pos(.405D, .35D, .1575D).tex(0, 1).endVertex();
		vb.pos(.22D, .35D, .1575D).tex(1, 1).endVertex();
		vb.pos(.22D, .25D, .1575D).tex(1, 0).endVertex();
		
		//front
		vb.pos(.035D, .25D, .4075D).tex(0, 0).endVertex();
		vb.pos(.035D, .35D, .4075D).tex(0, 1).endVertex();
		vb.pos(.22D, .35D, .1575D).tex(1, 1).endVertex();
		vb.pos(.22D, .25D, .1575D).tex(1, 0).endVertex();
		
		//back
		vb.pos(.405D, .25D, .3425D).tex(0, 0).endVertex();
		vb.pos(.405D, .35D, .3425D).tex(0, 1).endVertex();
		vb.pos(.2175D, .35D, .5925D).tex(1, 1).endVertex();
		vb.pos(.2175D, .25D, .5925D).tex(1, 0).endVertex();

		//Transformer->Out
		//out front
		vb.pos(.97D, .25D, .4075D).tex(0, 0).endVertex();
		vb.pos(.97D, .35D, .4075D).tex(0, 1).endVertex();
		vb.pos(.97D, .35D, .5925D).tex(1, 1).endVertex();
		vb.pos(.97D, .25D, .5925D).tex(1, 0).endVertex();

		//out back
		vb.pos(.97D, .25D, .5925D).tex(0, 0).endVertex();
		vb.pos(.97D, .35D, .5925D).tex(0, 1).endVertex();
		vb.pos(.7825D, .35D, .5925D).tex(1, 1).endVertex();
		vb.pos(.7825D, .25D, .5925D).tex(1, 0).endVertex();

		//transformer back
		vb.pos(.595D, .25D, .1575D).tex(0, 0).endVertex();
		vb.pos(.595D, .35D, .1575D).tex(0, 1).endVertex();
		vb.pos(.595D, .35D, .3425D).tex(1, 1).endVertex();
		vb.pos(.595D, .25D, .3425D).tex(1, 0).endVertex();

		//transformer front
		vb.pos(.595D, .25D, .1575D).tex(0, 0).endVertex();
		vb.pos(.595D, .35D, .1575D).tex(0, 1).endVertex();
		vb.pos(.78D, .35D, .1575D).tex(1, 1).endVertex();
		vb.pos(.78D, .25D, .1575D).tex(1, 0).endVertex();

		//front
		vb.pos(.97D, .25D, .4075D).tex(0, 0).endVertex();
		vb.pos(.97D, .35D, .4075D).tex(0, 1).endVertex();
		vb.pos(.78D, .35D, .1575D).tex(1, 1).endVertex();
		vb.pos(.78D, .25D, .1575D).tex(1, 0).endVertex();

		//back
		vb.pos(.595D, .25D, .3425D).tex(0, 0).endVertex();
		vb.pos(.595D, .35D, .3425D).tex(0, 1).endVertex();
		vb.pos(.7825D, .35D, .5925D).tex(1, 1).endVertex();
		vb.pos(.7825D, .25D, .5925D).tex(1, 0).endVertex();
		
		//Transformer->Transformer
		//left
		vb.pos(.7825D, .5D, .155D).tex(0, 0).endVertex();
		vb.pos(.7825D, .6D, .155D).tex(0, 1).endVertex();
		vb.pos(.7825D, .6D, .345D).tex(1, 1).endVertex();
		vb.pos(.7825D, .5D, .345D).tex(1, 0).endVertex();

		//right
		vb.pos(.2175D, .5D, .155D).tex(0, 0).endVertex();
		vb.pos(.2175D, .6D, .155D).tex(0, 1).endVertex();
		vb.pos(.2175D, .6D, .345D).tex(1, 1).endVertex();
		vb.pos(.2175D, .5D, .345D).tex(1, 0).endVertex();

		//front
		vb.pos(.7825D, .5D, .155D).tex(0, 0).endVertex();
		vb.pos(.7825D, .6D, .155D).tex(0, 1).endVertex();
		vb.pos(.2175D, .6D, .155D).tex(1, 1).endVertex();
		vb.pos(.2175D, .5D, .155D).tex(1, 0).endVertex();

		//back
		vb.pos(.7825D, .5D, .345D).tex(0, 0).endVertex();
		vb.pos(.7825D, .6D, .345D).tex(0, 1).endVertex();
		vb.pos(.2175D, .6D, .345D).tex(1, 1).endVertex();
		vb.pos(.2175D, .5D, .345D).tex(1, 0).endVertex();

		Tessellator.getInstance().draw();

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
