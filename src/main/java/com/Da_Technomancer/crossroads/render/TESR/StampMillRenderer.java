package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;

public class StampMillRenderer extends TileEntityRenderer<StampMillTileEntity>{

	protected StampMillRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(StampMillTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		Color ironColor = GearFactory.findMaterial("iron").getColor();

		float prog = te.renderAngle(partialTicks);
		matrix.translate(0.5D, 1.5D, 0.5D);
		if(state.get(CRProperties.HORIZ_AXIS) == Direction.Axis.Z){
			matrix.rotate(Vector3f.YP.rotationDegrees(90));
		}

		//Axle
		matrix.push();
		matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		matrix.rotate(Vector3f.YP.rotationDegrees(-prog));
		CRModels.drawAxle(matrix, buffer, combinedLight, ironColor);

		//Teeth
		for(int i = 0; i < 3; i++){
			matrix.push();
			matrix.translate(0, -13F / 32F + 5F * i / 16F, 0);
			matrix.rotate(Vector3f.YP.rotationDegrees(i * 90 + 90));
			matrix.rotate(Vector3f.ZP.rotationDegrees(90));
			matrix.scale(0.4F, 0.5F, 0.4F);
			CRModels.drawAxle(matrix, buffer, combinedLight, ironColor);
			matrix.pop();
		}

		matrix.pop();

		double offset0 = (Math.sin(2D * Math.toRadians(prog)) + 1D) / 2D * 9D / 32D;
		double offset1 = (Math.sin(2D * Math.toRadians(prog - 90D)) + 1D) / 2D * 9D / 32D;
		matrix.translate(-5F/ 16F, offset1, -2F / 8F);

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());

		//Stamps
		for(int i = 0; i < 3; i++){
			matrix.translate(0, i % 2 == 0 ? offset0 - offset1 : offset1 - offset0, 0);

			//Rod
			float rodRad = 1F / 16F;
			float rodLen = 14F / 16F;

			//Texture coors
			float uRad = sprite.getInterpolatedU(rodRad * 16);
			float u2Rad = sprite.getInterpolatedU(rodRad * 2 * 16);
			float u3Rad = sprite.getInterpolatedU(rodRad * 3 * 16);

			float v0 = sprite.getMinV();
			float vLen = sprite.getInterpolatedV(rodLen * 16);
			float vRad = sprite.getInterpolatedV(rodRad * 16);
			float v3Rad = sprite.getInterpolatedV(rodRad * 3 * 16);

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
			float vDiff = sprite.getInterpolatedV((bottom - rodLen) * 16D);

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
