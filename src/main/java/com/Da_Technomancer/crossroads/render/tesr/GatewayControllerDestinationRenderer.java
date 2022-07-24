package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.blocks.technomancy.GatewayControllerDestinationTileEntity;
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
import net.minecraft.core.Direction;

public class GatewayControllerDestinationRenderer implements BlockEntityRenderer<GatewayControllerDestinationTileEntity>{

	protected GatewayControllerDestinationRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(GatewayControllerDestinationTileEntity frame, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		//Stripped-down version of the renderer in GatewayControllerRenderer, without the dialing wheel, triangular selector, or link lines

		if(!frame.isActive()){
			//Only do rendering if this is the top-center block of a formed multiblock (the core renders the entire gateway)
			return;
		}

		float radius = frame.getSize() / 2F;
		Direction.Axis plane = frame.getPlane();
		boolean linked = frame.chevrons[frame.chevrons.length - 1] != null;//Whether this gateway is active and linked to another

		//Define lighting based on the interior of the frame, as the frame itself is solid blocks
		combinedLight = CRRenderUtil.getLightAtPos(frame.getLevel(), frame.getBlockPos().below(frame.getSize() / 5));

		//Render everything about the center of the multiblock, so we can rotate
		matrix.translate(0.5D, 1 - radius, 0.5D);

		//Rotate to align with the frame if applicable
		if(plane == Direction.Axis.Z){
			matrix.mulPose(Vector3f.YP.rotationDegrees(90));
		}

		//From this point, the frame is in the X-Y rendering plane
		matrix.scale(radius, radius, 1F);

		//From this point, everything should be rendered within [-1, 1] on the x-y; it will be scaled up

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GATEWAY_TEXTURE);

		//Quad dimensions
		final float squareOut = 1;
		final float squareIn = 3F / 5F;//At size = 5, width is 1
		final float sqDepth = 0.5F;
		final float octInLen = squareIn * (float) Math.cos(Math.PI / 8);
		final float octInWid = squareIn * (float) Math.sqrt(2D - Math.sqrt(2D)) / 2F;
		final float triLen = 1F / 5F;//Radius of the top of the triangle. At size 5, length = 1
		final float triDepth = (1F / 40F) * radius + sqDepth;
		final float iconEdgeRad = 5F / 64F;
		final float zFightingOffset = 0.003F;//Small offset used to prevent z-fighting
		final float iconDialBottom = squareIn * ((float) Math.sqrt(2) + 0.25F);
		final float iconDialTop = iconDialBottom + 2F * iconEdgeRad;

		//Texture UV coords
		//Symbols are the 8 alignment icons. They are arranged in a column on the texture
		final float symbolUSt = sprite.getU0();
		final float symbolUEn = sprite.getU(2);
		//Poly used for square and octagon
		final float polyUSt = symbolUEn;
		final float polyUEn = sprite.getU(12);
		final float sqFrUSt = sprite.getU(4);
		final float sqFrUEn = sprite.getU(10);
		final float sqFrVSt = sprite.getV0();
		final float sqFrVEn = sprite.getV(2);
		final float sqInVSt = sqFrVEn;
		final float sqInVEn = sprite.getV(4);
		final float sqOutVSt = sqInVEn;
		final float sqOutVEn = sprite.getV(6);
		final float portalUSt = sprite.getU(12);
		final float portalUEn = sprite.getU1();
		final float portalTexRad = .051F * (sprite.getU1() - sprite.getU0());//half the side length of the regular octagon
		final float portalUMid1 = (portalUSt + portalUEn) / 2F - portalTexRad;
		final float portalUMid2 = portalUMid1 + 2F * portalTexRad;
		final float portalVSt = sprite.getV(4 * ((int) (frame.getLevel().getGameTime() / 5L) % 4));
		final float portalVEn = portalVSt + (sprite.getV1() - sprite.getV0()) * 0.25F;
		final float portalVMid1 = (portalVSt + portalVEn) / 2F - portalTexRad;
		final float portalVMid2 = portalVMid1 + 2F * portalTexRad;

		//We abuse rotations to draw one segment rotated several times instead of hard-defining every vertex
		//Because I can't be bothered to math out all 32 distinct vertex positions on an octagon ring

		//The outer edge of the render is aligned with the outside of the block frame. The inside is not aligned with anything
		VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
		
		//Fixed square ring
		matrix.pushPose();
		Quaternion ringRotation = Vector3f.ZP.rotationDegrees(90);
		for(int i = 0; i < 4; i++){
			//Front
			CRRenderUtil.addVertexBlock(builder, matrix, squareOut, squareOut, sqDepth, polyUEn, sqFrVSt, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareOut, squareOut, sqDepth, polyUSt, sqFrVSt, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareIn, squareIn, sqDepth, sqFrUSt, sqFrVEn, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, squareIn, squareIn, sqDepth, sqFrUEn, sqFrVEn, 0, 0, 1, combinedLight);

			//Other front
			CRRenderUtil.addVertexBlock(builder, matrix, squareOut, squareOut, -sqDepth, polyUEn, sqFrVSt, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, squareIn, squareIn, -sqDepth, sqFrUEn, sqFrVEn, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareIn, squareIn, -sqDepth, sqFrUSt, sqFrVEn, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareOut, squareOut, -sqDepth, polyUSt, sqFrVSt, 0, 0, -1, combinedLight);

			//Outer edge
			CRRenderUtil.addVertexBlock(builder, matrix, squareOut, squareOut, -sqDepth, polyUEn, sqOutVSt, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareOut, squareOut, -sqDepth, polyUSt, sqOutVSt, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareOut, squareOut, sqDepth, polyUSt, sqOutVEn, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, squareOut, squareOut, sqDepth, polyUEn, sqOutVEn, 0, 1, 0, combinedLight);

			//Inner edge
			CRRenderUtil.addVertexBlock(builder, matrix, squareIn, squareIn, -sqDepth, polyUEn, sqInVSt, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, squareIn, squareIn, sqDepth, polyUEn, sqInVEn, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareIn, squareIn, sqDepth, polyUSt, sqInVEn, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -squareIn, squareIn, -sqDepth, polyUSt, sqInVSt, 0, 1, 0, combinedLight);

			matrix.mulPose(ringRotation);
		}
		matrix.popPose();

		//Dialed icons
		if(frame.chevrons[0] != null){
			//Front
			matrix.pushPose();
			matrix.mulPose(Vector3f.ZP.rotationDegrees(45));

			//Draw each symbol individually
			Quaternion chevronRotation = Vector3f.ZP.rotationDegrees(-90);
			for(int i = 0; i < frame.chevrons.length; i++){
				if(frame.chevrons[i] == null){
					break;
				}
				int entryId = GatewayAddress.getEntryID(frame.chevrons[i]);
				CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconDialTop, sqDepth + zFightingOffset, symbolUSt, getIconVSt(sprite, entryId), 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT);
				CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconDialBottom, sqDepth + zFightingOffset, symbolUSt, getIconVEn(sprite, entryId), 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT);
				CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconDialBottom, sqDepth + zFightingOffset, symbolUEn, getIconVEn(sprite, entryId), 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT);
				CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconDialTop, sqDepth + zFightingOffset, symbolUEn, getIconVSt(sprite, entryId), 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT);

				matrix.mulPose(chevronRotation);
			}

			matrix.popPose();

			//Other front
			matrix.pushPose();
			matrix.mulPose(Vector3f.ZP.rotationDegrees(-45));

			//Draw each symbol individually
			for(int i = 0; i < frame.chevrons.length; i++){
				if(frame.chevrons[i] == null){
					break;
				}

				int entryId = GatewayAddress.getEntryID(frame.chevrons[i]);
				CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconDialTop, -sqDepth - zFightingOffset, symbolUEn, getIconVSt(sprite, entryId), 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT);
				CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconDialTop, -sqDepth - zFightingOffset, symbolUSt, getIconVSt(sprite, entryId), 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT);
				CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconDialBottom, -sqDepth - zFightingOffset, symbolUSt, getIconVEn(sprite, entryId), 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT);
				CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconDialBottom, -sqDepth - zFightingOffset, symbolUEn, getIconVEn(sprite, entryId), 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT);

				matrix.mulPose(ringRotation);
			}

			matrix.popPose();
		}

		//Portal, rendered with translucent type
		if(linked){

			//Render a double sided octagonal portal
			//Bright lighting, translucent
			//Has to be done via 3 quadrilaterals for the octagon, all done twice for both sides
			int[] col = {255, 255, 255, 200};

			//Switch builder to translucent
			builder = buffer.getBuffer(RenderType.translucentNoCrumbling());
			
			//Vertices are commented with the number of the vertex on the final octagon

			//Front
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, -octInLen, 0, portalUMid2, portalVEn, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//1
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, -octInLen, 0, portalUMid1, portalVEn, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//2
			CRRenderUtil.addVertexBlock(builder, matrix, -octInLen, -octInWid, 0, portalUSt, portalVMid2, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//3
			CRRenderUtil.addVertexBlock(builder, matrix, -octInLen, octInWid, 0, portalUSt, portalVMid1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//4

			CRRenderUtil.addVertexBlock(builder, matrix, -octInLen, octInWid, 0, portalUSt, portalVMid1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//4
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, 0, portalUMid1, portalVSt, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//5
			CRRenderUtil.addVertexBlock(builder, matrix, octInLen, -octInWid, 0, portalUEn, portalVMid2, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//8
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, -octInLen, 0, portalUMid2, portalVEn, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//1

			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, 0, portalUMid1, portalVSt, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//5
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, octInLen, 0, portalUMid2, portalVSt, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//6
			CRRenderUtil.addVertexBlock(builder, matrix, octInLen, octInWid, 0, portalUEn, portalVMid1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//7
			CRRenderUtil.addVertexBlock(builder, matrix, octInLen, -octInWid, 0, portalUEn, portalVMid2, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//8

			//Other front
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, -octInLen, 0, portalUMid2, portalVEn, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//1
			CRRenderUtil.addVertexBlock(builder, matrix, -octInLen, octInWid, 0, portalUSt, portalVMid1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//4
			CRRenderUtil.addVertexBlock(builder, matrix, -octInLen, -octInWid, 0, portalUSt, portalVMid2, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//3
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, -octInLen, 0, portalUMid1, portalVEn, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//2

			CRRenderUtil.addVertexBlock(builder, matrix, -octInLen, octInWid, 0, portalUSt, portalVMid1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//4
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, -octInLen, 0, portalUMid2, portalVEn, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//1
			CRRenderUtil.addVertexBlock(builder, matrix, octInLen, -octInWid, 0, portalUEn, portalVMid2, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//8
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, 0, portalUMid1, portalVSt, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//5

			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, 0, portalUMid1, portalVSt, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//5
			CRRenderUtil.addVertexBlock(builder, matrix, octInLen, -octInWid, 0, portalUEn, portalVMid2, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//8
			CRRenderUtil.addVertexBlock(builder, matrix, octInLen, octInWid, 0, portalUEn, portalVMid1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//7
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, octInLen, 0, portalUMid2, portalVSt, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);//6
		}
	}

	private float getIconVSt(TextureAtlasSprite sprite, int index){
		return sprite.getV(index * 2);
	}

	private float getIconVEn(TextureAtlasSprite sprite, int index){
		return sprite.getV((index + 1) * 2);
	}

	@Override
	public boolean shouldRenderOffScreen(GatewayControllerDestinationTileEntity te){
		return te.isActive();
	}

	@Override
	public int getViewDistance(){
		return 256;//Same value as beacon
	}
}
