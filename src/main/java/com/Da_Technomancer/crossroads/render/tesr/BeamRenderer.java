package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamHelper;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.templates.IBeamRenderTE;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;

/**
 * All blocks using BeamRenderer MUST return false to isOpaqueCube 
 */
public class BeamRenderer<T extends BlockEntity & IBeamRenderTE> implements BlockEntityRenderer<T>{

	public BeamRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	/**
	 * Draws a beam oriented up and grid-fixed
	 * Best used with a matrix at the center of the blockspace, rotated to face the desired direction and for visual rotation
	 * @param matrix The matrix, will not be modified
	 * @param builder The vertex builder, with a POSITION_COLOR_TEX format- expected to be BEAM_TYPE builder
	 * @param length The length of the beam to draw, in blocks
	 * @param width The width of the beam, in blocks
	 * @param color The beam color
	 */
	public static void drawBeam(PoseStack matrix, VertexConsumer builder, float length, float width, Color color){
		final float BEAM_SIDE_U = 0;
		final float BEAM_END_U = 0.5F;
		final float BEAM_V_STOP = 0.5F;
		
		float halfWidth = width / 2F;
		int[] col = {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
		float endV = length / 2;//V-coord for the far edge of the sides

		Matrix4f lastMatrix = matrix.last().pose();

		//Sides
		//-X
		builder.vertex(lastMatrix, -halfWidth, 0, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, 0).endVertex();
		builder.vertex(lastMatrix, -halfWidth, length, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, endV).endVertex();
		builder.vertex(lastMatrix, -halfWidth, length, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, endV).endVertex();
		builder.vertex(lastMatrix, -halfWidth, 0, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, 0).endVertex();
		//+X
		builder.vertex(lastMatrix, halfWidth, 0, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, 0).endVertex();
		builder.vertex(lastMatrix, halfWidth, length, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, endV).endVertex();
		builder.vertex(lastMatrix, halfWidth, length, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, endV).endVertex();
		builder.vertex(lastMatrix, halfWidth, 0, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, 0).endVertex();
		//-Z
		builder.vertex(lastMatrix, -halfWidth, 0, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, 0).endVertex();
		builder.vertex(lastMatrix, -halfWidth, length, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, endV).endVertex();
		builder.vertex(lastMatrix, halfWidth, length, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, endV).endVertex();
		builder.vertex(lastMatrix, halfWidth, 0, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, 0).endVertex();
		//+Z
		builder.vertex(lastMatrix, -halfWidth, 0, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, 0).endVertex();
		builder.vertex(lastMatrix, -halfWidth, length, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_SIDE_U, endV).endVertex();
		builder.vertex(lastMatrix, halfWidth, length, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, endV).endVertex();
		builder.vertex(lastMatrix, halfWidth, 0, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, 0).endVertex();

		//Near end
		builder.vertex(lastMatrix, -halfWidth, 0, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, 0).endVertex();
		builder.vertex(lastMatrix, -halfWidth, 0, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, BEAM_V_STOP).endVertex();
		builder.vertex(lastMatrix, halfWidth, 0, halfWidth).color(col[0], col[1], col[2], col[3]).uv(1, BEAM_V_STOP).endVertex();
		builder.vertex(lastMatrix, halfWidth, 0, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(1, 0).endVertex();
		//Far end
		builder.vertex(lastMatrix, -halfWidth, length, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, 0).endVertex();
		builder.vertex(lastMatrix, -halfWidth, length, halfWidth).color(col[0], col[1], col[2], col[3]).uv(BEAM_END_U, BEAM_V_STOP).endVertex();
		builder.vertex(lastMatrix, halfWidth, length, halfWidth).color(col[0], col[1], col[2], col[3]).uv(1, BEAM_V_STOP).endVertex();
		builder.vertex(lastMatrix, halfWidth, length, -halfWidth).color(col[0], col[1], col[2], col[3]).uv(1, 0).endVertex();
	}

	@Override
	public void render(T beam, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		int[] packets = beam.getRenderedBeams();

		matrix.pushPose();
		matrix.translate(0.5, 0.5, 0.5);

		boolean doRotation = CRConfig.rotateBeam.get();
		Quaternion verticalRot;
		if(doRotation){
			verticalRot = Vector3f.YP.rotationDegrees(CRRenderUtil.getRenderTime(partialTicks, beam.getLevel()) * 2F);
		}else{
			verticalRot = Vector3f.YP.rotationDegrees(45);//Constant 45 degree angle
		}
		VertexConsumer builder = buffer.getBuffer(CRRenderTypes.BEAM_TYPE);

		for(int dir = 0; dir < 6; dir++){
			if(packets[dir] != 0){
				Triple<Color, Integer, Integer> trip = BeamHelper.getTriple(packets[dir]);
				float width = trip.getRight().floatValue() / 8F / (float) Math.sqrt(2);//Convert diagonal radius to side length
				int length = trip.getMiddle();

				matrix.pushPose();
				matrix.mulPose(Direction.from3DDataValue(dir).getRotation());
				matrix.mulPose(verticalRot);
				drawBeam(matrix, builder, length, width, trip.getLeft());

				matrix.popPose();
			}
		}

		matrix.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(T te){
		return true;
	}
}
