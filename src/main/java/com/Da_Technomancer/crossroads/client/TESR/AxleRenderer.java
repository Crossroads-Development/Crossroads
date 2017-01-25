package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.tileentities.rotary.AxleTileEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class AxleRenderer extends TileEntitySpecialRenderer<AxleTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID + ":textures/model/axle.png");
	private final ModelAxle modelAx = new ModelAxle();

	@Override
	public void renderTileEntityAt(AxleTileEntity axle, double x, double y, double z, float partialTicks, int destroyStage){

		if(!axle.getWorld().isBlockLoaded(axle.getPos(), false) || axle.getWorld().getBlockState(axle.getPos()).getBlock() != ModBlocks.axle){
			return;
		}

		EnumFacing.Axis axis = axle.getWorld().getBlockState(axle.getPos()).getValue(Properties.AXIS);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5F, y + .5F, z + .5F);
		GlStateManager.rotate(axis == EnumFacing.Axis.Y ? 0 : 90F, axis == EnumFacing.Axis.Z ? 1 : 0, 0, axis == EnumFacing.Axis.X ? 1 : 0);
		GlStateManager.rotate((axis == EnumFacing.Axis.X ? 1 : -1) * (float) axle.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis)).getAngle(), 0F, 1F, 0F);
		modelAx.render(textureAx, Color.WHITE);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
