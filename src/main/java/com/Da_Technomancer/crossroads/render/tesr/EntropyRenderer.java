package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Quaternionf;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EntropyRenderer<T extends BlockEntity & IFluxLink> extends LinkLineRenderer<T>{

	public EntropyRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn){
		super(rendererDispatcherIn);
	}

	@Override
	public void render(T te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		int[] arcs = te.getRenderedArcs();
		if(arcs.length == 0){
			return;
		}

		matrix.pushPose();
		matrix.translate(0.5D, 0.5D, 0.5D);
		VertexConsumer entropyBuilder = buffer.getBuffer(CRRenderTypes.FLUX_TRANSFER_TYPE);
		long gameTime = te.getLevel().getGameTime();
		for(int arc : arcs){
			byte relX = (byte) (arc & 0xFF);
			byte relY = (byte) ((arc >> 8) & 0xFF);
			byte relZ = (byte) ((arc >> 16) & 0xFF);
			float length = (float) Math.sqrt(relX * relX + relY * relY + relZ * relZ);
			matrix.pushPose();
			matrix.mulPose(Axis.YP.rotation((float) Math.atan2(relX, relZ)));
			matrix.mulPose(Axis.XP.rotation((float) (Math.atan2(-relY, Math.sqrt(relX * relX + relZ * relZ)) + Math.PI / 2F)));
			renderArc(length, matrix, entropyBuilder, gameTime, partialTicks);
			matrix.popPose();
		}
		matrix.popPose();
	}

	/**
	 * Renders an entropy transfer arc along the +y axis
	 * Does not modify the passed matrix
	 * @param length Length
	 * @param matrix Matrix stack, ideally translated and rotated to the desired position and orientation
	 * @param entropyBuilder The builder associated with CRRenderTypes.FLUX_TRANSFER_TYPE
	 * @param worldTime Total world/game time
	 * @param partialTicks Partial ticks, [0, 1]
	 */
	public static void renderArc(float length, PoseStack matrix, VertexConsumer entropyBuilder, long worldTime, float partialTicks){
		matrix.pushPose();

		final float unitLen = 0.5F;
		int lenCount = (int) (length / unitLen);

		matrix.scale(1, length / (unitLen * lenCount), 1);//As lenCount is an integer, this scale factor may slightly stretch the entire render to account for rounding error

		for(int i = 0; i < 3; i++){
			matrix.mulPose(Axis.YP.rotationDegrees(12.5F + i * (i == 2 ? -1 : 1) * (worldTime + partialTicks) * 20F));
			float lenOffset = i * 0.2F;
			float radius = i / 20F + 0.03F;
			int circumCount = (int) (radius * 64F);
			float angle = (float) Math.PI * 2F / circumCount;
			Quaternionf stepRotation = Axis.YP.rotation(angle);
			Quaternionf lengthRotation = Axis.YP.rotation(angle / 3F);
			for(int j = 0; j < circumCount; j++){
				matrix.mulPose(stepRotation);
				matrix.pushPose();
				for(int k = 0; k < lenCount; k++){
					matrix.mulPose(lengthRotation);
					float sideRad = ((i + j + k) % 3) * 0.007F + 0.005F;
					float pieceLen = 0.3F + ((i + j * 3 + k * 2) % 4) * 0.05F;
					int[] color = ((i + j * 2 + k) % 7) == 0 ? new int[] {255, 255, 255, 64} : new int[] {0, 0, 0, 255};
					entropyBuilder.vertex(matrix.last().pose(), radius, k * unitLen + lenOffset, -sideRad).color(color[0], color[1], color[2], color[3]).uv(0, 0).endVertex();
					entropyBuilder.vertex(matrix.last().pose(), radius, k * unitLen + lenOffset, sideRad).color(color[0], color[1], color[2], color[3]).uv(0, 1).endVertex();
					entropyBuilder.vertex(matrix.last().pose(), radius, k * unitLen + pieceLen + lenOffset, sideRad).color(color[0], color[1], color[2], color[3]).uv(1, 1).endVertex();
					entropyBuilder.vertex(matrix.last().pose(), radius, k * unitLen + pieceLen + lenOffset, -sideRad).color(color[0], color[1], color[2], color[3]).uv(1, 0).endVertex();
				}
				matrix.popPose();
			}
		}

		matrix.popPose();
	}
}
