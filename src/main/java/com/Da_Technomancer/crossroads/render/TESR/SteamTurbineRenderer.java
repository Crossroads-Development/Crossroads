package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class SteamTurbineRenderer extends TileEntityRenderer<SteamTurbineTileEntity>{

	@Override
	public void render(SteamTurbineTileEntity turbine, double x, double y, double z, float partialTicks, int destroyStage){
		if(turbine == null || turbine.getWorld().getBlockState(turbine.getPos()).getBlock() != CRBlocks.steamTurbine){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);
		GlStateManager.translated(.5F, 0F, .5F);
		GlStateManager.rotated(turbine.getCompletion(), 0F, 1F, 0F);
		CRModels.renderScrew();
		GlStateManager.popMatrix();
	}
}
