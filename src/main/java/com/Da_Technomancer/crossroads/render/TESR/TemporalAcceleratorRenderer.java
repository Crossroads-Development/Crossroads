package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.TemporalAcceleratorTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class TemporalAcceleratorRenderer extends LinkLineRenderer<TemporalAcceleratorTileEntity>{


	protected TemporalAcceleratorRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(TemporalAcceleratorTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.temporalAccelerator){
			return;
		}
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		Direction dir = state.get(ESProperties.FACING);

		matrix.translate(0.5D, 0.5D, 0.5D);
		matrix.rotate(dir.getRotation());

		//Area of effect overlay when holding wrench
		if(ESConfig.isWrench(Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND)) || ESConfig.isWrench(Minecraft.getInstance().player.getHeldItem(Hand.OFF_HAND))){
			float radius = TemporalAcceleratorTileEntity.SIZE / 2F + 0.01F;
			int[] overlayCol = {255, 100, 0, 60};
			IVertexBuilder overlayBuilder = buffer.getBuffer(CRRenderTypes.AREA_OVERLAY_TYPE);

			matrix.push();
			matrix.translate(0, radius + 0.5D - 0.001D, 0);

			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 16).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 16).endVertex();

			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 16).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 16).endVertex();

			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 16).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 16).endVertex();

			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 16).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 16).endVertex();

			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 16).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 16).endVertex();

			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 0).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(16, 16).endVertex();
			overlayBuilder.pos(matrix.getLast().getMatrix(), -radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).tex(0, 16).endVertex();

			matrix.pop();
		}

		//Render the two rotating octagons

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		int[] col = CRRenderUtil.convertColor(GearFactory.findMaterial("copshowium").getColor());
		float angle = te.getWorld().getGameTime() + partialTicks;
		float lHalf = 7F / 16F;//Half the side length of the octagon
		int medLight = CRRenderUtil.calcMediumLighting(combinedLight);

		matrix.rotate(Vector3f.YP.rotationDegrees(angle));
		matrix.translate(0, 5F / 16F, 0);
		matrix.scale(2F * lHalf, 1, 2F * lHalf);
		CRModels.draw8Core(builder, matrix, col, medLight, sprite);

		matrix.rotate(Vector3f.YP.rotationDegrees(-2F * angle));
		matrix.translate(0, -4F / 16F, 0);
		matrix.scale(0.8F, 1, 0.8F);
		CRModels.draw8Core(builder, matrix, col, medLight, sprite);
	}
}
