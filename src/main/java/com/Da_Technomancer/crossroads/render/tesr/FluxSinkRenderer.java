package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.FluxSinkTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FluxSinkRenderer extends EntropyRenderer<FluxSinkTileEntity>{

	protected FluxSinkRenderer(BlockEntityRendererProvider.Context dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(FluxSinkTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		float runtime = te.getRunDuration() + partialTicks;
		if(runtime <= 0){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);

		//Render entropy arcs to the plates
		if(te.renderPortals[0] != -1){
			VertexConsumer entropyBuilder = buffer.getBuffer(CRRenderTypes.FLUX_TRANSFER_TYPE);
			long worldTime = te.getLevel().getGameTime();
			for(int portalIndex : te.renderPortals){
				if(portalIndex == -1){
					continue;
				}
				matrix.pushPose();
				float[] portalPos = getPortalCenterPos(portalIndex, runtime);
				matrix.mulPose(Vector3f.YP.rotation((float) Math.atan2(portalPos[0], portalPos[2])));
				matrix.mulPose(Vector3f.XP.rotation((float) (Math.atan2(-portalPos[1], Math.sqrt(portalPos[0] * portalPos[0] + portalPos[2] * portalPos[2])) + Math.PI / 2F)));
				EntropyRenderer.renderArc((float) Math.sqrt(portalPos[0] * portalPos[0] + portalPos[1] * portalPos[1] + portalPos[2] * portalPos[2]), matrix, entropyBuilder, worldTime, partialTicks);
				matrix.popPose();
			}
		}

		VertexConsumer builder = buffer.getBuffer(CRRenderTypes.FLUX_SINK_TYPE);

//		GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//Render an icosahedron

		float scale = (float) Math.min(1D, runtime / FluxSinkTileEntity.STARTUP_TIME) * 2.5F;//Expand slowly to full size when starting up

		int medLight = CRRenderUtil.calcMediumLighting(combinedLight);

		//Ring of plates
		matrix.pushPose();
		matrix.mulPose(Vector3f.YP.rotationDegrees(runtime / 10F));
		final float len = 4;
		final float plateScale = Math.min(runtime / (FluxSinkTileEntity.STARTUP_TIME * 2F / 3F), 1) * 0.5F;

		for(int i = 0; i < 8; i++){
			float yOffset = 0.4F * (float) Math.sin(runtime / 100 + i * 5);
			float textVSt = ((i + (int) (runtime / 8)) % 4) * 0.25F;//Animated texture

			builder.vertex(matrix.last().pose(), len, yOffset - plateScale, -plateScale).color(255, 255, 255, 255).uv(0.75F, textVSt).uv2(medLight).endVertex();
			builder.vertex(matrix.last().pose(), len, yOffset + plateScale, -plateScale).color(255, 255, 255, 255).uv(0.75F, textVSt + 0.25F).uv2(medLight).endVertex();
			builder.vertex(matrix.last().pose(), len, yOffset + plateScale, plateScale).color(255, 255, 255, 255).uv(1, textVSt + 0.25F).uv2(medLight).endVertex();
			builder.vertex(matrix.last().pose(), len, yOffset - plateScale, plateScale).color(255, 255, 255, 255).uv(1, textVSt).uv2(medLight).endVertex();

			matrix.mulPose(Vector3f.YP.rotationDegrees(360F / 8F));
		}

		matrix.popPose();

		//Wobble effect
		matrix.mulPose(Vector3f.XP.rotationDegrees(8F * (float) Math.sin(runtime / 40D)));
		matrix.mulPose(Vector3f.ZP.rotationDegrees(8F * (float) Math.cos(runtime / 40D)));

		//Inner layer, almost opaque
		drawIcos(builder, matrix, scale, 0, 0, new int[] {255, 255, 255, 230}, combinedLight);

		//See-through outer shell
		drawIcos(builder, matrix, scale + 0.2F + (float) Math.sin(runtime / 20D) * 0.08F, 0.25F, 0.25F, new int[] {255, 255, 255, 35}, medLight);
	}

	//Angle of the axis of symmetry that each segment of the icosahedron is rotated about to form the overall shape
	//Found by brute force. If someone wants to do the geometric proof to correct the fourth decimal point, be my guest
	private static final double symmetryAxisAngle = 31.722F;
	private static final float symmetryAxisX = (float) Math.cos(Math.toRadians(symmetryAxisAngle));
	private static final float symmetryAxisY = (float) Math.sin(Math.toRadians(symmetryAxisAngle));
	private static final float goldRatio = (float) (1F + Math.sqrt(5)) / 2F;//The golden ratio

	private static void drawIcos(VertexConsumer builder, PoseStack matrix, float scale, float cornerU, float cornerV, int[] col, int light){
		final float smallLen = scale / 2;
		final float largeLen = goldRatio * smallLen;

		final float uSt = 0;
		final float uEn = 0.25F;
		final float vSt = 0;
		final float vEn = 0.25F;

		Vector3f rotationAxis = new Vector3f(symmetryAxisX, symmetryAxisY, 0);
		Vector3f rotationCounterAxis = new Vector3f(-symmetryAxisY, symmetryAxisX, 0);

		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 5; j++){
				//We want triangles, but are in QUADS mode
				//We double one of the vertices

				builder.vertex(matrix.last().pose(), 0, largeLen, smallLen).color(col[0], col[1], col[2], col[3]).uv(cornerU, cornerV).uv2(light).endVertex();
				builder.vertex(matrix.last().pose(), 0, largeLen, -smallLen).color(col[0], col[1], col[2], col[3]).uv(uEn, vSt).uv2(light).endVertex();
				builder.vertex(matrix.last().pose(), largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).uv(uSt, vEn).uv2(light).endVertex();
				builder.vertex(matrix.last().pose(), largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).uv(uSt, vEn).uv2(light).endVertex();//Repeat for triangle

				builder.vertex(matrix.last().pose(), 0, largeLen, smallLen).color(col[0], col[1], col[2], col[3]).uv(cornerU, cornerV).uv2(light).endVertex();
				builder.vertex(matrix.last().pose(), 0, largeLen, -smallLen).color(col[0], col[1], col[2], col[3]).uv(uEn, vSt).uv2(light).endVertex();
				builder.vertex(matrix.last().pose(), -largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).uv(uSt, vEn).uv2(light).endVertex();
				builder.vertex(matrix.last().pose(), -largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).uv(uSt, vEn).uv2(light).endVertex();//Repeat for triangle

				matrix.mulPose(rotationAxis.rotationDegrees(72));
			}
			matrix.mulPose(rotationAxis.rotationDegrees(36));
			matrix.mulPose(rotationCounterAxis.rotationDegrees(180));
		}
	}

	/**
	 * Used for rendering
	 * Gets the center position of the rendered 'portals', relative to the center of the blockpos
	 * @param plateIndex An integer in [0, 7]
	 * @param runtime Total time running
	 * @return A size 3 float array of the relative position of the center of a portal, in [x, y, z] order
	 */
	private static float[] getPortalCenterPos(int plateIndex, float runtime){
		float len = 3.65F;
		float angle = (float) -(Math.toRadians(360 / 8F) * plateIndex + Math.toRadians(runtime / 10D));
		float x = len * (float) Math.cos(angle);
		float z = len * (float) Math.sin(angle);
		float y = 0.4F * (float) Math.sin(runtime / 100 + plateIndex * 5);
		return new float[] {x, y, z};
	}
}
