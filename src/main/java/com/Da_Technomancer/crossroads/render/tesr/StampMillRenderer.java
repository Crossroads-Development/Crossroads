package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.StampMillTileEntity;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

public class StampMillRenderer implements BlockEntityRenderer<StampMillTileEntity>{

	protected StampMillRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(StampMillTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		Color ironColor = CRMaterialLibrary.findMaterial("iron").getColor();

		float prog = te.renderAngle(partialTicks);
		matrix.translate(0.5D, 1.5D, 0.5D);
		if(state.getValue(CRProperties.HORIZ_AXIS) == Direction.Axis.Z){
			matrix.mulPose(Axis.YP.rotationDegrees(90));
		}

		//Axle
		matrix.pushPose();
		matrix.mulPose(Axis.ZP.rotationDegrees(90));
		matrix.mulPose(Axis.YP.rotationDegrees(-prog));
		CRModels.drawAxle(matrix, buffer, combinedLight, ironColor);

		//Teeth
		for(int i = 0; i < 3; i++){
			matrix.pushPose();
			matrix.translate(0, -13F / 32F + 5F * i / 16F, 0);
			matrix.mulPose(Axis.YP.rotationDegrees(i * 90 + 90));
			matrix.mulPose(Axis.ZP.rotationDegrees(90));
			matrix.scale(0.4F, 0.5F, 0.4F);
			CRModels.drawAxle(matrix, buffer, combinedLight, ironColor);
			matrix.popPose();
		}

		matrix.popPose();

		double[] offset = new double[2];
		for(int i = 0; i < 2; i++){
			double angle = ((2D * Math.toRadians(prog - (i == 1 ? 90D : 0))) % (2D * Math.PI) + 2D * Math.PI) % (2D * Math.PI);
			//Pattern of up-down movement where it rises slowly with the turn of the axle (sin(angle)), but drops quickly and has a rest period at the bottom
			double position = 0;//In [0, 1]
			double dropStart = Math.PI / 2;
			double dropEnd = Math.PI * 0.7;
			if(angle > 3 * Math.PI / 2 || angle < dropStart){
				//Slow rise
				position = (Math.sin(angle) + 1D) / 2D;
			}else if(angle < dropEnd){
				//Rapid drop
				position = 1 - (angle - dropStart) / (dropEnd - dropStart);
			}
			offset[i] = position * 8.3D / 32D;
		}
		matrix.translate(-5F/ 16F, offset[1], -2F / 8F);

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());

		//Stamps
		for(int i = 0; i < 3; i++){
			//Alternates the vertical offset between offset[0] and offset[1]
			matrix.translate(0, i % 2 == 0 ? offset[0] - offset[1] : offset[1] - offset[0], 0);

			//Rod
			float rodRad = 1F / 16F;
			float rodLen = 14F / 16F;

			//Texture coors
			float uRad = sprite.getU(rodRad * 16);
			float u2Rad = sprite.getU(rodRad * 2 * 16);
			float u3Rad = sprite.getU(rodRad * 3 * 16);

			float v0 = sprite.getV0();
			float vLen = sprite.getV(rodLen * 16);
			float vRad = sprite.getV(rodRad * 16);
			float v3Rad = sprite.getV(rodRad * 3 * 16);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, 0, -rodRad, uRad, v0, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, -rodRad, u3Rad, v0, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, -rodRad, u3Rad, vLen, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, -rodRad, uRad, vLen, 0, 0, -1, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, 0, rodRad, uRad, v0, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, rodRad, uRad, vLen, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, rodRad, u3Rad, vLen, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, rodRad, u3Rad, v0, 0, 0, 1, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, 0, -rodRad, uRad, v0, -1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, -rodRad, uRad, vLen, -1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, rodRad, u3Rad, vLen, -1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, 0, rodRad, u3Rad, v0, -1, 0, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, -rodRad, uRad, v0, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, rodRad, u3Rad, v0, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, rodRad, u3Rad, vLen, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, -rodRad, uRad, vLen, 1, 0, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, 0, -rodRad, uRad, vRad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, 0, rodRad, uRad, v3Rad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, rodRad, u3Rad, v3Rad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, -rodRad, u3Rad, vRad, 0, -1, 0, combinedLight);

			//Pin
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, -rodRad, uRad, vRad, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, 0, -rodRad, u2Rad, vRad, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, -2F * rodRad, -rodRad, u2Rad, v3Rad, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -2F * rodRad, -rodRad, uRad, v3Rad, 0, 0, -1, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, rodRad, uRad, vRad, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -2F * rodRad, rodRad, uRad, v3Rad, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, -2F * rodRad, rodRad, u3Rad, v3Rad, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, 0, rodRad, u3Rad, vRad, 0, 0, 1, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, 0, -rodRad, uRad, vRad, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, 0, rodRad, u3Rad, vRad, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, -2F * rodRad, rodRad, u3Rad, v3Rad, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, -2F * rodRad, -rodRad, uRad, v3Rad, 1, 0, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, -rodRad, uRad, vRad, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, 0, rodRad, uRad, v3Rad, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, 0, rodRad, u2Rad, v3Rad, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, 0, -rodRad, u2Rad, vRad, 0, 1, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -2F * rodRad, -rodRad, uRad, vRad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, -2F * rodRad, -rodRad, u2Rad, vRad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, 2F * rodRad, -2F * rodRad, rodRad, u2Rad, v3Rad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -2F * rodRad, rodRad, uRad, v3Rad, 0, -1, 0, combinedLight);

			//Stamp Head
			rodRad = 1F / 8F;
			float bottom = 1.25F;
			float vDiff = sprite.getV((bottom - rodLen) * 16D);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, -rodRad, uRad, v0, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, -rodRad, u3Rad, v0, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -bottom, -rodRad, u3Rad, vDiff, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -bottom, -rodRad, uRad, vDiff, 0, 0, -1, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, rodRad, uRad, v0, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -bottom, rodRad, uRad, vDiff, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -bottom, rodRad, u3Rad, vDiff, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, rodRad, u3Rad, v0, 0, 0, 1, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, -rodRad, uRad, v0, -1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -bottom, -rodRad, uRad, vDiff, -1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -bottom, rodRad, u3Rad, vDiff, -1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, rodRad, u3Rad, v0, -1, 0, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, -rodRad, uRad, v0, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, rodRad, u3Rad, v0, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -bottom, rodRad, u3Rad, vDiff, 1, 0, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -bottom, -rodRad, uRad, vDiff, 1, 0, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, -rodRad, uRad, vRad, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -rodLen, rodRad, uRad, v3Rad, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, rodRad, u3Rad, v3Rad, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -rodLen, -rodRad, u3Rad, vRad, 0, 1, 0, combinedLight);

			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -bottom, -rodRad, uRad, vRad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -bottom, -rodRad, u3Rad, vRad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, rodRad, -bottom, rodRad, u3Rad, v3Rad, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -rodRad, -bottom, rodRad, uRad, v3Rad, 0, -1, 0, combinedLight);

			matrix.translate(5F / 16F, 0, 0);
		}
	}
}
