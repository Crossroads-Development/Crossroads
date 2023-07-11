package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.rotary.SteamTurbineTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.common.util.LazyOptional;

public class SteamTurbineRenderer implements BlockEntityRenderer<SteamTurbineTileEntity>{

	protected SteamTurbineRenderer(BlockEntityRendererProvider.Context dispatch){
		super();
	}

	@Override
	public void render(SteamTurbineTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		matrix.translate(0.5D, 0, 0.5D);
		LazyOptional<IAxleHandler> opt = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(opt.isPresent()){
			matrix.mulPose(Axis.YP.rotationDegrees(opt.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		}
		CRModels.renderScrew(matrix, buffer, combinedLight);
	}
}
