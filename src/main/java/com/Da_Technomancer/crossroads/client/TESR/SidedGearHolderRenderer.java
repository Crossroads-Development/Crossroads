package com.Da_Technomancer.crossroads.client.TESR;

import java.awt.Color;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class SidedGearHolderRenderer extends TileEntitySpecialRenderer<SidedGearHolderTileEntity>{

	private final ModelGearOctagon modelOct = new ModelGearOctagon();
	private final ResourceLocation res = new ResourceLocation(Main.MODID, "textures/model/gearOct.png");

	@Override
	public void renderTileEntityAt(SidedGearHolderTileEntity gearHolder, double x, double y, double z, float partialTicks, int destroyStage){

		if(!gearHolder.getWorld().isBlockLoaded(gearHolder.getPos(), false)){
			return;
		}

		Color color;
		
		for(EnumFacing side : EnumFacing.values()){
			if(gearHolder.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side)){
				color = gearHolder.getMembers()[side.getIndex()].getColor();
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.disableLighting();
				GlStateManager.translate(x + .5D, y + .5D, z + .5D);
				GlStateManager.rotate(side == EnumFacing.DOWN ? 0 : side == EnumFacing.UP ? 180F : side == EnumFacing.NORTH || side == EnumFacing.EAST ? 90F : -90F, side.getAxis() == EnumFacing.Axis.Z ? 1 : 0, 0, side.getAxis() == EnumFacing.Axis.Z ? 0 : 1);
				GlStateManager.rotate(-(float) gearHolder.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side).getAngle(), 0F, 1F, 0F);
				modelOct.render(res, color);
				GlStateManager.enableLighting();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
	}
}
