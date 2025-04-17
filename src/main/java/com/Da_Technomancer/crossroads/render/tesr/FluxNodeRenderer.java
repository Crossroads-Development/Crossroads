package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.FluxNodeTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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

		matrix.mulPose(Axis.YP.rotationDegrees(angle));
//		GlStateManager.rotated(angle, 0, 1, 0);

		VertexConsumer builder = buffer.getBuffer(RenderType.solid());

//		GlStateManager.color4f(1, 1, 1, 1);

		TextureAtlasSprite spriteGimbal = CRRenderUtil.getTextureSprite(CRRenderTypes.NODE_GIMBAL_TEXTURE);

//		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);
//		GlStateManager.rotated(90, 0, 0, 1);
//		GlStateManager.rotated(angle + 90, 0, 1, 0);
		matrix.mulPose(Axis.ZP.rotationDegrees(90));
		matrix.mulPose(Axis.YP.rotationDegrees(angle + 90));
		matrix.scale(5F / 7F, 5F / 7F, 5F / 7F);
//		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);
//		GlStateManager.rotated(90, 0, 0, 1);
//		GlStateManager.rotated(angle + 90, 0, 1, 0);
		matrix.mulPose(Axis.ZP.rotationDegrees(90));
		matrix.mulPose(Axis.YP.rotationDegrees(angle + 90));
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

		builder.addVertex(matrix.last().pose(), -size, -size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -size, size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), size, size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), size, -size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, -1);
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0);
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1);
//		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 1);
//		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0);

		builder.addVertex(matrix.last().pose(), -size, -size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), size, -size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), size, size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -size, size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, 0, 1);
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 0);
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0);
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1);
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1);

		builder.addVertex(matrix.last().pose(), -size, -size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -size, -size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -size, size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -size, size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), -1, 0, 0);
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0);
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(1, 0);
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(1, 1);
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1);

		builder.addVertex(matrix.last().pose(), size, -size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), size, size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), size, size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), size, -size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 1, 0, 0);
//		buf.pos(0.5D, -0.5D, -0.5D).tex(0, 0);
//		buf.pos(0.5D, 0.5D, -0.5D).tex(0, 1);
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1);
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0);

		builder.addVertex(matrix.last().pose(), -size, -size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), size, -size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), size, -size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), -size, -size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, -1, 0);
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0);
//		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0);
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 1);
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 1);

		builder.addVertex(matrix.last().pose(), -size, size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), -size, size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU0(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), size, size, size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV1()).setLight(combinedLight).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), size, size, -size).setColor(255, 255, 255, 255).setUv(spriteCop.getU1(), spriteCop.getV0()).setLight(combinedLight).setNormal(matrix.last(), 0, 1, 0);
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 0);
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1);
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1);
//		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 0);
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

		builder.addVertex(matrix.last().pose(), outer, outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -outer, outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -outer, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), outer, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
//		buf.pos(outer, outer, edge).tex(texWidth, 0);
//		buf.pos(-outer, outer, edge).tex(0, 0);
//		buf.pos(-outer, inner, edge).tex(0, innerTex);
//		buf.pos(outer, inner, edge).tex(texWidth, innerTex);

		builder.addVertex(matrix.last().pose(), outer, outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), outer, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -outer, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -outer, outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
//		buf.pos(outer, outer, -edge).tex(texWidth, 0);
//		buf.pos(outer, inner, -edge).tex(texWidth, innerTex);
//		buf.pos(-outer, inner, -edge).tex(0, innerTex);
//		buf.pos(-outer, outer, -edge).tex(0, 0);

		builder.addVertex(matrix.last().pose(), outer, -outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), outer, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -outer, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -outer, -outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
//		buf.pos(outer, -outer, edge).tex(texWidth, 0);
//		buf.pos(outer, -inner, edge).tex(texWidth, innerTex);
//		buf.pos(-outer, -inner, edge).tex(0, innerTex);
//		buf.pos(-outer, -outer, edge).tex(0, 0);

		builder.addVertex(matrix.last().pose(), outer, -outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -outer, -outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -outer, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), outer, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
//		buf.pos(outer, -outer, -edge).tex(texWidth, 0);
//		buf.pos(-outer, -outer, -edge).tex(0, 0);
//		buf.pos(-outer, -inner, -edge).tex(0, innerTex);
//		buf.pos(outer, -inner, -edge).tex(texWidth, innerTex);

		builder.addVertex(matrix.last().pose(), outer, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), inner, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), inner, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), outer, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
//		buf.pos(outer, inner, edge).tex(0, innerTex);
//		buf.pos(inner, inner, edge).tex(innerTex, innerTex);
//		buf.pos(inner, -inner, edge).tex(innerTex, texWidth - innerTex);
//		buf.pos(outer, -inner, edge).tex(0, texWidth - innerTex);

		builder.addVertex(matrix.last().pose(), outer, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), outer, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), inner, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), inner, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
//		buf.pos(outer, inner, -edge).tex(0, innerTex);
//		buf.pos(outer, -inner, -edge).tex(0, texWidth - innerTex);
//		buf.pos(inner, -inner, -edge).tex(innerTex, texWidth - innerTex);
//		buf.pos(inner, inner, -edge).tex(innerTex, innerTex);

		builder.addVertex(matrix.last().pose(), -outer, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -outer, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -inner, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
		builder.addVertex(matrix.last().pose(), -inner, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, 1);
//		buf.pos(-outer, inner, edge).tex(0, innerTex);
//		buf.pos(-outer, -inner, edge).tex(0, texWidth - innerTex);
//		buf.pos(-inner, -inner, edge).tex(innerTex, texWidth - innerTex);
//		buf.pos(-inner, inner, edge).tex(innerTex, innerTex);

		builder.addVertex(matrix.last().pose(), -outer, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -inner, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -inner, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(innerTex), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
		builder.addVertex(matrix.last().pose(), -outer, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(0), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 0, -1);
//		buf.pos(-outer, inner, -edge).tex(0, innerTex);
//		buf.pos(-inner, inner, -edge).tex(innerTex, innerTex);
//		buf.pos(-inner, -inner, -edge).tex(innerTex, texWidth - innerTex);
//		buf.pos(-outer, -inner, -edge).tex(0, texWidth - innerTex);

		//Outer rim

		builder.addVertex(matrix.last().pose(), -outer, -outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), outer, -outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), outer, -outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), -outer, -outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
//		buf.pos(-outer, -outer, -edge).tex(texWidth, 0);
//		buf.pos(outer, -outer, -edge).tex(texWidth, texWidth);
//		buf.pos(outer, -outer, edge).tex(edgeEnd, texWidth);
//		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0);

		builder.addVertex(matrix.last().pose(), -outer, outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), -outer, outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), outer, outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), outer, outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
//		buf.pos(-outer, outer, -edge).tex(texWidth, 0);
//		buf.pos(-outer, outer, edge).tex(edgeEnd, 0);
//		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth);
//		buf.pos(outer, outer, -edge).tex(texWidth, texWidth);

		builder.addVertex(matrix.last().pose(), -outer, -outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -outer, -outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -outer, outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -outer, outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
//		buf.pos(-outer, -outer, -edge).tex(texWidth, 0);
//		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0);
//		buf.pos(-outer, outer, edge).tex(edgeEnd, texWidth);
//		buf.pos(-outer, outer, -edge).tex(texWidth, texWidth);

		builder.addVertex(matrix.last().pose(), outer, -outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), outer, outer, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), outer, outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), outer, -outer, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
//		buf.pos(outer, -outer, -edge).tex(texWidth, 0);
//		buf.pos(outer, outer, -edge).tex(texWidth, texWidth);
//		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth);
//		buf.pos(outer, -outer, edge).tex(edgeEnd, 0);

		//Inner rim

		builder.addVertex(matrix.last().pose(), -inner, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), -inner, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), inner, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
		builder.addVertex(matrix.last().pose(), inner, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, -1, 0);
//		buf.pos(-inner, -inner, -edge).tex(texWidth, 0);
//		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0);
//		buf.pos(inner, -inner, edge).tex(edgeEnd, texWidth - innerTex);
//		buf.pos(inner, -inner, -edge).tex(texWidth, texWidth - innerTex);

		builder.addVertex(matrix.last().pose(), -inner, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), inner, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), inner, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
		builder.addVertex(matrix.last().pose(), -inner, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 0, 1, 0);
//		buf.pos(-inner, inner, -edge).tex(texWidth, 0);
//		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex);
//		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex);
//		buf.pos(-inner, inner, edge).tex(edgeEnd, 0);

		builder.addVertex(matrix.last().pose(), -inner, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -inner, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -inner, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
		builder.addVertex(matrix.last().pose(), -inner, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), -1, 0, 0);
//		buf.pos(-inner, -inner, -edge).tex(texWidth, 0);
//		buf.pos(-inner, inner, -edge).tex(texWidth, texWidth - innerTex);
//		buf.pos(-inner, inner, edge).tex(edgeEnd, texWidth - innerTex);
//		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0);

		builder.addVertex(matrix.last().pose(), inner, -inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), inner, -inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(0)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), inner, inner, edge).setColor(255, 255, 255, 255).setUv(sprite.getU(edgeEnd), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
		builder.addVertex(matrix.last().pose(), inner, inner, -edge).setColor(255, 255, 255, 255).setUv(sprite.getU(texWidth), sprite.getV(texWidth - innerTex)).setLight(light).setNormal(matrix.last(), 1, 0, 0);
//		buf.pos(inner, -inner, -edge).tex(texWidth, 0);
//		buf.pos(inner, -inner, edge).tex(edgeEnd, 0);
//		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex);
//		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex);

//		tess.draw();
	}
}
