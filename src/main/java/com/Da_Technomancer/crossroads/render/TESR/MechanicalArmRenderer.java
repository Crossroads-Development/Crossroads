package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

import java.awt.*;

public class MechanicalArmRenderer extends TileEntityRenderer<MechanicalArmTileEntity>{

	@Override
	public void render(MechanicalArmTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!te.getWorld().isBlockLoaded(te.getPos(), false) || te.getWorld().getBlockState(te.getPos()).getBlock() != CrossroadsBlocks.mechanicalArm){
			return;
		}

		float partialCycle = partialTicks + (float) (te.getWorld().getTotalWorldTime() % 2);//Based on a two tick cycle (to sync with redstone)
		partialCycle /= 2F;
		Color ironColor = GearFactory.findMaterial("Iron").getColor();

		double[] angle = new double[] {te.angle[0] * partialCycle + (1F - partialCycle) * te.angleRecord[0], te.angle[1] * partialCycle + (1F - partialCycle) * te.angleRecord[1], te.angle[2] * partialCycle + (1F - partialCycle) * te.angleRecord[2]};

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + 1D, z + .5D);
		GlStateManager.rotate(180F - (float) Math.toDegrees(angle[0]), 0, 1, 0);

		GlStateManager.translate(0, angle[1] / 2D, 0);

		//Lower arm
		GlStateManager.pushMatrix();
		GlStateManager.scale(2, angle[1], 2);
		ModelAxle.render(ironColor);
		GlStateManager.popMatrix();

		GlStateManager.translate(0, angle[1] / 2D, 0);

		//Connecting joint
		GlStateManager.pushMatrix();
		GlStateManager.scale(4D, 0.25D, 4D);
		ModelAxle.render(ironColor);
		GlStateManager.popMatrix();

		//Upper Arm
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.scale(1, angle[2], 1);
		GlStateManager.translate(0, 0.5D, 0);
		ModelAxle.render(ironColor);
		GlStateManager.popMatrix();

		//Claw Base
		GlStateManager.pushMatrix();
		GlStateManager.translate(-angle[2], 0, 0);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.scale(1, .5D, 1);
		ModelAxle.render(ironColor);
		GlStateManager.popMatrix();

		//Claw Prongs
		GlStateManager.translate(-angle[2] - .1875D, 0, .1875D);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.scale(1, .25D, 1);
		ModelAxle.render(ironColor);
		GlStateManager.translate(0, 0, -.375D);
		ModelAxle.render(ironColor);

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(MechanicalArmTileEntity te){
		return true;
	}
}