package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelPump;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class SteamTurbineRenderer extends TileEntitySpecialRenderer<SteamTurbineTileEntity>{
	
	private static final ResourceLocation texture = new ResourceLocation(Main.MODID, "textures/model/pump.png");
	private static final ModelPump model = new ModelPump();

	@Override
	public void renderTileEntityAt(SteamTurbineTileEntity pump, double x, double y, double z, float partialTicks, int destroyStage){
		if(pump == null || pump.getWorld().getBlockState(pump.getPos()).getBlock() != ModBlocks.steamTurbine){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-.5F, -1.5F, .5F);
		GlStateManager.rotate(pump.getCompletion(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		model.renderScrew();
		GlStateManager.popMatrix();
	}
}
