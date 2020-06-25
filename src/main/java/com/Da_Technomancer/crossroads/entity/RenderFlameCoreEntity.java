package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class RenderFlameCoreEntity extends EntityRenderer<EntityFlameCore>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/flame.png");

	protected RenderFlameCoreEntity(EntityRendererManager renderManager){
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityFlameCore entity){
		return TEXTURE;
	}

	@Override
	public void render(EntityFlameCore entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight){

		Color color = new Color(entity.getDataManager().get(EntityFlameCore.COLOR), true);
		int[] col = new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
		float scale = EntityFlameCore.FLAME_VEL * (float) entity.getDataManager().get(EntityFlameCore.TIME_EXISTED);

		IVertexBuilder builder = buffer.getBuffer(RenderType.getEntityTranslucent(getEntityTexture(entity)));

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
	public boolean shouldRender(EntityFlameCore entity, ClippingHelperImpl helper, double camX, double camY, double camZ){
		return true;
	}
}
