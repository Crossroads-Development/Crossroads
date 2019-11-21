package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelPump;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;

public class SteamTurbineRenderer extends TileEntityRenderer<SteamTurbineTileEntity>{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/pump.png");
	private static final ModelPump MODEL = new ModelPump();

	@Override
	public void render(SteamTurbineTileEntity turbine, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(turbine == null || turbine.getWorld().getBlockState(turbine.getPos()).getBlock() != CrossroadsBlocks.steamTurbine){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5F, 0F, .5F);
		GlStateManager.rotate(turbine.getCompletion(), 0F, 1F, 0F);
		Minecraft.getInstance().textureManager.bindTexture(TEXTURE);
		MODEL.renderScrew();
		GlStateManager.popMatrix();
	}
}
