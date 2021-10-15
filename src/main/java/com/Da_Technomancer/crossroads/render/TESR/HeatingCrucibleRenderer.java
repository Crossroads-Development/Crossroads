package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class HeatingCrucibleRenderer implements BlockEntityRenderer<HeatingCrucibleTileEntity>{

	protected HeatingCrucibleRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(HeatingCrucibleTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		if(te.getActiveTexture() == null || te.getBlockState().getBlock() != CRBlocks.heatingCrucible){
			return;
		}
		int fullness = te.getBlockState().getValue(CRProperties.FULLNESS);
		if(fullness == 0){
			return;
		}

		VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(te.getActiveTexture());

		int light = CRRenderUtil.calcMediumLighting(combinedLight);//We want the molten fluid to glow in the dark slightly
		float xzStart = 2F / 16F;
		float xzEnd = 14F / 16F;
		float height = (float) (2 + 4 * fullness) / 16F;

		CRRenderUtil.addVertexBlock(builder, matrix, xzEnd, height, xzStart, sprite.getU(xzEnd * 16), sprite.getV(16 - (xzStart * 16)), 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, xzStart, height, xzStart, sprite.getU(xzStart * 16), sprite.getV(16 - (xzStart * 16)), 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, xzStart, height, xzEnd, sprite.getU(xzStart * 16), sprite.getV(16 - (xzEnd * 16)), 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, xzEnd, height, xzEnd, sprite.getU(xzEnd * 16), sprite.getV(16 - (xzEnd * 16)), 0, 1, 0, light);
	}
}
