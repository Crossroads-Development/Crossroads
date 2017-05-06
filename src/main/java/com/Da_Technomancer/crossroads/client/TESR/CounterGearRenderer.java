package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CounterGearTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class CounterGearRenderer extends TileEntitySpecialRenderer<CounterGearTileEntity>{

	private final ModelGearOctagon modelOct = new ModelGearOctagon();
	private final ResourceLocation res = new ResourceLocation(Main.MODID, "textures/model/gear_oct.png");
	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureSc = new ResourceLocation(Main.MODID, "textures/model/screw.png");
	private final ModelAxle modelAx = new ModelAxle();

	@Override
	public void renderTileEntityAt(CounterGearTileEntity gear, double x, double y, double z, float partialTicks, int destroyStage){

		if(!gear.getWorld().isBlockLoaded(gear.getPos(), false)){
			return;
		}

		if(gear.getMember() == null){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + .5D + gear.getHeight(), z + .5D);
		GlStateManager.rotate(-(float) gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getAngle(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(res);
		modelOct.render(res, gear.getMember().getColor());
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.color(1, 1, 1);
		GlStateManager.translate(x + .5F, y + .375F, z + .5F);
		GlStateManager.scale(1D, .76D, 1D);
		GlStateManager.rotate((float) -gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getAngle(), 0F, 1F, 0F);
		modelAx.render(textureSc, textureAx, Color.WHITE);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
