package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeamCannonTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class BeamCannonRenderer extends TileEntityRenderer<BeamCannonTileEntity>{

	public BeamCannonRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(BeamCannonTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int combinedOverlayIn){
		if(te.getBlockState().getBlock() != CRBlocks.beamCannon){
			return;
		}
		matrix.push();
		matrix.translate(0.5, 0.5, 0.5);
		matrix.rotate(te.getBlockState().get(CRProperties.FACING).getRotation());
		matrix.rotate(Vector3f.YP.rotation(-te.clientAngle[0]));

		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		TextureAtlasSprite bronzeSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.BRONZE_TEXTURE);
		TextureAtlasSprite barrelSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.BEAM_CANNON_BARREL_TEXTURE);

		//Draw base
		float length = 7F / 16F;
		float width = 5F / 16F;
		float widthInner = 4F / 16F;
		float height = 4F / 16F;
		float heightInner = 1F / 16F;
		float uSt = bronzeSprite.getMinU();
		float uEn = bronzeSprite.getMaxU();
		float uShort = bronzeSprite.getInterpolatedU(3);
		float vSt = bronzeSprite.getMinV();
		float vHeight = bronzeSprite.getInterpolatedV(4);
		float vRim = bronzeSprite.getInterpolatedV(1);
		float vTopSt = bronzeSprite.getInterpolatedV(2);
		float vTopEn = bronzeSprite.getInterpolatedV(3);
		float vTopWidth = bronzeSprite.getInterpolatedV(10);

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
		matrix.rotate(Vector3f.XP.rotation(te.clientAngle[1]));

		float barrelWidth = widthInner - 0.001F;
		float barrelBottom = -1.5F / 16F;
		float barrelTop = 33.5F / 16F;
		float bUTopSt = barrelSprite.getInterpolatedU(8);
		float bUTopEn = barrelSprite.getInterpolatedU(12);
		float bVTopSt = barrelSprite.getInterpolatedV(0);
		float bVTopEn = barrelSprite.getInterpolatedV(4);
		float bUSideSt = barrelSprite.getInterpolatedU(0);
		float bUSideEn = barrelSprite.getInterpolatedU(4);
		float bVSideSt = barrelSprite.getInterpolatedV(16);
		float bVSideEn = barrelSprite.getInterpolatedV(0);

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
			IVertexBuilder beamBuilder = buffer.getBuffer(CRRenderTypes.BEAM_TYPE);
			BeamRenderer.drawBeam(matrix, beamBuilder, te.beamLength, te.beamSize / 8F / (float) Math.sqrt(2), te.beamCol);
		}

		matrix.pop();
	}

	@Override
	public boolean isGlobalRenderer(BeamCannonTileEntity te){
		return true;
	}
}
