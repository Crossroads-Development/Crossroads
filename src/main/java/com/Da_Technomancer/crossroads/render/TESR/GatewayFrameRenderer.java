package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import net.minecraft.util.ResourceLocation;

public class GatewayFrameRenderer extends LinkLineRenderer<GatewayFrameTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/gateway.png");

	@Override
	public void render(GatewayFrameTileEntity frame, double x, double y, double z, float partialTicks, int destroyStage){
		if(frame == null || !frame.getWorld().isBlockLoaded(frame.getPos()) || !frame.isActive()){
			return;
		}
		super.render(frame, x, y, z, partialTicks, destroyStage);

		//TODO
	}
}
