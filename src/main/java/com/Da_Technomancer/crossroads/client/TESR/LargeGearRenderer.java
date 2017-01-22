package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelLargeGear;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class LargeGearRenderer extends TileEntitySpecialRenderer<LargeGearMasterTileEntity>{

	protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID + ":textures/model/largeGear.png");
	private static final ModelLargeGear model = new ModelLargeGear();

	@Override
	public void renderTileEntityAt(LargeGearMasterTileEntity gear, double x, double y, double z, float partialTicks, int destroyStage){

		if(gear.getWorld().getBlockState(gear.getPos()).getBlock() != ModBlocks.largeGearMaster || !gear.getWorld().isBlockLoaded(gear.getPos(), false)){
			return;
		}

		IAxleHandler handler;
		Color color = gear.getMember().getColor();

		GlStateManager.pushMatrix();
		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
		GlStateManager.translate(x, y, z);
		if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -1.5F, .5F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			GlStateManager.translate(.5F, -.5F, .5F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST)){
			GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -.5F, .5F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST)){
			GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(.5F, -1.5F, .5F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(.5F, -1.5F, .5F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH);
		}else{
			GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(.5F, -.5F, -.5F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.SOUTH);
		}
		if(handler != null){
			GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
			model.render();
		}
		GlStateManager.popMatrix();

	}
}
