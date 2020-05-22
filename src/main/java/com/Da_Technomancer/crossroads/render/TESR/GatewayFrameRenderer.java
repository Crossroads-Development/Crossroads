package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GatewayFrameRenderer extends LinkLineRenderer<GatewayFrameTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/gateway.png");

	@Override
	public void render(GatewayFrameTileEntity frame, double x, double y, double z, float partialTicks, int destroyStage){
		if(frame == null || !frame.getWorld().isBlockLoaded(frame.getPos()) || !frame.isActive()){
			//Only do rendering if this is the top-center block of a formed multiblock (the core renders the entire gateway)
			return;
		}
		super.render(frame, x, y, z, partialTicks, destroyStage);

		double radius = frame.getSize() / 2D;
		Direction.Axis plane = frame.getPlane();
		boolean linked = frame.chevrons[frame.chevrons.length - 1] != null;//Whether this gateway is active and linked to another
		double dialingWheelAngle = frame.getAngle(partialTicks);

		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();//Needed for lighting to work when rotating

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		//Define lighting based on the interior of the frame, as the frame itself is solid blocks
		CRRenderUtil.setLighting(frame.getWorld().getCombinedLight(frame.getPos().down(frame.getSize() / 5), 0));

		//Render everything about the center of the multiblock, so we can rotate
		GlStateManager.translated(x + 0.5, y + 1D - radius, z + 0.5);

		//Rotate to align with the frame if applicable
		if(plane == Direction.Axis.Z){
			GlStateManager.rotated(90, 0, 1, 0);
		}
		//From this point, the frame is in the X-Y rendering plane

		GlStateManager.scaled(radius, radius, 1D);

		//From this point, everything should be rendered within [-1, 1] on the x-y; it will be scaled up

		//The outer edge of the render is aligned with the outside of the block frame. The inside is not aligned with anything
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();

		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		//Quad dimensions
		final double squareOut = 1;
		final double squareIn = 3D / 5D;//At size = 5, width is 1
		final double sqDepth = 0.5D;
		final double octOutLen = squareOut * Math.cos(Math.PI / 8);//Length of outer edge of octagon to align with outer square edge when rotated
		final double octInLen = squareIn * Math.cos(Math.PI / 8);
		final double octOutWid = squareOut * Math.sqrt(2D - Math.sqrt(2D)) / 2D;//Standard formula that relates diameter and side length of regular octagon
		final double octInWid = squareIn * Math.sqrt(2D - Math.sqrt(2D)) / 2D;
		final double octDepth = 0.4D;
		final double triLen = 1D / 5D;//Radius of the top of the triangle. At size 5, length = 1
		final double triDepth = (1D / 40D) * radius + sqDepth;
		final double iconEdgeRad = 5D / 64D;
		final double iconBottom = octInLen + 1D / 32D;
		final double iconTop = iconBottom + 2D * iconEdgeRad;
		final double iconEdgeZRad = octDepth;//When icons are rendered in the x-z plane, this is used for z to keep the icon square with uneven scaling
		final double iconEdgeXRad = iconEdgeZRad / radius;//When icons are rendered in the x-z plane, this is used for x to keep the icon square with uneven scaling
		final double zFightingOffset = 0.003D;//Small offset used to prevent z-fighting
		final double iconDialBottom = squareIn * (Math.sqrt(2) + 0.25D);
		final double iconDialTop = iconDialBottom + 2D * iconEdgeRad;

		//Texture UV coords
		//Symbols are the 8 alignment icons. They are arranged in a column on the texture
		final float symbolUSt = 0;
		final float symbolUEn = 2F / 16F;
		//Poly used for square and octagon
		final float polyUSt = symbolUEn;
		final float polyUEn = 12F / 16F;
		final float sqFrUSt = 4F / 16F;
		final float sqFrUEn = 10F / 16F;
		final float sqFrVSt = 0;
		final float sqFrVEn = 1F / 8F;
		final float sqInVSt = sqFrVEn;
		final float sqInVEn = 2F / 8F;
		final float sqOutVSt = sqInVEn;
		final float sqOutVEn = 3F / 8F;
		final float octFrUSt = 3F / 16F;
		final float octFrUEn = 11F / 16F;
		final float octFrVSt = sqOutVEn;
		final float octFrVEn = 4F / 8F;
		final float octInVSt = octFrVEn;
		final float octInVEn = 5F / 8F;
		final float octOutVSt = octInVEn;
		final float octOutVEn = 6F / 8F;
		final float triVSt = octOutVEn;
		final float triVEn = 1F;
		final float triFrUEn = 3F / 8F;
		final float triTopUSt = triFrUEn;
		final float triTopUEn = 13F / 32F;//Free me from texture mapping purgatory
		final float triFrUMid = (polyUSt + triTopUSt) / 2F;
		final float triEdgeUSt = triTopUEn;
		final float triEdgeUEn = 7F / 16F;
		final float portalUSt = 3F / 4F;
		final float portalUEn = 1F;
		final float portalTexRad = .051F;//half the side length of the regular octagon
		final float portalUMid1 = (portalUSt + portalUEn) / 2F - portalTexRad;
		final float portalUMid2 = portalUMid1 + 2F * portalTexRad;
		final float portalVSt = 0.25F * ((int) (frame.getWorld().getGameTime() / 5L) % 4);
		final float portalVEn = portalVSt + 0.25F;
		final float portalVMid1 = (portalVSt + portalVEn) / 2F - portalTexRad;
		final float portalVMid2 = portalVMid1 + 2F * portalTexRad;

		//We abuse rotations to draw one segment rotated several times instead of hard-defining every vertex
		//Because I can't be bothered to math out all 32 distinct vertex positions on an octagon ring

		//Rotating octagonal ring
		GlStateManager.pushMatrix();
		GlStateManager.rotated(Math.toDegrees(dialingWheelAngle), 0, 0, 1);

		for(int i = 0; i < 8; i++){
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			//Front
			vb.pos(octOutWid, octOutLen, octDepth).tex(polyUEn, octFrVSt).endVertex();
			vb.pos(-octOutWid, octOutLen, octDepth).tex(polyUSt, octFrVSt).endVertex();
			vb.pos(-octInWid, octInLen, octDepth).tex(octFrUSt, octFrVEn).endVertex();
			vb.pos(octInWid, octInLen, octDepth).tex(octFrUEn, octFrVEn).endVertex();

			//Other front (in some bizarre cultures this is called the 'back'. However, I speak American)
			vb.pos(octOutWid, octOutLen, -octDepth).tex(polyUEn, octFrVSt).endVertex();
			vb.pos(octInWid, octInLen, -octDepth).tex(octFrUEn, octFrVEn).endVertex();
			vb.pos(-octInWid, octInLen, -octDepth).tex(octFrUSt, octFrVEn).endVertex();
			vb.pos(-octOutWid, octOutLen, -octDepth).tex(polyUSt, octFrVSt).endVertex();

			//Outer edge
			vb.pos(octOutWid, octOutLen, -octDepth).tex(polyUEn, octOutVEn).endVertex();
			vb.pos(-octOutWid, octOutLen, -octDepth).tex(polyUSt, octOutVEn).endVertex();
			vb.pos(-octOutWid, octOutLen, octDepth).tex(polyUSt, octOutVSt).endVertex();
			vb.pos(octOutWid, octOutLen, octDepth).tex(polyUEn, octOutVSt).endVertex();

			//Inner edge
			vb.pos(octInWid, octInLen, -octDepth).tex(polyUEn, octInVSt).endVertex();
			vb.pos(octInWid, octInLen, octDepth).tex(polyUEn, octInVEn).endVertex();
			vb.pos(-octInWid, octInLen, octDepth).tex(polyUSt, octInVEn).endVertex();
			vb.pos(-octInWid, octInLen, -octDepth).tex(polyUSt, octInVSt).endVertex();

			//Inside icons
			vb.pos(-iconEdgeXRad, octInLen - zFightingOffset, -iconEdgeZRad).tex(symbolUEn, getIconVSt(i)).endVertex();
			vb.pos(iconEdgeXRad, octInLen - zFightingOffset, -iconEdgeZRad).tex(symbolUSt, getIconVSt(i)).endVertex();
			vb.pos(iconEdgeXRad, octInLen - zFightingOffset, iconEdgeZRad).tex(symbolUSt, getIconVEn(i)).endVertex();
			vb.pos(-iconEdgeXRad, octInLen - zFightingOffset, iconEdgeZRad).tex(symbolUEn, getIconVEn(i)).endVertex();

			//Front icons
			vb.pos(iconEdgeRad, iconTop, octDepth + zFightingOffset).tex(symbolUEn, getIconVSt(i)).endVertex();
			vb.pos(-iconEdgeRad, iconTop, octDepth + zFightingOffset).tex(symbolUSt, getIconVSt(i)).endVertex();
			vb.pos(-iconEdgeRad, iconBottom, octDepth + zFightingOffset).tex(symbolUSt, getIconVEn(i)).endVertex();
			vb.pos(iconEdgeRad, iconBottom, octDepth + zFightingOffset).tex(symbolUEn, getIconVEn(i)).endVertex();

			//Other front icons
			vb.pos(iconEdgeRad, iconTop, -octDepth - zFightingOffset).tex(symbolUSt, getIconVSt(i)).endVertex();
			vb.pos(iconEdgeRad, iconBottom, -octDepth - zFightingOffset).tex(symbolUSt, getIconVEn(i)).endVertex();
			vb.pos(-iconEdgeRad, iconBottom, -octDepth - zFightingOffset).tex(symbolUEn, getIconVEn(i)).endVertex();
			vb.pos(-iconEdgeRad, iconTop, -octDepth - zFightingOffset).tex(symbolUEn, getIconVSt(i)).endVertex();

			tess.draw();

			GlStateManager.rotated(-45, 0, 0, 1);
		}

		//Portal
		if(linked){
			//Render double sided
			GlStateManager.disableCull();
			//Glow in the dark
			int light = CRRenderUtil.getCurrLighting();
			CRRenderUtil.setBrightLighting();
			GlStateManager.color4f(1, 1, 1, 0.8F);

			vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);

			//Octagon filling the octagonal frame
			vb.pos(octInWid, octInLen, 0).tex(portalUMid2, portalVEn).endVertex();
			vb.pos(-octInWid, octInLen, 0).tex(portalUMid1, portalVEn).endVertex();
			vb.pos(-octInLen, octInWid, 0).tex(portalUSt, portalVMid2).endVertex();
			vb.pos(-octInLen, -octInWid, 0).tex(portalUSt, portalVMid1).endVertex();
			vb.pos(-octInWid, -octInLen, 0).tex(portalUMid1, portalVSt).endVertex();
			vb.pos(octInWid, -octInLen, 0).tex(portalUMid2, portalVSt).endVertex();
			vb.pos(octInLen, -octInWid, 0).tex(portalUEn, portalVMid1).endVertex();
			vb.pos(octInLen, octInWid, 0).tex(portalUEn, portalVMid2).endVertex();

			tess.draw();

			GlStateManager.color4f(1, 1, 1, 1);
			CRRenderUtil.setLighting(light);
			GlStateManager.enableCull();
		}

		GlStateManager.popMatrix();

		//Fixed square ring
		for(int i = 0; i < 4; i++){
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			//Front
			vb.pos(squareOut, squareOut, sqDepth).tex(polyUEn, sqFrVSt).endVertex();
			vb.pos(-squareOut, squareOut, sqDepth).tex(polyUSt, sqFrVSt).endVertex();
			vb.pos(-squareIn, squareIn, sqDepth).tex(sqFrUSt, sqFrVEn).endVertex();
			vb.pos(squareIn, squareIn, sqDepth).tex(sqFrUEn, sqFrVEn).endVertex();

			//Other front
			vb.pos(squareOut, squareOut, -sqDepth).tex(polyUEn, sqFrVSt).endVertex();
			vb.pos(squareIn, squareIn, -sqDepth).tex(sqFrUEn, sqFrVEn).endVertex();
			vb.pos(-squareIn, squareIn, -sqDepth).tex(sqFrUSt, sqFrVEn).endVertex();
			vb.pos(-squareOut, squareOut, -sqDepth).tex(polyUSt, sqFrVSt).endVertex();

			//Outer edge
			vb.pos(squareOut, squareOut, -sqDepth).tex(polyUEn, sqOutVSt).endVertex();
			vb.pos(-squareOut, squareOut, -sqDepth).tex(polyUSt, sqOutVSt).endVertex();
			vb.pos(-squareOut, squareOut, sqDepth).tex(polyUSt, sqOutVEn).endVertex();
			vb.pos(squareOut, squareOut, sqDepth).tex(polyUEn, sqOutVEn).endVertex();

			//Inner edge
			vb.pos(squareIn, squareIn, -sqDepth).tex(polyUEn, sqInVSt).endVertex();
			vb.pos(squareIn, squareIn, sqDepth).tex(polyUEn, sqInVEn).endVertex();
			vb.pos(-squareIn, squareIn, sqDepth).tex(polyUSt, sqInVEn).endVertex();
			vb.pos(-squareIn, squareIn, -sqDepth).tex(polyUSt, sqInVSt).endVertex();

			tess.draw();

			GlStateManager.rotated(90, 0, 0, 1);
		}

		//Triangular selector

		vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

		//Front
		vb.pos(triLen, squareOut, triDepth).tex(polyUSt, triVEn).endVertex();
		vb.pos(-triLen, squareOut, triDepth).tex(triFrUEn, triVEn).endVertex();
		vb.pos(0, squareIn, triDepth).tex(triFrUMid, triVSt).endVertex();

		//Other front
		vb.pos(triLen, squareOut, -triDepth).tex(polyUSt, triVEn).endVertex();
		vb.pos(0, squareIn, -triDepth).tex(triFrUMid, triVSt).endVertex();
		vb.pos(-triLen, squareOut, -triDepth).tex(triFrUEn, triVEn).endVertex();

		tess.draw();


		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		//Top
		vb.pos(triLen, squareOut, triDepth).tex(triTopUSt, triVSt).endVertex();
		vb.pos(triLen, squareOut, sqDepth).tex(triTopUEn, triVSt).endVertex();
		vb.pos(-triLen, squareOut, sqDepth).tex(triTopUEn, triVEn).endVertex();
		vb.pos(-triLen, squareOut, triDepth).tex(triTopUSt, triVEn).endVertex();

		//Other top
		vb.pos(triLen, squareOut, -triDepth).tex(triTopUSt, triVSt).endVertex();
		vb.pos(-triLen, squareOut, -triDepth).tex(triTopUSt, triVEn).endVertex();
		vb.pos(-triLen, squareOut, -sqDepth).tex(triTopUEn, triVEn).endVertex();
		vb.pos(triLen, squareOut, -sqDepth).tex(triTopUEn, triVSt).endVertex();

		//Side
		vb.pos(triLen, squareOut, triDepth).tex(triEdgeUSt, triVSt).endVertex();
		vb.pos(0, squareIn, triDepth).tex(triEdgeUSt, triVEn).endVertex();
		vb.pos(0, squareIn, sqDepth).tex(triEdgeUEn, triVEn).endVertex();
		vb.pos(triLen, squareOut, sqDepth).tex(triEdgeUEn, triVSt).endVertex();

		//Side
		vb.pos(-triLen, squareOut, triDepth).tex(triEdgeUSt, triVSt).endVertex();
		vb.pos(-triLen, squareOut, sqDepth).tex(triEdgeUEn, triVSt).endVertex();
		vb.pos(0, squareIn, sqDepth).tex(triEdgeUEn, triVEn).endVertex();
		vb.pos(0, squareIn, triDepth).tex(triEdgeUSt, triVEn).endVertex();

		//Side
		vb.pos(triLen, squareOut, -triDepth).tex(triEdgeUSt, triVSt).endVertex();
		vb.pos(triLen, squareOut, -sqDepth).tex(triEdgeUEn, triVSt).endVertex();
		vb.pos(0, squareIn, -sqDepth).tex(triEdgeUEn, triVEn).endVertex();
		vb.pos(0, squareIn, -triDepth).tex(triEdgeUSt, triVEn).endVertex();

		//Side
		vb.pos(-triLen, squareOut, -triDepth).tex(triEdgeUSt, triVSt).endVertex();
		vb.pos(0, squareIn, -triDepth).tex(triEdgeUSt, triVEn).endVertex();
		vb.pos(0, squareIn, -sqDepth).tex(triEdgeUEn, triVEn).endVertex();
		vb.pos(-triLen, squareOut, -sqDepth).tex(triEdgeUEn, triVSt).endVertex();

		tess.draw();

		//Dialed icons
		if(frame.chevrons[0] != null){
			CRRenderUtil.setBrightLighting();//Glow in the dark

			//Front
			GlStateManager.pushMatrix();
			GlStateManager.rotated(45, 0, 0, 1);

			//Draw each symbol individually
			for(int i = 0; i < frame.chevrons.length; i++){
				if(frame.chevrons[i] == null){
					break;
				}
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vb.pos(-iconEdgeRad, iconDialTop, sqDepth + zFightingOffset).tex(symbolUSt, getIconVSt(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				vb.pos(-iconEdgeRad, iconDialBottom, sqDepth + zFightingOffset).tex(symbolUSt, getIconVEn(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				vb.pos(iconEdgeRad, iconDialBottom, sqDepth + zFightingOffset).tex(symbolUEn, getIconVEn(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				vb.pos(iconEdgeRad, iconDialTop, sqDepth + zFightingOffset).tex(symbolUEn, getIconVSt(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				tess.draw();
				GlStateManager.rotated(-90, 0, 0, 1);
			}

			GlStateManager.popMatrix();

			//Other front
			GlStateManager.pushMatrix();
			GlStateManager.rotated(-45, 0, 0, 1);

			//Draw each symbol individually
			for(int i = 0; i < frame.chevrons.length; i++){
				if(frame.chevrons[i] == null){
					break;
				}
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vb.pos(-iconEdgeRad, iconDialTop, -sqDepth - zFightingOffset).tex(symbolUEn, getIconVSt(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				vb.pos(iconEdgeRad, iconDialTop, -sqDepth - zFightingOffset).tex(symbolUSt, getIconVSt(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				vb.pos(iconEdgeRad, iconDialBottom, -sqDepth - zFightingOffset).tex(symbolUSt, getIconVEn(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				vb.pos(-iconEdgeRad, iconDialBottom, -sqDepth - zFightingOffset).tex(symbolUEn, getIconVEn(GatewayAddress.getEntryID(frame.chevrons[i]))).endVertex();
				tess.draw();
				GlStateManager.rotated(90, 0, 0, 1);
			}

			GlStateManager.popMatrix();
		}

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}

	private float getIconVSt(int index){
		return index / 8F;
	}

	private float getIconVEn(int index){
		return (index + 1) / 8F;
	}

	@Override
	public boolean isGlobalRenderer(GatewayFrameTileEntity te){
		return te.isActive();
	}
}
