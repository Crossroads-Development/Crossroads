package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class LargeGearRenderer extends TileEntitySpecialRenderer<LargeGearMasterTileEntity>{

	private static final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/model/largeGear.png");
	private static final ModelLargeGear model = new ModelLargeGear();

	@Override
	public void renderTileEntityAt(LargeGearMasterTileEntity gear, double x, double y, double z, float partialTicks, int destroyStage){

		if(!gear.getWorld().isBlockLoaded(gear.getPos(), false) || !gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, null)){
			return;
		}

		IRotaryHandler handler = gear.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, null);
		Color color = handler.getMember().getColor();

		GlStateManager.pushMatrix();
		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		GlStateManager.translate(x, y, z);
		if(gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -1.5F, .5F);
		}else if(gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.UP)){
			GlStateManager.translate(.5F, -.5F, .5F);
		}else if(gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.EAST)){
			GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -.5F, .5F);
		}else if(gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.WEST)){
			GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(.5F, -1.5F, .5F);
		}else if(gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(.5F, -1.5F, .5F);
		}else{
			GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(.5F, -.5F, -.5F);
		}
		GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		model.render();
		GlStateManager.popMatrix();

	}
}
