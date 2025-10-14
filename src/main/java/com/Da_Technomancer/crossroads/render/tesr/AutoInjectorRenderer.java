package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AutoInjectorRenderer implements BlockEntityRenderer<AutoInjectorTileEntity>{


	protected AutoInjectorRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(AutoInjectorTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.autoInjector){
			return;
		}
		Direction dir = state.getValue(CRProperties.FACING);

		matrix.translate(0.5D, 0.5D, 0.5D);
		matrix.mulPose(dir.getRotation());

		//Area of effect overlay when holding wrench
		if(ConfigUtil.isWrench(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND)) || ConfigUtil.isWrench(Minecraft.getInstance().player.getItemInHand(InteractionHand.OFF_HAND))){
			float radius = AutoInjectorTileEntity.SIZE / 2F + 0.01F;
			int[] overlayCol = {0, 255, 100, 60};
			VertexConsumer overlayBuilder = buffer.getBuffer(CRRenderTypes.AREA_OVERLAY_TYPE);

			matrix.pushPose();
			matrix.translate(0, radius + 0.5D - 0.001D, 0);

			overlayBuilder.addVertex(matrix.last().pose(), -radius, -radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, -radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, -radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 8);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, -radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 8);

			overlayBuilder.addVertex(matrix.last().pose(), -radius, radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 8);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 8);

			overlayBuilder.addVertex(matrix.last().pose(), radius, -radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 8);
			overlayBuilder.addVertex(matrix.last().pose(), radius, -radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 8);

			overlayBuilder.addVertex(matrix.last().pose(), -radius, -radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 0);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 0);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 8);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, -radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 8);

			overlayBuilder.addVertex(matrix.last().pose(), -radius, -radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, -radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 8);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, radius, -radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 8);

			overlayBuilder.addVertex(matrix.last().pose(), -radius, -radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, -radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 0);
			overlayBuilder.addVertex(matrix.last().pose(), radius, radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(8, 8);
			overlayBuilder.addVertex(matrix.last().pose(), -radius, radius, radius).setColor(overlayCol[0], overlayCol[1], overlayCol[2], overlayCol[3]).setUv(0, 8);

			matrix.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(AutoInjectorTileEntity te){
		return new AABB(te.getBlockPos()).inflate(AutoInjectorTileEntity.SIZE);
	}
}
