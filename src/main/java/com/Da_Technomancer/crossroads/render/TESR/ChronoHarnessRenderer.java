package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChronoHarnessTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class ChronoHarnessRenderer extends LinkLineRenderer<ChronoHarnessTileEntity>{

	protected ChronoHarnessRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(ChronoHarnessTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		float angle = te.getRenderAngle(partialTicks);
		int medLight = CRRenderUtil.calcMediumLighting(combinedLight);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());

		//Revolving rods
		matrix.translate(0.5D, 0, 0.5D);

		float smallOffset = 0.0928F;
		float largeOffset = 5F / 16F;

		matrix.rotate(Vector3f.YP.rotationDegrees(angle));

		TextureAtlasSprite innerSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.COPSHOWIUM_TEXTURE);
		addRod(builder, matrix, smallOffset, smallOffset, innerSprite, medLight);
		addRod(builder, matrix, smallOffset, -smallOffset, innerSprite, medLight);
		addRod(builder, matrix, -smallOffset, -smallOffset, innerSprite, medLight);
		addRod(builder, matrix, -smallOffset, smallOffset, innerSprite, medLight);

		matrix.rotate(Vector3f.YP.rotationDegrees(-2F * angle));

		TextureAtlasSprite outerSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);
		addRod(builder, matrix, smallOffset, largeOffset, outerSprite, medLight);
		addRod(builder, matrix, smallOffset, -largeOffset, outerSprite, medLight);
		addRod(builder, matrix, -smallOffset, largeOffset, outerSprite, medLight);
		addRod(builder, matrix, -smallOffset, -largeOffset, outerSprite, medLight);
		addRod(builder, matrix, largeOffset, smallOffset, outerSprite, medLight);
		addRod(builder, matrix, largeOffset, -smallOffset, outerSprite, medLight);
		addRod(builder, matrix, -largeOffset, smallOffset, outerSprite, medLight);
		addRod(builder, matrix, -largeOffset, -smallOffset, outerSprite, medLight);
	}

	private void addRod(IVertexBuilder builder, MatrixStack matrix, float x, float z, TextureAtlasSprite sprite, int light){
		float rad = 1F / 16F;
		float minY = 2F / 16F;
		float maxY = 14F / 16F;

		float uEn = sprite.getInterpolatedU(2 * rad * 16);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, uEn, sprite.getMaxV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, uEn, sprite.getMinV(), 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, uEn, sprite.getMinV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, sprite.getMinU(), sprite.getMinV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, sprite.getMinU(), sprite.getMaxV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, uEn, sprite.getMaxV(), 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, uEn, sprite.getMinV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, sprite.getMinU(), sprite.getMinV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, sprite.getMinU(), sprite.getMaxV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, uEn, sprite.getMaxV(), -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getMaxV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getMinV(), 1, 0, 0, light);
	}
}
