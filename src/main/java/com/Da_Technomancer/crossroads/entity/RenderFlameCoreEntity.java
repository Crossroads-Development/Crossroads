package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderFlameCoreEntity extends EntityRenderer<EntityFlameCore>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/particle/flame.png");

	protected RenderFlameCoreEntity(EntityRendererManager renderManager){
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityFlameCore entity){
		return TEXTURE;
	}

	@Override
	public void render(EntityFlameCore entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight){
		Color col = new Color(entity.getDataManager().get(EntityFlameCore.COLOR), true);

		matrix.push();

		GlStateManager.enableBlend();
		CRRenderUtil.setBrightLighting();

		GlStateManager.color4f((float) col.getRed() / 255F, (float) col.getGreen() / 255F, (float) col.getBlue() / 255F, (float) col.getAlpha() / 255F);
		GlStateManager.translated(x, y, z);

		double scale = EntityFlameCore.FLAME_VEL * (float) entity.getDataManager().get(EntityFlameCore.TIME_EXISTED);
		GlStateManager.scaled(scale, scale, scale);

		bindEntityTexture(entity);

		buf.pos(-1, -1, -1).tex(0, 0).endVertex();
		buf.pos(1, -1, -1).tex(1, 0).endVertex();
		buf.pos(1, 1, -1).tex(1, 1).endVertex();
		buf.pos(-1, 1, -1).tex(0, 1).endVertex();

		buf.pos(-1, -1, -1).tex(0, 0).endVertex();
		buf.pos(1, -1, -1).tex(1, 0).endVertex();
		buf.pos(1, -1, 1).tex(1, 1).endVertex();
		buf.pos(-1, -1, 1).tex(0, 1).endVertex();

		buf.pos(-1, -1, -1).tex(0, 0).endVertex();
		buf.pos(-1, -1, 1).tex(1, 0).endVertex();
		buf.pos(-1, 1, 1).tex(1, 1).endVertex();
		buf.pos(-1, 1, -1).tex(0, 1).endVertex();

		buf.pos(-1, -1, 1).tex(0, 0).endVertex();
		buf.pos(1, -1, 1).tex(1, 0).endVertex();
		buf.pos(1, 1, 1).tex(1, 1).endVertex();
		buf.pos(-1, 1, 1).tex(0, 1).endVertex();

		buf.pos(-1, 1, -1).tex(0, 0).endVertex();
		buf.pos(1, 1, -1).tex(1, 0).endVertex();
		buf.pos(1, 1, 1).tex(1, 1).endVertex();
		buf.pos(-1, 1, 1).tex(0, 1).endVertex();

		buf.pos(1, -1, -1).tex(0, 0).endVertex();
		buf.pos(1, -1, 1).tex(1, 0).endVertex();
		buf.pos(1, 1, 1).tex(1, 1).endVertex();
		buf.pos(1, 1, -1).tex(0, 1).endVertex();

		matrix.pop();

		super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
	}

	@Override
	public boolean shouldRender(EntityFlameCore entity, ClippingHelperImpl helper, double camX, double camY, double camZ){
		return true;
	}
}
