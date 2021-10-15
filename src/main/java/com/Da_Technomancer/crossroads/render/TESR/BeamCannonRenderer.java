package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeamCannonTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BeamCannonRenderer implements BlockEntityRenderer<BeamCannonTileEntity>{

	public BeamCannonRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(BeamCannonTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light, int combinedOverlayIn){
		if(te.getBlockState().getBlock() != CRBlocks.beamCannon){
			return;
		}
		matrix.pushPose();
		matrix.translate(0.5, 0.5, 0.5);
		matrix.mulPose(te.getBlockState().getValue(CRProperties.FACING).getRotation());
		matrix.mulPose(Vector3f.YP.rotation(-te.clientAngle[0]));

		VertexConsumer builder = buffer.getBuffer(RenderType.solid());
		TextureAtlasSprite bronzeSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.BRONZE_TEXTURE);
		TextureAtlasSprite barrelSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.BEAM_CANNON_BARREL_TEXTURE);

		//Draw base
		float length = 7F / 16F;
		float width = 5F / 16F;
		float widthInner = 4F / 16F;
		float height = 4F / 16F;
		float heightInner = 1F / 16F;
		float uSt = bronzeSprite.getU0();
		float uEn = bronzeSprite.getU1();
		float uShort = bronzeSprite.getU(3);
		float vSt = bronzeSprite.getV0();
		float vHeight = bronzeSprite.getV(4);
		float vRim = bronzeSprite.getV(1);
		float vTopSt = bronzeSprite.getV(2);
		float vTopEn = bronzeSprite.getV(3);
		float vTopWidth = bronzeSprite.getV(10);

		matrix.translate(0, 1F / 16F, 0);
		
		//Outer base length side (-)
		CRRenderUtil.addVertexBlock(builder, matrix, -width, 0, -length, uSt, vHeight, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, 0, length, uEn, vHeight, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, height, length, uEn, vSt, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, height, -length, uSt, vSt, -1, 0, 0, light);

		//Outer base length side (+)
		CRRenderUtil.addVertexBlock(builder, matrix, width, 0, -length, uSt, vHeight, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, height, -length, uSt, vSt, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, height, length, uEn, vSt, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, 0, length, uEn, vHeight, 1, 0, 0, light);

		//Inner base length side (-)
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, 0, -length, uSt, vHeight, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, height, -length, uSt, vSt, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, height, length, uEn, vSt, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, 0, length, uEn, vHeight, -1, 0, 0, light);

		//Inner base length side (+)
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, 0, -length, uSt, vHeight, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, 0, length, uEn, vHeight, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, height, length, uEn, vSt, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, height, -length, uSt, vSt, 1, 0, 0, light);

		//Inner base top
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, heightInner, -length, uSt, vTopWidth, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, heightInner, length, uEn, vTopWidth, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, heightInner, length, uEn, vTopSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, heightInner, -length, uSt, vTopSt, 0, 1, 0, light);

		//Base top (-)
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, height, -length, uSt, vTopEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, height, -length, uSt, vTopSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, height, length, uEn, vTopSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, height, length, uEn, vTopEn, 0, 1, 0, light);

		//Base top (+)
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, height, -length, uSt, vTopEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, height, length, uEn, vTopEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, height, length, uEn, vTopSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, height, -length, uSt, vTopSt, 0, 1, 0, light);

		//Outer base end bottom (-)
		CRRenderUtil.addVertexBlock(builder, matrix, -width, 0, -length, uSt, vSt, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, heightInner, -length, uSt, vRim, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, heightInner, -length, uEn, vRim, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, 0, -length, uEn, vSt, 0, 0, -1, light);

		//Outer base end bottom (+)
		CRRenderUtil.addVertexBlock(builder, matrix, -width, 0, length, uSt, vSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, 0, length, uEn, vSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, heightInner, length, uEn, vRim, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, heightInner, length, uSt, vRim, 0, 0, 1, light);

		//Outer base end top (- end, - side)
		CRRenderUtil.addVertexBlock(builder, matrix, -width, heightInner, -length, uSt, vSt, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, height, -length, uShort, vSt, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, height, -length, uShort, vRim, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, heightInner, -length, uSt, vRim, 0, 0, -1, light);

		//Outer base end top (- end, + side)
		CRRenderUtil.addVertexBlock(builder, matrix, width, heightInner, -length, uSt, vSt, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, heightInner, -length, uSt, vRim, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, height, -length, uShort, vRim, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, height, -length, uShort, vSt, 0, 0, -1, light);

		//Outer base end top (+ end, - side)
		CRRenderUtil.addVertexBlock(builder, matrix, -width, heightInner, length, uSt, vSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, heightInner, length, uSt, vRim, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -widthInner, height, length, uShort, vRim, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -width, height, length, uShort, vSt, 0, 0, 1, light);

		//Outer base end top (+ end, + side)
		CRRenderUtil.addVertexBlock(builder, matrix, width, heightInner, length, uSt, vSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, width, height, length, uShort, vSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, height, length, uShort, vRim, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, widthInner, heightInner, length, uSt, vRim, 0, 0, 1, light);

		//Draw barrel
		matrix.translate(0, 2.5F / 16F, 0);
		matrix.mulPose(Vector3f.XP.rotation(te.clientAngle[1]));

		float barrelWidth = widthInner - 0.001F;
		float barrelBottom = -1.5F / 16F;
		float barrelTop = 33.5F / 16F;
		float bUTopSt = barrelSprite.getU(8);
		float bUTopEn = barrelSprite.getU(12);
		float bVTopSt = barrelSprite.getV(0);
		float bVTopEn = barrelSprite.getV(4);
		float bUSideSt = barrelSprite.getU(0);
		float bUSideEn = barrelSprite.getU(4);
		float bVSideSt = barrelSprite.getV(16);
		float bVSideEn = barrelSprite.getV(0);

		//Bottom
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelBottom, -barrelWidth, bUTopSt, bVTopSt, 0,  -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelBottom, -barrelWidth, bUTopEn, bVTopSt, 0,  -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelBottom, barrelWidth, bUTopEn, bVTopEn, 0,  -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelBottom, barrelWidth, bUTopSt, bVTopEn, 0,  -1, 0, light);
		//Top
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelTop, -barrelWidth, bUTopSt, bVTopSt, 0,  1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelTop, barrelWidth, bUTopSt, bVTopEn, 0,  1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelTop, barrelWidth, bUTopEn, bVTopEn, 0,  1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelTop, -barrelWidth, bUTopEn, bVTopSt, 0,  1, 0, light);
		//-X
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelBottom, -barrelWidth, bUSideSt, bVSideSt, -1,  0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelBottom, barrelWidth, bUSideEn, bVSideSt, -1,  0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelTop, barrelWidth, bUSideEn, bVSideEn, -1,  0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelTop, -barrelWidth, bUSideSt, bVSideEn, -1,  0, 0, light);
		//+X
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelBottom, -barrelWidth, bUSideSt, bVSideSt, 1,  0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelTop, -barrelWidth, bUSideSt, bVSideEn, 1,  0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelTop, barrelWidth, bUSideEn, bVSideEn, 1,  0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelBottom, barrelWidth, bUSideEn, bVSideSt, 1,  0, 0, light);
		//-Z
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelBottom, -barrelWidth, bUSideSt, bVSideSt, 0,  0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelTop, -barrelWidth, bUSideSt, bVSideEn, 0,  0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelTop, -barrelWidth, bUSideEn, bVSideEn, 0,  0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelBottom, -barrelWidth, bUSideEn, bVSideSt, 0,  0, -1, light);
		//+Z
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelBottom, barrelWidth, bUSideSt, bVSideSt, 0,  0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelBottom, barrelWidth, bUSideEn, bVSideSt, 0,  0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, barrelWidth, barrelTop, barrelWidth, bUSideEn, bVSideEn, 0,  0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -barrelWidth, barrelTop, barrelWidth, bUSideSt, bVSideEn, 0,  0, 1, light);

		//Render the beam
		if(te.beamLength > 0){
			VertexConsumer beamBuilder = buffer.getBuffer(CRRenderTypes.BEAM_TYPE);
			matrix.translate(0, barrelTop, 0);
			BeamRenderer.drawBeam(matrix, beamBuilder, 1 + Math.max(0, te.beamLength - (barrelTop + 3.5F / 16F)), te.beamSize / 8F / (float) Math.sqrt(2), te.beamCol);
		}

		matrix.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(BeamCannonTileEntity te){
		return true;
	}
}
