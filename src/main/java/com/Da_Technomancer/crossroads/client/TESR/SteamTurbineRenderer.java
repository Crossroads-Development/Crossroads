package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelSteamTurbine;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class SteamTurbineRenderer extends TileEntitySpecialRenderer<SteamTurbineTileEntity>{
	private static final ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/model/pump.png");
	private static final ModelSteamTurbine model = new ModelSteamTurbine();

	@Override
	public void renderTileEntityAt(SteamTurbineTileEntity pump, double x, double y, double z, float partialTicks, int destroyStage){

		ResourceLocation r = texture;

		if(pump != null && !pump.getWorld().isBlockLoaded(pump.getPos(), false)){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);

		if(pump == null){
			GlStateManager.scale(.3D, .3D, .3D);
			GlStateManager.translate(-1.5F, -2.2F, 1.2F);
		}else{
			GlStateManager.translate(-.5F, -1.5F, .5F);
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(r);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(pump == null ? 0 : pump.getAngle(), 0F, 1F, 0F);
		model.renderMain();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.rotate(pump == null ? 0 : pump.getCompletion() * 360F, 0F, 1F, 0F);
		model.renderScrew();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}
}
