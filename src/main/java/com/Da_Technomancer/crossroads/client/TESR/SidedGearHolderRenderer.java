package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class SidedGearHolderRenderer extends TileEntitySpecialRenderer<SidedGearHolderTileEntity>{

	private final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/model/sidedGear.png");
	private final ModelGear model = new ModelGear();

	@Override
	public void renderTileEntityAt(SidedGearHolderTileEntity gearHolder, double x, double y, double z, float partialTicks, int destroyStage){

		if(!gearHolder.getWorld().isBlockLoaded(gearHolder.getPos(), false)){
			return;
		}

		Color color;

		// DOWN 0
		if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			color = gearHolder.getMembers()[0].getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -1.5F, .5F);
			GlStateManager.rotate((float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render();
			GlStateManager.popMatrix();
		}

		// UP 1
		if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			color = gearHolder.getMembers()[1].getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.translate(.5F, -.5F, .5F);
			GlStateManager.rotate((float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render();
			GlStateManager.popMatrix();
		}

		// NORTH 2
		if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			color = gearHolder.getMembers()[2].getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(.5F, -1.5F, .5F);
			GlStateManager.rotate((float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH).getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render();
			GlStateManager.popMatrix();
		}

		// SOUTH 3
		if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.SOUTH)){
			color = gearHolder.getMembers()[3].getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(.5F, -.5F, -.5F);
			GlStateManager.rotate((float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.SOUTH).getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render();
			GlStateManager.popMatrix();
		}

		// WEST 4
		if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST)){
			color = gearHolder.getMembers()[4].getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(.5F, -1.5F, .5F);
			GlStateManager.rotate((float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST).getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render();
			GlStateManager.popMatrix();
		}

		// EAST 5
		if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST)){
			color = gearHolder.getMembers()[5].getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -.5F, .5F);
			GlStateManager.rotate((float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST).getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render();
			GlStateManager.popMatrix();
		}
	}
}
