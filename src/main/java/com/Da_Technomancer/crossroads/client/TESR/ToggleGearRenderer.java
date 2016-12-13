package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class ToggleGearRenderer extends TileEntitySpecialRenderer<ToggleGearTileEntity>{

	private final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/model/sidedGear.png");
	private final ModelGear model = new ModelGear();
	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID + ":textures/model/axle.png");
	private final ModelAxle modelAx = new ModelAxle();
	
	@Override
	public void renderTileEntityAt(ToggleGearTileEntity gear, double x, double y, double z, float partialTicks, int destroyStage){
		
		if(!gear.getWorld().isBlockLoaded(gear.getPos(), false)){
			return;
		}

		Color color;

		if(gear.getMember() == null){
			return;
		}
		color = gear.getMember().getColor();
		GlStateManager.pushMatrix();
		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-.5F, -1.5F, .5F);
		if(!gear.getWorld().getBlockState(gear.getPos()).getValue(Properties.REDSTONE_BOOL)){
			GlStateManager.translate(0F, -.5F, 0F);
		}
		GlStateManager.rotate((float) gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getAngle(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		model.render();
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5F, .5D, .5F);
		GlStateManager.scale(1D, .75D, 1D);
		GlStateManager.rotate((float) -gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getAngle(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(textureAx);
		modelAx.render();
		GlStateManager.popMatrix();
	}
}
