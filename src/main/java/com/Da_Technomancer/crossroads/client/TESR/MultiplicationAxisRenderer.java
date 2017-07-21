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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class MultiplicationAxisRenderer extends TileEntitySpecialRenderer<MultiplicationAxisTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureGear = new ResourceLocation(Main.MODID, "textures/model/gear_oct.png");
	private final ResourceLocation texturePyr = new ResourceLocation(Main.MODID, "textures/model/pyramid.png");
	private final ResourceLocation textureSpr = new ResourceLocation(Main.MODID, "textures/model/spring.png");
	private final ResourceLocation textureBelt = new ResourceLocation(Main.MODID, "textures/model/belt.png");
	private final ModelAxle modelAx = new ModelAxle();
	private final ModelGearOctagon modelGear = new ModelGearOctagon();
	private final ModelPyramid modelPyr = new ModelPyramid();

	@Override
	public void render(MultiplicationAxisTileEntity axis, double x, double y, double z, float partialTicks, int destroyStage, float alpha){

		if(!axis.getWorld().isBlockLoaded(axis.getPos(), false)){
			return;
		}
		IBlockState state = axis.getWorld().getBlockState(axis.getPos());
		if(state.getBlock() != ModBlocks.multiplicationAxis){
			return;
		}
		
		EnumFacing facing = state.getValue(Properties.FACING);
		double speedTwo = axis.lastInTwo;
		float angleOne = (float) axis.angleOne;
		float angleTwo = (float) axis.angleTwo;
		float angleThree = (float) axis.angleThree;
		float angleTwoPos = (float) axis.angleTwoPos;
		Color col = GearTypes.COPSHOWIUM.getColor();

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(-facing.getHorizontalAngle() - 90, 0, 1, 0);
		if(state.getValue(Properties.REDSTONE_BOOL)){
			GlStateManager.rotate(180, 0, 1, 0);
		}
		GlStateManager.translate(-.5D, -.5D, -.5D);

		//Some of the scaling is a tiny bit less than expected values to avoid z-fighting.

		//Axles
		//first carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.0625D, .21875D, .5D);
		GlStateManager.scale(1D / 3D, .4374D, 1D / 3D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//first transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .59375D - (Math.min(8D, Math.abs(speedTwo)) * .0234375D), .25D);
		GlStateManager.scale(1D, .3125D, 1D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .5D - (Math.signum(speedTwo) * .1328125D), .25D);
		GlStateManager.scale(1D, .453125D, 1D);
		GlStateManager.rotate(angleThree * (float) -Math.signum(speedTwo), 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//upper height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .749D, .5D);
		GlStateManager.scale(1D, .5D, 1D);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//sign height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(-10, 0, 1, 0);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .45D, .35D);
		GlStateManager.scale(1D / 3D, 1D / 3D, .15D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//lower height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .325D, .5D);
		GlStateManager.rotate(angleTwo, 0, 1F, 0);
		GlStateManager.scale(1D / 3D, .15D, 1D / 3D);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//ratchet 1 in
		GlStateManager.pushMatrix();
		GlStateManager.translate(.4375D, .1875D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(angleTwo + 22.5F, 0, 1F, 0);
		GlStateManager.scale(1D / 3D, .25D, 1D / 3D);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//ratchet 1
		GlStateManager.pushMatrix();
		GlStateManager.translate(.25D, .1875D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-Math.abs(angleTwo) + 22.5F, 0, 1F, 0);
		GlStateManager.scale(1, .125D, 1);
		modelAx.render(textureAx, textureAx, Color.GRAY);
		GlStateManager.popMatrix();

		//to ratchet 2
		GlStateManager.pushMatrix();
		GlStateManager.translate(.375D, .1875D, .625D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		GlStateManager.scale(1D / 3D, .125D, 1D / 3D);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//ratchet 2
		GlStateManager.pushMatrix();
		GlStateManager.translate(.25D, .1875D, .75D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(1, .125D, 1);
		modelAx.render(textureAx, textureAx, Color.GRAY);
		GlStateManager.popMatrix();

		//horizontal spring height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.171875D, .51953125D, .38D);
		GlStateManager.rotate(-15, 0, 1, 0);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(-angleTwoPos, 0, 1F, 0);
		GlStateManager.scale(1D / 3D, .365D, 1D / 3D);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//lower spring height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1171875D, .35D, .625D);
		GlStateManager.rotate(angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(1D / 3D, .2D, 1D / 3D);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Pyramid
		//first transformer
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .34375 - (Math.min(8, Math.abs(speedTwo)) * .0234375D), .25D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		modelPyr.render(texturePyr, Color.WHITE);
		GlStateManager.popMatrix();

		//Gears
		//first carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.0625D, .5D, .5D);
		GlStateManager.rotate(angleOne, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//second carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.875D, .5D, .38D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		GlStateManager.scale(.1875D, .7D, .1875D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//tranformer to second carrier
		GlStateManager.pushMatrix();
		GlStateManager.translate(.69921875D, .5D, .25D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-angleThree + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.1875D, .1875D, .1875D);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//second transformer top
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .8203125D - (Math.signum(speedTwo) * .1328125D), .25D);
		GlStateManager.scale(.1875D, .1875D, .1875D);
		GlStateManager.rotate(angleThree * (float) -Math.signum(speedTwo), 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//second transformer bottom
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .34375D - (Math.signum(speedTwo) * .1328125D), .25D);
		GlStateManager.scale(.1875D, .1875D, .1875D);
		GlStateManager.rotate(angleThree * (float) -Math.signum(speedTwo), 0, 1F, 0);
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
		GlStateManager.translate(.5D, .35D, .5D);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet in
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .25D, .5D);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet start
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5703125D, .1875D, .5D);
		GlStateManager.rotate(270, 0, 0, 1);
		GlStateManager.rotate(-angleTwo + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet 1 start
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3203125D, .1875D, .5D);
		GlStateManager.rotate(270, 0, 0, 1);
		GlStateManager.rotate(-angleTwo + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet 1 end
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1796875D, .1875D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet 2 end
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1796875D, .1875D, .75D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet 2 in
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3203125D, .1875D, .75D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(angleTwoPos, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet 1 to axle
		GlStateManager.pushMatrix();
		GlStateManager.translate(.375D, .1875D, .55859375D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//axle to ratchet 2
		GlStateManager.pushMatrix();
		GlStateManager.translate(.375D, .1875D, .69140625D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet connector
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1796875D, .1875D, .625D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(angleTwoPos, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//ratchet to arm
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1171875D, .25D, .625D);
		GlStateManager.rotate(angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//spring shifter
		GlStateManager.pushMatrix();
		GlStateManager.translate(.225D, .51953125D, .1875D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(15, 0, 0, 1);
		GlStateManager.rotate(-angleTwoPos, 0, 1F, 0);
		GlStateManager.scale(.15D, .625D, .15D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//arm top
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1171875D, .45703125D, .625D);
		GlStateManager.rotate(angleTwoPos + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//arm mid
		GlStateManager.pushMatrix();
		GlStateManager.translate(.1171875D, .51953125D, .5625D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(15, 0, 0, 1);
		GlStateManager.rotate(-angleTwoPos, 0, 1F, 0);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//middle height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(10, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .49D, .55D);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//sign shifter
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(10, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .51D, .55D);
		GlStateManager.scale(.15D, .625D, .15D);
		GlStateManager.rotate(-angleTwo + 45F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//in
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(.5D, .046875D, .5D);
		GlStateManager.scale(.125D, .125D, .125D);
		GlStateManager.rotate(angleOne + 45F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//out
		GlStateManager.pushMatrix();
		GlStateManager.translate(.98125D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-angleThree + 22.5F, 0, 1F, 0);
		GlStateManager.scale(.1875D, .3D, .1875D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		//Springs
		//first transformer bottom
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .16D - (Math.min(8D, Math.abs(speedTwo)) * .01171875D), .25D);
		GlStateManager.scale(1D, .1875D - (Math.min(8D, Math.abs(speedTwo)) * .0234375D), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer bottom
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .16D - (Math.signum(speedTwo) * .1328125D / 2D), .25D);
		GlStateManager.scale(1D, .1875D * (1 - (Math.signum(speedTwo) * .70833333333D)), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//first transformer top
		GlStateManager.pushMatrix();
		GlStateManager.translate(.3125D, .84D - (Math.min(8D, Math.abs(speedTwo)) * .01171875D), .25D);
		GlStateManager.scale(1D, .1875D + (Math.min(8D, Math.abs(speedTwo)) * .0234375D), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//second transformer top
		GlStateManager.pushMatrix();
		GlStateManager.translate(.6875D, .84D - (Math.signum(speedTwo) * .1328125D / 2D), .25D);
		GlStateManager.scale(1D, .1875D * (1 + (Math.signum(speedTwo) * .70833333333D)), 1D);
		modelAx.render(textureSpr, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Belts
		GlStateManager.disableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(textureBelt);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		//In->Transformer
		float growth = 1F - (float) (Math.min(8, Math.abs(speedTwo))) / 8F;

		//in front
		vb.pos(.035D, .23D, .47D).tex(0, 0).endVertex();
		vb.pos(.035D, .26D, .47D).tex(0, 1).endVertex();
		vb.pos(.035D, .26D, .53D).tex(1, 1).endVertex();
		vb.pos(.035D, .23D, .53D).tex(1, 0).endVertex();

		//in back
		vb.pos(.035D, .23D, .53D).tex(0, 0).endVertex();
		vb.pos(.035D, .26D, .53D).tex(0, 1).endVertex();
		vb.pos(.1D, .26D, .53D).tex(1, 1).endVertex();
		vb.pos(.1D, .23D, .53D).tex(1, 0).endVertex();

		//transformer back
		vb.pos(.33D + (growth * .075D), .23D, .23D - (growth * .0725D)).tex(0, 0).endVertex();
		vb.pos(.33D + (growth * .075D), .26D, .23D - (growth * .0725D)).tex(0, 1).endVertex();
		vb.pos(.33D + (growth * .075D), .26D, .27D + (growth * .0725D)).tex(1, 1).endVertex();
		vb.pos(.33D + (growth * .075D), .23D, .27D + (growth * .0725D)).tex(1, 0).endVertex();

		//transformer front
		vb.pos(.33D + (growth * .075D), .23D, .23D - (growth * .0725D)).tex(0, 0).endVertex();
		vb.pos(.33D + (growth * .075D), .26D, .23D - (growth * .0725D)).tex(0, 1).endVertex();
		vb.pos(.3D - (growth * .08D), .26D, .23D - (growth * .0725D)).tex(1, 1).endVertex();
		vb.pos(.3D - (growth * .08D), .23D, .23D - (growth * .0725D)).tex(1, 0).endVertex();

		//front
		vb.pos(.035D, .23D, .47D).tex(0, 0).endVertex();
		vb.pos(.035D, .26D, .47D).tex(0, 1).endVertex();
		vb.pos(.3D - (growth * .08D), .26D, .23D - (growth * .0725D)).tex(1, 1).endVertex();
		vb.pos(.3D - (growth * .08D), .23D, .23D - (growth * .0725D)).tex(1, 0).endVertex();

		//back
		vb.pos(.33D + (growth * .075D), .23D, .27D + (growth * .0725D)).tex(0, 0).endVertex();
		vb.pos(.33D + (growth * .075D), .26D, .27D + (growth * .0725D)).tex(0, 1).endVertex();
		vb.pos(.1D, .26D, .53D).tex(1, 1).endVertex();
		vb.pos(.1D, .23D, .53D).tex(1, 0).endVertex();

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
