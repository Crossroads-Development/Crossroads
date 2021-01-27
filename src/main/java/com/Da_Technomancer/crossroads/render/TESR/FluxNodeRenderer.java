package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxNodeTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class FluxNodeRenderer extends EntropyRenderer<FluxNodeTileEntity>{

	protected FluxNodeRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(FluxNodeTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

//		GlStateManager.pushMatrix();
//		GlStateManager.pushLightingAttributes();
//		GlStateManager.disableLighting();

		matrix.translate(0.5D, 0.5D, 0.5D);
//		GlStateManager.translated(x + .5D, y + .5D, z + .5D);

		float angle = te.getRenderAngle(partialTicks);
//		Tessellator tess = Tessellator.getInstance();
//		BufferBuilder buf = tess.getBuffer();

		matrix.rotate(Vector3f.YP.rotationDegrees(angle));
//		GlStateManager.rotated(angle, 0, 1, 0);

		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());

//		GlStateManager.color4f(1, 1, 1, 1);

		TextureAtlasSprite spriteGimbal = CRRenderUtil.getTextureSprite(CRRenderTypes.NODE_GIMBAL_TEXTURE);

//		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);
//		GlStateManager.rotated(90, 0, 0, 1);
//		GlStateManager.rotated(angle + 90, 0, 1, 0);
		matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		matrix.rotate(Vector3f.YP.rotationDegrees(angle + 90));
		matrix.scale(5F / 7F, 5F / 7F, 5F / 7F);
//		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);
//		GlStateManager.rotated(90, 0, 0, 1);
//		GlStateManager.rotated(angle + 90, 0, 1, 0);
		matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		matrix.rotate(Vector3f.YP.rotationDegrees(angle + 90));
		matrix.scale(5F / 7F, 5F / 7F, 5F / 7F);
//		GlStateManager.scalef(5F / 7F, 5F / 7F, 5F / 7F);
		drawGimbal(builder, matrix, spriteGimbal, combinedLight);

		TextureAtlasSprite spriteCop = CRRenderUtil.getTextureSprite(CRRenderTypes.COPSHOWIUM_TEXTURE);
//		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_COP);
		matrix.scale(7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F);
//		GlStateManager.scalef(7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F, 7F / 8F * 5F / 7F);
//		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		//Copshowium cube

		float size = 0.5F;

		builder.pos(matrix.getLast().getMatrix(), -size, -size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, size, -size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, -size, -size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();
//		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 1).endVertex();
//		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -size, -size, size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, -size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, size, size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 0).endVertex();
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -size, -size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, -size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();

		builder.pos(matrix.getLast().getMatrix(), size, -size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, -size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
//		buf.pos(0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(0.5D, 0.5D, -0.5D).tex(0, 1).endVertex();
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -size, -size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, -size, -size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, -size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, -size, size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
//		buf.pos(-0.5D, -0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(0.5D, -0.5D, -0.5D).tex(1, 0).endVertex();
//		buf.pos(0.5D, -0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(-0.5D, -0.5D, 0.5D).tex(0, 1).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -size, size, -size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -size, size, size).color(255, 255, 255, 255).tex(spriteCop.getMinU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, size, size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMaxV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), size, size, -size).color(255, 255, 255, 255).tex(spriteCop.getMaxU(), spriteCop.getMinV()).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, -0.5D).tex(0, 0).endVertex();
//		buf.pos(-0.5D, 0.5D, 0.5D).tex(0, 1).endVertex();
//		buf.pos(0.5D, 0.5D, 0.5D).tex(1, 1).endVertex();
//		buf.pos(0.5D, 0.5D, -0.5D).tex(1, 0).endVertex();
//		tess.draw();

//		GlStateManager.popAttributes();
//		GlStateManager.popMatrix();
	}

	private void drawGimbal(IVertexBuilder builder, MatrixStack matrix, TextureAtlasSprite sprite, int light){
		float outer = 7F / 16F;
		float inner = 5F / 16F;
		float edge = 1F / 16F;

		float texWidth = 14F;
		float edgeEnd = 16;
		float innerTex = 2F;

//		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		builder.pos(matrix.getLast().getMatrix(), outer, outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
//		buf.pos(outer, outer, edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, outer, edge).tex(0, 0).endVertex();
//		buf.pos(-outer, inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(outer, inner, edge).tex(texWidth, innerTex).endVertex();

		builder.pos(matrix.getLast().getMatrix(), outer, outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
//		buf.pos(outer, outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, inner, -edge).tex(texWidth, innerTex).endVertex();
//		buf.pos(-outer, inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(-outer, outer, -edge).tex(0, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), outer, -outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
//		buf.pos(outer, -outer, edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, -inner, edge).tex(texWidth, innerTex).endVertex();
//		buf.pos(-outer, -inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(-outer, -outer, edge).tex(0, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), outer, -outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
//		buf.pos(outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, -outer, -edge).tex(0, 0).endVertex();
//		buf.pos(-outer, -inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(outer, -inner, -edge).tex(texWidth, innerTex).endVertex();

		builder.pos(matrix.getLast().getMatrix(), outer, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
//		buf.pos(outer, inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(inner, inner, edge).tex(innerTex, innerTex).endVertex();
//		buf.pos(inner, -inner, edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(outer, -inner, edge).tex(0, texWidth - innerTex).endVertex();

		builder.pos(matrix.getLast().getMatrix(), outer, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
//		buf.pos(outer, inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(outer, -inner, -edge).tex(0, texWidth - innerTex).endVertex();
//		buf.pos(inner, -inner, -edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(inner, inner, -edge).tex(innerTex, innerTex).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -outer, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
//		buf.pos(-outer, inner, edge).tex(0, innerTex).endVertex();
//		buf.pos(-outer, -inner, edge).tex(0, texWidth - innerTex).endVertex();
//		buf.pos(-inner, -inner, edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(-inner, inner, edge).tex(innerTex, innerTex).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -outer, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(innerTex), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
//		buf.pos(-outer, inner, -edge).tex(0, innerTex).endVertex();
//		buf.pos(-inner, inner, -edge).tex(innerTex, innerTex).endVertex();
//		buf.pos(-inner, -inner, -edge).tex(innerTex, texWidth - innerTex).endVertex();
//		buf.pos(-outer, -inner, -edge).tex(0, texWidth - innerTex).endVertex();

		//Outer rim

		builder.pos(matrix.getLast().getMatrix(), -outer, -outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
//		buf.pos(-outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, -outer, -edge).tex(texWidth, texWidth).endVertex();
//		buf.pos(outer, -outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -outer, outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
//		buf.pos(-outer, outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, outer, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(outer, outer, -edge).tex(texWidth, texWidth).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -outer, -outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, -outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -outer, outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
//		buf.pos(-outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-outer, -outer, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(-outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(-outer, outer, -edge).tex(texWidth, texWidth).endVertex();

		builder.pos(matrix.getLast().getMatrix(), outer, -outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, outer, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), outer, -outer, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
//		buf.pos(outer, -outer, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(outer, outer, -edge).tex(texWidth, texWidth).endVertex();
//		buf.pos(outer, outer, edge).tex(edgeEnd, texWidth).endVertex();
//		buf.pos(outer, -outer, edge).tex(edgeEnd, 0).endVertex();

		//Inner rim

		builder.pos(matrix.getLast().getMatrix(), -inner, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
//		buf.pos(-inner, -inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(inner, -inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(inner, -inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -inner, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
//		buf.pos(-inner, inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();
//		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(-inner, inner, edge).tex(edgeEnd, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), -inner, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), -inner, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
//		buf.pos(-inner, -inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(-inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();
//		buf.pos(-inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(-inner, -inner, edge).tex(edgeEnd, 0).endVertex();

		builder.pos(matrix.getLast().getMatrix(), inner, -inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, -inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, inner, edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(edgeEnd), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		builder.pos(matrix.getLast().getMatrix(), inner, inner, -edge).color(255, 255, 255, 255).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth - innerTex)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
//		buf.pos(inner, -inner, -edge).tex(texWidth, 0).endVertex();
//		buf.pos(inner, -inner, edge).tex(edgeEnd, 0).endVertex();
//		buf.pos(inner, inner, edge).tex(edgeEnd, texWidth - innerTex).endVertex();
//		buf.pos(inner, inner, -edge).tex(texWidth, texWidth - innerTex).endVertex();

//		tess.draw();
	}
}
