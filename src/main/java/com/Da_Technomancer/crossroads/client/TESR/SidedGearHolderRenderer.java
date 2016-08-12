package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class SidedGearHolderRenderer extends TileEntitySpecialRenderer<SidedGearHolderTileEntity>{

	private static final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/model/sidedGear.png");
	private static final ModelGear model = new ModelGear();
	
	@Override
	public void renderTileEntityAt(SidedGearHolderTileEntity gearHolder, double x, double y, double z, float partialTicks, int destroyStage) {
		
		ResourceLocation r = texture;
		
		if(!gearHolder.getWorld().isBlockLoaded(gearHolder.getPos(), false)){
			return;
		}

		IRotaryHandler handler;
		Color color;
		
		//DOWN 0
		if(gearHolder.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			handler = gearHolder.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN);
			color = handler.getMember().getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-.5F, -1.5F, .5F);
			GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(r);
			model.render();
			GlStateManager.popMatrix();
		}
				
		//UP 1
		if(gearHolder.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.UP)){
			handler = gearHolder.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.UP);
			color = handler.getMember().getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
			GlStateManager.translate(x, y, z);
		    GlStateManager.translate(.5F, -.5F, .5F);
		    GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
		    Minecraft.getMinecraft().renderEngine.bindTexture(r);
		    model.render();
		    GlStateManager.popMatrix();
		}
		
		//NORTH 2
		if(gearHolder.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			handler = gearHolder.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.NORTH);
			color = handler.getMember().getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
			GlStateManager.translate(x, y, z);
		    GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
		    GlStateManager.translate(.5F, -1.5F, .5F);
		    GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
		    Minecraft.getMinecraft().renderEngine.bindTexture(r);
		    model.render();
		    GlStateManager.popMatrix();
		}
		
		//SOUTH 3
		if(gearHolder.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.SOUTH)){
			handler = gearHolder.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.SOUTH);
			color = handler.getMember().getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
			GlStateManager.translate(x, y, z);
		    GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
		    GlStateManager.translate(.5F, -.5F, -.5F);
		    GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
		    Minecraft.getMinecraft().renderEngine.bindTexture(r);
		    model.render();
		    GlStateManager.popMatrix();
		}
		
		//WEST 4
		if(gearHolder.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.WEST)){
			handler = gearHolder.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.WEST);
			color = handler.getMember().getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
			GlStateManager.translate(x, y, z);
		    GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
		    GlStateManager.translate(.5F, -1.5F, .5F);
		    GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
		    Minecraft.getMinecraft().renderEngine.bindTexture(r);
		    model.render();
		    GlStateManager.popMatrix();
		}
		
		//EAST 5
		if(gearHolder.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.EAST)){
			handler = gearHolder.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.EAST);
			color = handler.getMember().getColor();
			GlStateManager.pushMatrix();
			GlStateManager.color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
			GlStateManager.translate(x, y, z);
		    GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
		    GlStateManager.translate(-.5F, -.5F, .5F);
		    GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
		    Minecraft.getMinecraft().renderEngine.bindTexture(r);
		    model.render();
		    GlStateManager.popMatrix();
		}
	}
}
