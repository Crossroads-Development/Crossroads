package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class AutoInjectorRenderer extends TileEntityRenderer<AutoInjectorTileEntity>{


	protected AutoInjectorRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(AutoInjectorTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.autoInjector){
			return;
		}
		Direction dir = state.getValue(ESProperties.FACING);

		matrix.translate(0.5D, 0.5D, 0.5D);
		matrix.mulPose(dir.getRotation());

		//Area of effect overlay when holding wrench
		if(ESConfig.isWrench(Minecraft.getInstance().player.getItemInHand(Hand.MAIN_HAND)) || ESConfig.isWrench(Minecraft.getInstance().player.getItemInHand(Hand.OFF_HAND))){
			float radius = AutoInjectorTileEntity.SIZE / 2F + 0.01F;
			int[] overlayCol = {0, 255, 100, 60};
			IVertexBuilder overlayBuilder = buffer.getBuffer(CRRenderTypes.AREA_OVERLAY_TYPE);

			matrix.pushPose();
			matrix.translate(0, radius + 0.5D - 0.001D, 0);

			overlayBuilder.vertex(matrix.last().pose(), -radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 8).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 8).endVertex();

			overlayBuilder.vertex(matrix.last().pose(), -radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 8).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 8).endVertex();

			overlayBuilder.vertex(matrix.last().pose(), radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 8).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 8).endVertex();

			overlayBuilder.vertex(matrix.last().pose(), -radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 8).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 8).endVertex();

			overlayBuilder.vertex(matrix.last().pose(), -radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, -radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 8).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, radius, -radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 8).endVertex();

			overlayBuilder.vertex(matrix.last().pose(), -radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, -radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 0).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(8, 8).endVertex();
			overlayBuilder.vertex(matrix.last().pose(), -radius, radius, radius).color(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).uv(0, 8).endVertex();

			matrix.popPose();
		}
	}
}
