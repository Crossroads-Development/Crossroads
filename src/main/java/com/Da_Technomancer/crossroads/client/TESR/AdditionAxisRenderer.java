package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.technomancy.AdditionAxisTileEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class AdditionAxisRenderer extends TileEntitySpecialRenderer<AdditionAxisTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureGear = new ResourceLocation(Main.MODID, "textures/model/gear_oct.png");
	private final ModelAxle modelAx = new ModelAxle();
	private final ModelGearOctagon modelGear = new ModelGearOctagon();

	@Override
	public void renderTileEntityAt(AdditionAxisTileEntity axis, double x, double y, double z, float partialTicks, int destroyStage){

		if(!axis.getWorld().isBlockLoaded(axis.getPos(), false) || axis.getWorld().getBlockState(axis.getPos()).getBlock() != ModBlocks.additionAxis){
			return;
		}
		
		boolean orient = axis.getWorld().getBlockState(axis.getPos()).getValue(Properties.ORIENT);
		float angleOne = (float) axis.angleOne;
		float angleTwo = (float) axis.angleTwo;
		float angleThree = (float) axis.angleThree;
		Color col = GearTypes.COPSHOWIUM.getColor();

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(orient ? 90 : 0, 0, 1, 0);
		GlStateManager.translate(-.5D, -.5D, -.5D);

		//Some of the scaling is a tiny bit less than expected values to avoid z-fighting.

		//Axles
		//in 1
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(0, -.15D, 0);
		GlStateManager.scale(1, .7D, 1);
		GlStateManager.rotate(-angleOne, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//in 2
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(0, .375D, 0);
		GlStateManager.scale(1, .25D, 1);
		GlStateManager.rotate(-angleTwo, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//upper height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .875D, .5D);
		GlStateManager.scale(1D, .125D, 1D);
		GlStateManager.rotate(-angleThree, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//chassis
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, .7D, .5D);
		GlStateManager.scale(3, .5D, 3);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Gears
		//upper height control
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, 1.25D, .5D);
		GlStateManager.scale(.5D, 1D, .5D);
		GlStateManager.rotate(-angleThree - 22.5F, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();
		
		//chassis
		GlStateManager.pushMatrix();
		GlStateManager.translate(.5D, .5D, .5D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(-.5D, -.5D, -.5D);
		GlStateManager.translate(.5D, 1.199D, .5D);
		GlStateManager.scale(.5D, 1, .5D);
		GlStateManager.rotate(angleThree, 0, 1F, 0);
		modelGear.render(textureGear, col);
		GlStateManager.popMatrix();

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
