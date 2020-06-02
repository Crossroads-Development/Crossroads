package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;

public class HeatingCrucibleRenderer extends TileEntityRenderer<HeatingCrucibleTileEntity>{

	protected HeatingCrucibleRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(HeatingCrucibleTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(te.getActiveTexture() == null || te.getBlockState().getBlock() != CRBlocks.heatingCrucible){
			return;
		}
		int fullness = te.getBlockState().get(CRProperties.FULLNESS);
		if(fullness == 0){
			return;
		}

		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(te.getActiveTexture());

		int light = CRRenderUtil.calcMediumLighting(combinedLight);//We want the molten fluid to glow in the dark slightly
		float xzStart = 2F / 16F;
		float xzEnd = 14F / 16F;
		float height = (float) (2 + 4 * fullness) / 16F;

		matrix.push();
		CRRenderUtil.addVertexBlock(builder, matrix, xzEnd, height, xzStart, sprite.getInterpolatedU(xzEnd * 16), sprite.getInterpolatedV(16 - (xzStart * 16)), 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, xzStart, height, xzStart, sprite.getInterpolatedU(xzStart * 16), sprite.getInterpolatedV(16 - (xzStart * 16)), 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, xzStart, height, xzEnd, sprite.getInterpolatedU(xzStart * 16), sprite.getInterpolatedV(16 - (xzEnd * 16)), 0, 1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, xzEnd, height, xzEnd, sprite.getInterpolatedU(xzEnd * 16), sprite.getInterpolatedV(16 - (xzEnd * 16)), 0, 1, 0, light);
		matrix.pop();
	}
}
