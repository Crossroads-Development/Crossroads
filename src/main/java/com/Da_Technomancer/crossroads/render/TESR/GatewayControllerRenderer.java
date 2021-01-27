package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayControllerTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class GatewayControllerRenderer extends EntropyRenderer<GatewayControllerTileEntity>{

	protected GatewayControllerRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(GatewayControllerTileEntity frame, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(!frame.isActive()){
			//Only do rendering if this is the top-center block of a formed multiblock (the core renders the entire gateway)
			return;
		}
		super.render(frame, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		float radius = frame.getSize() / 2F;
		Direction.Axis plane = frame.getPlane();
		boolean linked = frame.chevrons[frame.chevrons.length - 1] != null;//Whether this gateway is active and linked to another
		float dialingWheelAngle = (float) frame.getAngle(partialTicks);

		//Define lighting based on the interior of the frame, as the frame itself is solid blocks
		combinedLight = CRRenderUtil.getLightAtPos(frame.getWorld(), frame.getPos().down(frame.getSize() / 5));

		//Render everything about the center of the multiblock, so we can rotate
		matrix.translate(0.5D, 1 - radius, 0.5D);

		//Rotate to align with the frame if applicable
		if(plane == Direction.Axis.Z){
			matrix.rotate(Vector3f.YP.rotationDegrees(90));
		}

		//From this point, the frame is in the X-Y rendering plane
		matrix.scale(radius, radius, 1F);

		//From this point, everything should be rendered within [-1, 1] on the x-y; it will be scaled up

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GATEWAY_TEXTURE);

		//Quad dimensions
		final float squareOut = 1;
		final float squareIn = 3F / 5F;//At size = 5, width is 1
		final float sqDepth = 0.5F;
		final float octOutLen = squareOut * (float) Math.cos(Math.PI / 8);//Length of outer edge of octagon to align with outer square edge when rotated
		final float octInLen = squareIn * (float) Math.cos(Math.PI / 8);
		final float octOutWid = squareOut * (float) Math.sqrt(2D - Math.sqrt(2D)) / 2F;//Standard formula that relates diameter and side length of regular octagon
		final float octInWid = squareIn * (float) Math.sqrt(2D - Math.sqrt(2D)) / 2F;
		final float octDepth = 0.4F;
		final float triLen = 1F / 5F;//Radius of the top of the triangle. At size 5, length = 1
		final float triDepth = (1F / 40F) * radius + sqDepth;
		final float iconEdgeRad = 5F / 64F;
		final float iconBottom = octInLen + 1F / 32F;
		final float iconTop = iconBottom + 2F * iconEdgeRad;
		final float iconEdgeZRad = octDepth;//When icons are rendered in the x-z plane, this is used for z to keep the icon square with uneven scaling
		final float iconEdgeXRad = iconEdgeZRad / radius;//When icons are rendered in the x-z plane, this is used for x to keep the icon square with uneven scaling
		final float zFightingOffset = 0.003F;//Small offset used to prevent z-fighting
		final float iconDialBottom = squareIn * ((float) Math.sqrt(2) + 0.25F);
		final float iconDialTop = iconDialBottom + 2F * iconEdgeRad;

		//Texture UV coords
		//Symbols are the 8 alignment icons. They are arranged in a column on the texture
		final float symbolUSt = sprite.getMinU();
		final float symbolUEn = sprite.getInterpolatedU(2);
		//Poly used for square and octagon
		final float polyUSt = symbolUEn;
		final float polyUEn = sprite.getInterpolatedU(12);
		final float sqFrUSt = sprite.getInterpolatedU(4);
		final float sqFrUEn = sprite.getInterpolatedU(10);
		final float sqFrVSt = sprite.getMinV();
		final float sqFrVEn = sprite.getInterpolatedV(2);
		final float sqInVSt = sqFrVEn;
		final float sqInVEn = sprite.getInterpolatedV(4);
		final float sqOutVSt = sqInVEn;
		final float sqOutVEn = sprite.getInterpolatedV(6);
		final float octFrUSt = sprite.getInterpolatedU(3);
		final float octFrUEn = sprite.getInterpolatedU(11);
		final float octFrVSt = sqOutVEn;
		final float octFrVEn = sprite.getInterpolatedV(8);
		final float octInVSt = octFrVEn;
		final float octInVEn = sprite.getInterpolatedV(10);
		final float octOutVSt = octInVEn;
		final float octOutVEn = sprite.getInterpolatedV(12);
		final float triVSt = octOutVEn;
		final float triVEn = sprite.getMaxV();
		final float triFrUEn = sprite.getInterpolatedU(6);
		final float triTopUSt = triFrUEn;
		final float triTopUEn = sprite.getInterpolatedU(13F / 2F);//Free me from texture mapping purgatory
		final float triFrUMid = (polyUSt + triTopUSt) / 2F;
		final float triEdgeUSt = triTopUEn;
		final float triEdgeUEn = sprite.getInterpolatedU(7);
		final float portalUSt = sprite.getInterpolatedU(12);
		final float portalUEn = sprite.getMaxU();
		final float portalTexRad = .051F * (sprite.getMaxU() - sprite.getMinU());//half the side length of the regular octagon
		final float portalUMid1 = (portalUSt + portalUEn) / 2F - portalTexRad;
		final float portalUMid2 = portalUMid1 + 2F * portalTexRad;
		final float portalVSt = sprite.getInterpolatedV(4 * ((int) (frame.getWorld().getGameTime() / 5L) % 4));
		final float portalVEn = portalVSt + (sprite.getMaxV() - sprite.getMinV()) * 0.25F;
		final float portalVMid1 = (portalVSt + portalVEn) / 2F - portalTexRad;
		final float portalVMid2 = portalVMid1 + 2F * portalTexRad;

		//We abuse rotations to draw one segment rotated several times instead of hard-defining every vertex
		//Because I can't be bothered to math out all 32 distinct vertex positions on an octagon ring

		//The outer edge of the render is aligned with the outside of the block frame. The inside is not aligned with anything
		IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());
		
		//Fixed square ring
		matrix.push();
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

			matrix.rotate(ringRotation);
		}
		matrix.pop();

		//Triangular selector
		//Triangles rendered by duplicating a vertex on the quad

		//Front
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, triDepth, polyUSt, triVEn, 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, triDepth, polyUSt, triVEn, 0, 0, 1, combinedLight);//duplicate
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, triDepth, triFrUEn, triVEn, 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, triDepth, triFrUMid, triVSt, 0, 0, 1, combinedLight);

		//Other front
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, -triDepth, polyUSt, triVEn, 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, -triDepth, polyUSt, triVEn, 0, 0, -1, combinedLight);//duplicate
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, -triDepth, triFrUMid, triVSt, 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, -triDepth, triFrUEn, triVEn, 0, 0, -1, combinedLight);

		//Top
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, triDepth, triTopUSt, triVSt, 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, sqDepth, triTopUEn, triVSt, 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, sqDepth, triTopUEn, triVEn, 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, triDepth, triTopUSt, triVEn, 0, 1, 0, combinedLight);

		//Other top
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, -triDepth, triTopUSt, triVSt, 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, -triDepth, triTopUSt, triVEn, 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, -sqDepth, triTopUEn, triVEn, 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, -sqDepth, triTopUEn, triVSt, 0, 1, 0, combinedLight);


		//Triangular sides have non-trivial normals
		Vector3d normalA = CRRenderUtil.findNormal(new Vector3d(triLen, squareOut, triDepth), new Vector3d(0, squareIn, triDepth), new Vector3d(triLen, squareOut, sqDepth));
		Vector3d normalB = CRRenderUtil.findNormal(new Vector3d(-triLen, squareOut, triDepth), new Vector3d(-triLen, squareOut, sqDepth), new Vector3d(0, squareIn, triDepth));

		//Side
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, triDepth, triEdgeUSt, triVSt, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, triDepth, triEdgeUSt, triVEn, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, sqDepth, triEdgeUEn, triVEn, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, sqDepth, triEdgeUEn, triVSt, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);

		//Side
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, triDepth, triEdgeUSt, triVSt, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, sqDepth, triEdgeUEn, triVSt, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, sqDepth, triEdgeUEn, triVEn, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, triDepth, triEdgeUSt, triVEn, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);

		//Side
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, -triDepth, triEdgeUSt, triVSt, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, triLen, squareOut, -sqDepth, triEdgeUEn, triVSt, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, -sqDepth, triEdgeUEn, triVEn, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, -triDepth, triEdgeUSt, triVEn, (float) normalA.x, (float) normalA.y, (float) normalA.z, combinedLight);

		//Side
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, -triDepth, triEdgeUSt, triVSt, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, -triDepth, triEdgeUSt, triVEn, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, 0, squareIn, -sqDepth, triEdgeUEn, triVEn, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -triLen, squareOut, -sqDepth, triEdgeUEn, triVSt, (float) normalB.x, (float) normalB.y, (float) normalB.z, combinedLight);

		//Dialed icons
		if(frame.chevrons[0] != null){
			//Front
			matrix.push();
			matrix.rotate(Vector3f.ZP.rotationDegrees(45));

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

				matrix.rotate(chevronRotation);
			}

			matrix.pop();

			//Other front
			matrix.push();
			matrix.rotate(Vector3f.ZP.rotationDegrees(-45));

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

				matrix.rotate(ringRotation);
			}

			matrix.pop();
		}
		
		//Rotating octagonal ring
		matrix.rotate(Vector3f.ZP.rotation(dialingWheelAngle));

		Quaternion segmentRotation = Vector3f.ZP.rotationDegrees(-45);

		for(int i = 0; i < 8; i++){

			//Front
			CRRenderUtil.addVertexBlock(builder, matrix, octOutWid, octOutLen, octDepth, polyUEn, octFrVSt, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octOutWid, octOutLen, octDepth, polyUSt, octFrVSt, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, octDepth, octFrUSt, octFrVEn, 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, octInLen, octDepth, octFrUEn, octFrVEn, 0, 0, 1, combinedLight);

			//Other front (in some bizarre cultures this is called the 'back'. However, I speak American)
			CRRenderUtil.addVertexBlock(builder, matrix, octOutWid, octOutLen, -octDepth, polyUEn, octFrVSt, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, octInLen, -octDepth, octFrUEn, octFrVEn, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, -octDepth, octFrUSt, octFrVEn, 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octOutWid, octOutLen, -octDepth, polyUSt, octFrVSt, 0, 0, -1, combinedLight);

			//Outer edge
			CRRenderUtil.addVertexBlock(builder, matrix, octOutWid, octOutLen, -octDepth, polyUEn, octOutVEn, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octOutWid, octOutLen, -octDepth, polyUSt, octOutVEn, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octOutWid, octOutLen, octDepth, polyUSt, octOutVSt, 0, 1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, octOutWid, octOutLen, octDepth, polyUEn, octOutVSt, 0, 1, 0, combinedLight);

			//Inner edge
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, octInLen, -octDepth, polyUEn, octInVSt, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, octInWid, octInLen, octDepth, polyUEn, octInVEn, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, octDepth, polyUSt, octInVEn, 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -octInWid, octInLen, -octDepth, polyUSt, octInVSt, 0, -1, 0, combinedLight);

			//Inside icons
			CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeXRad, octInLen - zFightingOffset, -iconEdgeZRad, symbolUEn, getIconVSt(sprite, i), 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeXRad, octInLen - zFightingOffset, -iconEdgeZRad, symbolUSt, getIconVSt(sprite, i), 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeXRad, octInLen - zFightingOffset, iconEdgeZRad, symbolUSt, getIconVEn(sprite, i), 0, -1, 0, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeXRad, octInLen - zFightingOffset, iconEdgeZRad, symbolUEn, getIconVEn(sprite, i), 0, -1, 0, combinedLight);

			//Front icons
			CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconTop, octDepth + zFightingOffset, symbolUEn, getIconVSt(sprite, i), 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconTop, octDepth + zFightingOffset, symbolUSt, getIconVSt(sprite, i), 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconBottom, octDepth + zFightingOffset, symbolUSt, getIconVEn(sprite, i), 0, 0, 1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconBottom, octDepth + zFightingOffset, symbolUEn, getIconVEn(sprite, i), 0, 0, 1, combinedLight);

			//Other front icons
			CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconTop, -octDepth - zFightingOffset, symbolUSt, getIconVSt(sprite, i), 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, iconEdgeRad, iconBottom, -octDepth - zFightingOffset, symbolUSt, getIconVEn(sprite, i), 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconBottom, -octDepth - zFightingOffset, symbolUEn, getIconVEn(sprite, i), 0, 0, -1, combinedLight);
			CRRenderUtil.addVertexBlock(builder, matrix, -iconEdgeRad, iconTop, -octDepth - zFightingOffset, symbolUEn, getIconVSt(sprite, i), 0, 0, -1, combinedLight);

			matrix.rotate(segmentRotation);
		}

		//Portal, rendered with translucent type
		if(linked){

			//Render a double sided octagonal portal
			//Bright lighting, translucent
			//Has to be done via 3 quadrilaterals for the octagon, all done twice for both sides
			int[] col = {255, 255, 255, 200};

			//Switch builder to translucent
			builder = buffer.getBuffer(RenderType.getTranslucentNoCrumbling());
			
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
		return sprite.getInterpolatedV(index * 2);
	}

	private float getIconVEn(TextureAtlasSprite sprite, int index){
		return sprite.getInterpolatedV((index + 1) * 2);
	}

	@Override
	public boolean isGlobalRenderer(GatewayControllerTileEntity te){
		return te.isActive();
	}
}
