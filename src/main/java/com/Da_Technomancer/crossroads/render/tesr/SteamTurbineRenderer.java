package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.rotary.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.util.LazyOptional;

import java.awt.*;

public class SteamTurbineRenderer implements BlockEntityRenderer<SteamTurbineTileEntity>{

	protected SteamTurbineRenderer(BlockEntityRendererProvider.Context dispatch){
		super();
	}

	@Override
	public void render(SteamTurbineTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		matrix.translate(0.5D, 0.5D, 0.5D);
		LazyOptional<IAxleHandler> opt = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(opt.isPresent()){
			matrix.mulPose(Vector3f.YP.rotationDegrees(opt.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		}
		//Draw central axle
		CRModels.drawAxle(matrix, buffer, combinedLight, new Color(160, 160, 160));

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);

		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		//Spinning turbine
		int bladeCount = 8;
		for(int i = 0; i < bladeCount; i++){
			matrix.pushPose();
			matrix.mulPose(Vector3f.YP.rotationDegrees(i * 360F / bladeCount));
			matrix.scale(0.6F, 1, 1.4F);
			CRModels.drawTurbineBlade(vb, matrix, -0.32F, combinedLight, sprite);
			matrix.popPose();
		}
	}
}
