package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RenderFlyingMachine extends EntityRenderer<EntityFlyingMachine>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/flying_machine.png");

	protected RenderFlyingMachine(EntityRendererManager renderManager){
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityFlyingMachine entity){
		return TEXTURE;
	}

	@Override
	public void render(EntityFlyingMachine entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light){
		matrix.rotate(Vector3f.YP.rotationDegrees(-entityYaw));

		IVertexBuilder builder = buffer.getBuffer(RenderType.getEntitySolid(getEntityTexture(entity)));

		matrix.push();
		matrix.translate(0, 0.5D, 0);
		matrix.rotate(Vector3f.XP.rotation(-entity.getAngle()));

		//All of these texture coords were originally specified as literals, and now that I have to port it to the new renderer, I regret this fact
		//But am also too lazy to fix this for the future
		//Future me, if you're reading this and need to re-work them again, sucks to be you --Your past self
		
		//Axle
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.125F, -0.125F, 0, 1, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.125F, 0.125F, 0, 0.9375F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.125F, 0.125F, 0.25F, 0.9375F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.125F, -0.125F, 0.25F, 1, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, -0.125F, -0.125F, 0.25F, 1, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, -0.125F, 0.125F, 0.25F, 0.9375F, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, -0.125F, 0.125F, 0, 0.9375F, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, -0.125F, -0.125F, 0, 1, 0, -1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, -0.125F, 0.125F, 0.25F, 1, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.125F, 0.125F, 0.25F, 0.9375F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.125F, 0.125F, 0, 0.9375F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, -0.125F, 0.125F, 0, 1, 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, -0.125F, -0.125F, 0, 1, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.125F, -0.125F, 0, 0.9375F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.125F, -0.125F, 0.25F, 0.9375F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, -0.125F, -0.125F, 0.25F, 1, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, -0.125F, -0.125F, 0, 0.875F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.125F, -0.125F, 0.0625F, 0.875F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.125F, 0.125F, 0.0625F, 0.9375F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, -0.125F, 0.125F, 0, 0.9375F, 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, -0.125F, 0.125F, 0, 0.9375F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.125F, 0.125F, 0.0625F, 0.9375F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.125F, -0.125F, 0.0625F, 0.875F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, -0.125F, -0.125F, 0, 0.875F, -1, 0, 0, light);

		//Gravity plates
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, 0.2F, -0.35F, 0.5F, 1, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, 0.2F, 0.35F, 0.5F, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, 0.2F, 0.35F, 0.75F, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, 0.2F, -0.35F, 0.75F, 1, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, -0.2F, -0.35F, 0.5F, 1, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, -0.2F, 0.35F, 0.5F, 0.75F, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, -0.2F, 0.35F, 0.25F, 0.75F, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, -0.2F, -0.35F, 0.25F, 1, 0, -1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, -0.2F, 0.35F, 1, 1, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, 0.2F, 0.35F, 1, 0.90625F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, 0.2F, 0.35F, 0.75F, 0.90625F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, -0.2F, 0.35F, 0.75F, 1, 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, -0.2F, -0.35F, 0.75F, 1, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, 0.2F, -0.35F, 0.75F, 0.90625F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, 0.2F, -0.35F, 1, 0.90625F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, -0.2F, -0.35F, 1, 1, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, -0.2F, 0.35F, 1, 1, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, -0.2F, -0.35F, 0.75F, 1, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, 0.2F, -0.35F, 0.75F, 0.90625F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.35F, 0.2F, 0.35F, 1, 0.90625F, 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, -0.2F, -0.35F, 1, 1, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, -0.2F, 0.35F, 0.75F, 1, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, 0.2F, 0.35F, 0.75F, 0.90625F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.35F, 0.2F, -0.35F, 1, 0.90625F, -1, 0, 0, light);

		matrix.pop();
		
		//End boxes
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.7F, -0.2F, 0.25F, 0.5F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.7F, 0.2F, 0, 0.5F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.3F, 0.2F, 0, 0.75F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.3F, -0.2F, 0.25F, 0.75F, 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.3F, -0.2F, 0.25F, 0.75F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.3F, 0.2F, 0, 0.75F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.7F, 0.2F, 0, 0.5F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.7F, -0.2F, 0.25F, 0.5F, 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.7F, -0.2F, 0.25F, 0.5F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.7F, 0.2F, 0, 0.5F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.3F, 0.2F, 0, 0.75F, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.3F, -0.2F, 0.25F, 0.75F, 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.3F, -0.2F, 0.25F, 0.75F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.3F, 0.2F, 0, 0.75F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.7F, 0.2F, 0, 0.5F, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.7F, -0.2F, 0.25F, 0.5F, -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.7F, -0.2F, 0.25F, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.7F, 0.2F, 0, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.7F, 0.2F, 0, 0.5F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.7F, -0.2F, 0.25F, 0.5F, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.7F, -0.2F, 0.25F, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.7F, 0.2F, 0, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.7F, 0.2F, 0, 0.5F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.7F, -0.2F, 0.25F, 0.5F, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.3F, -0.2F, 0.25F, 0.5F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.3F, 0.2F, 0, 0.5F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.3F, 0.2F, 0, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.3F, -0.2F, 0.25F, 0.75F, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.3F, -0.2F, 0.25F, 0.5F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.3F, 0.2F, 0, 0.5F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.3F, 0.2F, 0, 0.75F, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.3F, -0.2F, 0.25F, 0.75F, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.3F, -0.2F, 0.25F, 0.5F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.7F, -0.2F, 0, 0.5F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.7F, -0.2F, 0, 0.75F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.3F, -0.2F, 0.25F, 0.75F, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.3F, -0.2F, 0.25F, 0.5F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.7F, -0.2F, 0, 0.5F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.7F, -0.2F, 0, 0.75F, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.3F, -0.2F, 0.25F, 0.75F, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.3F, 0.2F, 0.25F, 0.75F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.5F, 0.7F, 0.2F, 0, 0.75F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.7F, 0.2F, 0, 0.5F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.7F, 0.3F, 0.2F, 0.25F, 0.5F, 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.3F, 0.2F, 0.25F, 0.75F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.7F, 0.7F, 0.2F, 0, 0.75F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.7F, 0.2F, 0, 0.5F, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.5F, 0.3F, 0.2F, 0.25F, 0.5F, 0, 0, 1, light);

		//Legs
		renderLeg(builder, matrix, light);
		matrix.push();
		float xTranslation = 1.275F;
		float zTranslation = 0.525F;
		matrix.translate(xTranslation, 0, 0);
		renderLeg(builder, matrix, light);
		matrix.translate(0, 0, zTranslation);
		renderLeg(builder, matrix, light);
		matrix.pop();
		matrix.push();
		matrix.translate(0, 0, zTranslation);
		renderLeg(builder, matrix, light);
		matrix.pop();

		//Supports
		matrix.push();
		renderSupport(builder, matrix, light);
		matrix.translate(1.275, 0, 0);
		renderSupport(builder, matrix, light);
		matrix.pop();

		//Seat
		float seatX = 0.7F;
		float seatYSt = 1.2F;
		float seatYEn = 1.3F;
		float seatZ = 0.4F;
		float seatUSt = 0;
		float seatUMid = 0.015625F;
		float seatUEn = 0.25F;
		float seatVSt = 0.5F;
		float seatVEn = 0.75F;

		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYEn, seatZ, seatUEn, seatVSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYEn, -seatZ, seatUEn, seatVEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYEn, -seatZ, seatUSt, seatVEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYEn, seatZ, seatUSt, seatVSt, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYSt, seatZ, seatUSt, seatVSt, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYSt, -seatZ, seatUSt, seatVEn, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYSt, -seatZ, seatUEn, seatVEn, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYSt, seatZ, seatUEn, seatVSt, 0, -1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYEn, seatZ, seatUSt, seatVSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYSt, seatZ, seatUMid, seatVSt, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYSt, seatZ, seatUMid, seatVEn, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYEn, seatZ, seatUSt, seatVEn, 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYEn, -seatZ, seatUSt, seatVEn, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYSt, -seatZ, seatUMid, seatVEn, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYSt, -seatZ, seatUMid, seatVSt, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYEn, -seatZ, seatUSt, seatVSt, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYEn, -seatZ, seatUSt, seatVEn, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYSt, -seatZ, seatUMid, seatVEn, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYSt, seatZ, seatUMid, seatVSt, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -seatX, seatYEn, seatZ, seatUSt, seatVSt, -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYEn, seatZ, seatUSt, seatVSt, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYSt, seatZ, seatUMid, seatVSt, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYSt, -seatZ, seatUMid, seatVEn, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, seatX, seatYEn, -seatZ, seatUSt, seatVEn, 1, 0, 0, light);
	}

	private static void renderSupport(IVertexBuilder builder, MatrixStack matrix, int light){
		float supportXSt = 0.575F;
		float supportXEn = 0.7F;
		float supportYSt = 0.7F;
		float supportYEn = 1.2F;
		float supportZ = 0.0625F;
		float supportUSt = 0;
		float supportUMid = 0.0625F;
		float supportUEn = 0.25F;
		float supportVSt = 0.875F;
		float supportVMid = 0.9375F;
		float supportVEn = 1;

		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYEn, supportZ, supportUMid, supportVSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYEn, -supportZ, supportUMid, supportVEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYEn, -supportZ, supportUSt, supportVEn, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYEn, supportZ, supportUSt, supportVSt, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYEn, supportZ, supportUSt, supportVMid, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYSt, supportZ, supportUEn, supportVMid, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYSt, supportZ, supportUEn, supportVEn, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYSt, supportZ, supportUSt, supportVEn, 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYEn, -supportZ, supportUSt, supportVEn, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYSt, -supportZ, supportUEn, supportVEn, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYSt, -supportZ, supportUEn, supportVMid, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYEn, -supportZ, supportUSt, supportVMid, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYEn, -supportZ, supportUSt, supportVEn, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYSt, -supportZ, supportUEn, supportVEn, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYSt, supportZ, supportUEn, supportVMid, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXEn, supportYEn, supportZ, supportUSt, supportVMid, -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYEn, supportZ, supportUSt, supportVMid, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYSt, supportZ, supportUEn, supportVMid, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYSt, -supportZ, supportUEn, supportVEn, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -supportXSt, supportYEn, -supportZ, supportUSt, supportVEn, 1, 0, 0, light);
	}

	private static void renderLeg(IVertexBuilder builder, MatrixStack matrix, int light){
		//Legs
		float legXSt = 0.575F;
		float legXEn = 0.7F;
		float legYSt = 0;
		float legYEn = 0.7F;
		float legZSt = 0.2F;
		float legZEn = 0.325F;
		float legUSt = 0;
		float legUMid = 0.0625F;
		float legUEn = 0.25F;
		float legVSt = 0.875F;
		float legVMid = 0.9375F;
		float legVEn = 1;

		//Leg
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYEn, -legZEn, legUSt, legVSt, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYEn, -legZSt, legUSt, legVMid, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYEn, -legZSt, legUMid, legVMid, 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYEn, -legZEn, legUMid, legVSt, 0, 1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYSt, -legZEn, legUMid, legVSt, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYSt, -legZSt, legUMid, legVMid, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYSt, -legZSt, legUSt, legVMid, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYSt, -legZEn, legUSt, legVSt, 0, -1, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYEn, -legZEn, legUSt, legVEn, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYSt, -legZEn, legUMid, legVEn, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYSt, -legZEn, legUMid, legVMid, 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYEn, -legZEn, legUSt, legVMid, 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYEn, -legZSt, legUSt, legVMid, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYSt, -legZSt, legUEn, legVMid, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYSt, -legZSt, legUEn, legVEn, 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYEn, -legZSt, legUSt, legVEn, 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYEn, -legZEn, legUSt, legVMid, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYSt, -legZEn, legUEn, legVMid, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYSt, -legZSt, legUEn, legVEn, -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXEn, legYEn, -legZSt, legUSt, legVEn, -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYEn, -legZSt, legUSt, legVEn, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYSt, -legZSt, legUEn, legVEn, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYSt, -legZEn, legUEn, legVMid, 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -legXSt, legYEn, -legZEn, legUSt, legVMid, 1, 0, 0, light);
	}
}
