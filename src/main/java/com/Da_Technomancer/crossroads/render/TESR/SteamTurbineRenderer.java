package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelPump;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;

public class SteamTurbineRenderer extends TileEntityRenderer<SteamTurbineTileEntity>{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/pump.png");

	@Override
	public void render(SteamTurbineTileEntity turbine, double x, double y, double z, float partialTicks, int destroyStage){
		if(turbine == null || turbine.getWorld().getBlockState(turbine.getPos()).getBlock() != CRBlocks.steamTurbine){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);
		GlStateManager.translated(.5F, 0F, .5F);
		GlStateManager.rotated(turbine.getCompletion(), 0F, 1F, 0F);
		Minecraft.getInstance().textureManager.bindTexture(TEXTURE);
		ModelPump.renderScrew();
		GlStateManager.popMatrix();
	}
}
