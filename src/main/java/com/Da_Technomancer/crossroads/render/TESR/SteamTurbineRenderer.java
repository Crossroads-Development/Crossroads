package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.common.util.LazyOptional;

public class SteamTurbineRenderer extends TileEntityRenderer<SteamTurbineTileEntity>{

	@Override
	public void render(SteamTurbineTileEntity turbine, double x, double y, double z, float partialTicks, int destroyStage){
		if(turbine == null){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + 0.5F, y, z + 0.5F);
		LazyOptional<IAxleHandler> opt = turbine.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(opt.isPresent()){
			GlStateManager.rotated(opt.orElseThrow(NullPointerException::new).getAngle(partialTicks), 0F, 1F, 0F);
		}
		CRModels.renderScrew();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
