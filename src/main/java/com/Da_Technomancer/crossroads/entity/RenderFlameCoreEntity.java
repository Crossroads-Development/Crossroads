package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class RenderFlameCoreEntity extends EntityRenderer<EntityFlameCore>{

	protected RenderFlameCoreEntity(EntityRendererProvider.Context context){
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityFlameCore entity){
		return CRRenderTypes.FLAME_CORE_TEXTURE;//Not actually used, as we render from a separate texture atlas
	}

	@Override
	public void render(EntityFlameCore entity, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int packedLight){

		Color color = new Color(entity.getEntityData().get(EntityFlameCore.COLOR), true);
		//Note that we tweak the alpha value
		int[] col = new int[] {color.getRed(), color.getGreen(), color.getBlue(), Math.max(150, Math.min(240, 80 + color.getAlpha()))};
		float scale = EntityFlameCore.FLAME_VEL * (float) entity.getEntityData().get(EntityFlameCore.TIME_EXISTED);
		float minU = -scale;
		float minV = minU;
		float maxU = minU + scale * 2;
		float maxV = maxU;

		VertexConsumer builder = buffer.getBuffer(CRRenderTypes.FLAME_CORE_TYPE);

		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, -scale, -scale, minU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, -scale, -scale, maxU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, scale, -scale, maxU, maxV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, scale, -scale, minU, maxV, col);

		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, -scale, -scale, minU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, -scale, -scale, maxU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, -scale, scale, maxU, maxV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, -scale, scale, minU, maxV, col);

		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, -scale, -scale, minU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, -scale, scale, maxU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, scale, scale, maxU, maxV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, scale, -scale, minU, maxV, col);

		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, -scale, scale, minU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, -scale, scale, maxU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, scale, scale, maxU, maxV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, scale, scale, minU, maxV, col);

		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, scale, -scale, minU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, scale, -scale, maxU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, scale, scale, maxU, maxV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, -scale, scale, scale, minU, maxV, col);

		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, -scale, -scale, minU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, -scale, scale, maxU, minV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, scale, scale, maxU, maxV, col);
		CRRenderUtil.addVertexPosColTex(builder, matrix, scale, scale, -scale, minU, maxV, col);

		super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
	}

	@Override
	public boolean shouldRender(EntityFlameCore entity, Frustum helper, double camX, double camY, double camZ){
		return true;
	}
}
