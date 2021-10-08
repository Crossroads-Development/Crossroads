package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class RenderFlameCoreEntity extends EntityRenderer<EntityFlameCore>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/flame.png");

	protected RenderFlameCoreEntity(EntityRenderDispatcher renderManager){
		super(renderManager);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityFlameCore entity){
		return TEXTURE;
	}

	@Override
	public void render(EntityFlameCore entity, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int packedLight){

		Color color = new Color(entity.getEntityData().get(EntityFlameCore.COLOR), true);
		int[] col = new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
		float scale = EntityFlameCore.FLAME_VEL * (float) entity.getEntityData().get(EntityFlameCore.TIME_EXISTED);

		VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));

		CRRenderUtil.addVertexEntity(builder, matrix, -scale, -scale, -scale, 0, 0, 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, -scale, -scale, 1, 0, 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, scale, -scale, 1, 1, 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, scale, -scale, 0, 1, 0, 0, -1, CRRenderUtil.BRIGHT_LIGHT, col);

		CRRenderUtil.addVertexEntity(builder, matrix, -scale, -scale, -scale, 0, 0, 0, -1, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, -scale, -scale, 1, 0, 0, -1, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, -scale, scale, 1, 1, 0, -1, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, -scale, scale, 0, 1, 0, -1, 0, CRRenderUtil.BRIGHT_LIGHT, col);

		CRRenderUtil.addVertexEntity(builder, matrix, -scale, -scale, -scale, 0, 0, -1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, -scale, scale, 1, 0, -1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, scale, scale, 1, 1, -1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, scale, -scale, 0, 1, -1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);

		CRRenderUtil.addVertexEntity(builder, matrix, -scale, -scale, scale, 0, 0, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, -scale, scale, 1, 0, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, scale, scale, 1, 1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, scale, scale, 0, 1, 0, 0, 1, CRRenderUtil.BRIGHT_LIGHT, col);

		CRRenderUtil.addVertexEntity(builder, matrix, -scale, scale, -scale, 0, 0, 0, 1, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, scale, -scale, 1, 0, 0, 1, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, scale, scale, 1, 1, 0, 1, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, -scale, scale, scale, 0, 1, 0, 1, 0, CRRenderUtil.BRIGHT_LIGHT, col);

		CRRenderUtil.addVertexEntity(builder, matrix, scale, -scale, -scale, 0, 0, 1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, -scale, scale, 1, 0, 1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, scale, scale, 1, 1, 1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);
		CRRenderUtil.addVertexEntity(builder, matrix, scale, scale, -scale, 0, 1, 1, 0, 0, CRRenderUtil.BRIGHT_LIGHT, col);

		super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
	}

	@Override
	public boolean shouldRender(EntityFlameCore entity, Frustum helper, double camX, double camY, double camZ){
		return true;
	}
}
