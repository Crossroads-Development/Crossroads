package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class MechanicalArmRenderer extends TileEntitySpecialRenderer<MechanicalArmTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ModelAxle modelAx = new ModelAxle();
	private final ResourceLocation textureGear = new ResourceLocation(Main.MODID, "textures/model/gear_oct.png");
	private final ModelGearOctagon modelGear = new ModelGearOctagon();

	@Override
	public void renderTileEntityAt(MechanicalArmTileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
		if(!te.getWorld().isBlockLoaded(te.getPos(), false) || te.getWorld().getBlockState(te.getPos()).getBlock() != ModBlocks.mechanicalArm){
			return;
		}

		Color colorGear = GearTypes.COPSHOWIUM.getColor();

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + 1D, z + .5D);
		GlStateManager.rotate(180F - (float) Math.toDegrees(te.angle[0]), 0, 1, 0);
		GlStateManager.rotate(90F - (float) Math.toDegrees(te.angle[1]), 0, 0, 1);

		//Lower arm
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, MechanicalArmTileEntity.LOWER_ARM_LENGTH / 2D, -.25D);
		//Control Lower Arm
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -.0625D, .5D);
		GlStateManager.rotate(22.5F - (float) Math.toDegrees(te.angle[2]), 0, 1, 0);
		GlStateManager.pushMatrix();
		GlStateManager.scale(1, MechanicalArmTileEntity.LOWER_ARM_LENGTH - .125D, 1);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Lower Gear
		GlStateManager.translate(0, .046875 + (MechanicalArmTileEntity.LOWER_ARM_LENGTH / 2D) - .125D, 0);
		GlStateManager.scale(.375D, .375D, .375D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, colorGear);
		GlStateManager.popMatrix();

		//Support Lower Arm
		GlStateManager.scale(1, MechanicalArmTileEntity.LOWER_ARM_LENGTH, 1);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Connecting Joint
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, MechanicalArmTileEntity.LOWER_ARM_LENGTH + .0625D, 0);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 1, 0, 0);
		//Support Box
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -.25D, 0);
		GlStateManager.scale(1.5D, .1875D, 1.5D);
		modelAx.render(textureAx, textureAx, Color.GRAY);
		GlStateManager.popMatrix();

		GlStateManager.rotate(-(float) Math.toDegrees(te.angle[2]) + 90F, 0, 1, 0);

		//Upper Gear
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, .07421875, 0);
		GlStateManager.scale(.375D, .375D, .375D);
		GlStateManager.translate(0, .4375D, 0);
		modelGear.render(textureGear, colorGear);
		GlStateManager.popMatrix();
		
		//Axle
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -.09375, 0);
		GlStateManager.scale(1, .3125D, 1);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Upper Arm
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.scale(.8D, MechanicalArmTileEntity.UPPER_ARM_LENGTH - .375D, .8D);
		GlStateManager.translate(0, .5D, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Claw Base
		GlStateManager.pushMatrix();
		GlStateManager.translate(-MechanicalArmTileEntity.UPPER_ARM_LENGTH + .375D, 0, 0);
		GlStateManager.scale(1, .5D, 1);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		//Claw Prongs
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.translate(.1875D, MechanicalArmTileEntity.UPPER_ARM_LENGTH - .1875D, 0);
		GlStateManager.scale(1, .25D, 1);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.translate(-.375D, 0, 0);
		modelAx.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean isGlobalRenderer(MechanicalArmTileEntity te){
		return true;
	}
}