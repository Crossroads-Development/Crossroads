package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.math.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

public class SteamTurbineRenderer extends BlockEntityRenderer<SteamTurbineTileEntity>{

	protected SteamTurbineRenderer(BlockEntityRenderDispatcher dispatch){
		super(dispatch);
	}

	@Override
	public void render(SteamTurbineTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		matrix.translate(0.5D, 0, 0.5D);
		LazyOptional<IAxleHandler> opt = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(opt.isPresent()){
			matrix.mulPose(Vector3f.YP.rotationDegrees(opt.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		}
		CRModels.renderScrew(matrix, buffer, combinedLight);
	}
}
