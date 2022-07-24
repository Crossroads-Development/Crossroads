package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.FluxNodeTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class FluxNodeRenderer extends EntropyRenderer<FluxNodeTileEntity>{

	protected FluxNodeRenderer(BlockEntityRendererProvider.Context dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(FluxNodeTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

//		GlStateManager.pushMatrix();
//		GlStateManager.pushLightingAttributes();
//		GlStateManager.disableLighting();

		matrix.translate(0.5D, 0.5D, 0.5D);
//		GlStateManager.translated(x + .5D, y + .5D, z + .5D);

		float angle = te.getRenderAngle(partialTicks);
//		Tessellator tess = Tessellator.getInstance();
//		BufferBuilder buf = tess.getBuffer();

		matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
//		GlStateManager.rotated(angle, 0, 1, 0);

		VertexConsumer builder = buffer.getBuffer(RenderType.solid());

//		GlStateManager.color4f(1, 1, 1, 1);

		TextureAtlasSprite spriteGimbal = CRRenderUtil.getTextureSprite(CRRenderTypes.NODE_GIMBAL_TEXTURE);

//		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);
//		GlStateManager.rotated(90, 0, 0, 1);
//		GlStateManager.rotated(angle + 90, 0, 1, 0);
		matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
		matrix.mulPose(Vector3f.YP.rotationDegrees(angle + 90));
		matrix.scale(5F / 7F, 5F / 7F, 5F / 7F);
//		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);
//		GlStateManager.rotated(90, 0, 0, 1);
//		GlStateManager.rotated(angle + 90, 0, 1, 0);
		matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
		matrix.mulPose(Vector3f.YP.rotationDegrees(angle + 90));
		matrix.scale(5F / 7F, 5F / 7F, 5F / 7F);
//		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);

		TextureAtlasSprite spriteCop = CRRenderUtil.getTextureSprite(CRRenderTypes.COPSHOWIUM_TEXTURE);
//		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_COP);
		matrix.scale(7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F);
//		GlStateManager.scalef(7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F);
//		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormats.POSITION_TEX);

		//Copshowium cube

		float size = 0.5F;

		builder.vertex(matrix.last().pose(), -size, -size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -size, size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), size, size, -size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), size, -size, -size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, -1).endVertex();
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();
//		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 1).endVertex();
//		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0).endVertex();

		builder.vertex(matrix.last().pose(), -size, -size, size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), size, -size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), size, size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -size, size, size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, 0, 1).endVertex();
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 0).endVertex();
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1).endVertex();

		builder.vertex(matrix.last().pose(), -size, -size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -size, -size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -size, size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -size, size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), -1, 0, 0).endVertex();
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();

		builder.vertex(matrix.last().pose(), size, -size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, -size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 1, 0, 0).endVertex();
//		buf.pos(0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();

		builder.vertex(matrix.last().pose(), -size, -size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, -size, -size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, -size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), -size, -size, size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, -1, 0).endVertex();
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0).endVertex();
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 1).endVertex();

		builder.vertex(matrix.last().pose(), -size, size, -size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), -size, size, size).color(255, 255, 255, 255).uv(spriteCop.getU0(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, size, size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV1()).uv2(combinedLight).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), size, size, -size).color(255, 255, 255, 255).uv(spriteCop.getU1(), spriteCop.getV0()).uv2(combinedLight).normal(matrix.last().normal(), 0, 1, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1).endVertex();
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 0).endVertex();
//		tess.draw();

//		GlStateManager.popAttributes();
//		GlStateManager.popMatrix();
	}

	private void drawGimbal(VertexConsumer builder, PoseStack matrix, TextureAtlasSprite sprite, int light){
		float outer = 7F / 16F;
		float inner = 5F / 16F;
		float edge = 1F / 16F;

		float texWidth = 14F;
		float edgeEnd = 16;
		float innerTex = 2F;

//		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormats.POSITION_TEX);

		builder.vertex(matrix.last().pose(), outer, outer, edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, outer, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), outer, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
//		buf.pos(outer, outer, edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, outer, edge).tex(0, 0).endVertex();
//		buf.pos(-outer, inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(outer, inner, edge).tex(texWidth, innerTex).endVertex();

		builder.vertex(matrix.last().pose(), outer, outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), outer, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
//		buf.pos(outer, outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, inner, -edge).tex(texWidth, innerTex).endVertex();
//		buf.pos(-outer, inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(-outer, outer, -edge).tex(0, 0).endVertex();

		builder.vertex(matrix.last().pose(), outer, -outer, edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), outer, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -outer, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
//		buf.pos(outer, -outer, edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, -inner, edge).tex(texWidth, innerTex).endVertex();
//		buf.pos(-outer, -inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(-outer, -outer, edge).tex(0, 0).endVertex();

		builder.vertex(matrix.last().pose(), outer, -outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), outer, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
//		buf.pos(outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, -outer, -edge).tex(0, 0).endVertex();
//		buf.pos(-outer, -inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(outer, -inner, -edge).tex(texWidth, innerTex).endVertex();

		builder.vertex(matrix.last().pose(), outer, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), inner, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), inner, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), outer, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
//		buf.pos(outer, inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(inner, inner, edge).tex(innerTex, innerTex).endVertex();
//		buf.pos(inner, -inner, edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(outer, -inner, edge).tex(0, texWidth - innerTex).endVertex();

		builder.vertex(matrix.last().pose(), outer, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), outer, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), inner, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), inner, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
//		buf.pos(outer, inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(outer, -inner, -edge).tex(0, texWidth - innerTex).endVertex();
//		buf.pos(inner, -inner, -edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(inner, inner, -edge).tex(innerTex, innerTex).endVertex();

		builder.vertex(matrix.last().pose(), -outer, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -inner, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
		builder.vertex(matrix.last().pose(), -inner, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, 1).endVertex();
//		buf.pos(-outer, inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(-outer, -inner, edge).tex(0, texWidth - innerTex).endVertex();
//		buf.pos(-inner, -inner, edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(-inner, inner, edge).tex(innerTex, innerTex).endVertex();

		builder.vertex(matrix.last().pose(), -outer, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -inner, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -inner, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(0), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 0, -1).endVertex();
//		buf.pos(-outer, inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(-inner, inner, -edge).tex(innerTex, innerTex).endVertex();
//		buf.pos(-inner, -inner, -edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(-outer, -inner, -edge).tex(0, texWidth - innerTex).endVertex();

		//Outer rim

		builder.vertex(matrix.last().pose(), -outer, -outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, -outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, -outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
//		buf.pos(-outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, -outer, -edge).tex(texWidth, texWidth).endVertex();
//		buf.pos(outer, -outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0).endVertex();

		builder.vertex(matrix.last().pose(), -outer, outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), -outer, outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
//		buf.pos(-outer, outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, outer, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(outer, outer, -edge).tex(texWidth, texWidth).endVertex();

		builder.vertex(matrix.last().pose(), -outer, -outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -outer, -outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -outer, outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -outer, outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
//		buf.pos(-outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(-outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(-outer, outer, -edge).tex(texWidth, texWidth).endVertex();

		builder.vertex(matrix.last().pose(), outer, -outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, outer, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), outer, -outer, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
//		buf.pos(outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, outer, -edge).tex(texWidth, texWidth).endVertex();
//		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(outer, -outer, edge).tex(edgeEnd, 0).endVertex();

		//Inner rim

		builder.vertex(matrix.last().pose(), -inner, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), -inner, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, -1, 0).endVertex();
//		buf.pos(-inner, -inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(inner, -inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(inner, -inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();

		builder.vertex(matrix.last().pose(), -inner, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrix.last().pose(), -inner, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 0, 1, 0).endVertex();
//		buf.pos(-inner, inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();
//		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(-inner, inner, edge).tex(edgeEnd, 0).endVertex();

		builder.vertex(matrix.last().pose(), -inner, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -inner, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -inner, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), -inner, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), -1, 0, 0).endVertex();
//		buf.pos(-inner, -inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();
//		buf.pos(-inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0).endVertex();

		builder.vertex(matrix.last().pose(), inner, -inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, -inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(0)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, inner, edge).color(255, 255, 255, 255).uv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
		builder.vertex(matrix.last().pose(), inner, inner, -edge).color(255, 255, 255, 255).uv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).uv2(light).normal(matrix.last().normal(), 1, 0, 0).endVertex();
//		buf.pos(inner, -inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(inner, -inner, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();

//		tess.draw();
	}
}
