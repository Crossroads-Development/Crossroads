package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamHelper;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Quaternionf;

import java.awt.*;

public class BeaconHarnessRenderer extends EntropyRenderer<BeaconHarnessTileEntity>{

	protected BeaconHarnessRenderer(BlockEntityRendererProvider.Context dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(BeaconHarnessTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		VertexConsumer beamBuilder = buffer.getBuffer(CRRenderTypes.BEAM_TYPE);

		matrix.translate(0.5D, 0, 0.5D);
		float angle = 0;

		//Render output beam
		int[] beamPacket = te.getRenderedBeams();
		//Beacon harness only outputs beams down
		Triple<Color, Integer, Integer> trip = BeamHelper.getTriple(beamPacket[0]);
		if(trip.getRight() != 0){
			//We are running. Calculate angle for rods
			angle = calcAngle(te, partialTicks);

			matrix.pushPose();
			matrix.mulPose(Axis.XP.rotationDegrees(180));

			final float width = trip.getRight().floatValue() / 8F / (float) Math.sqrt(2);//Convert diagonal radius to side length
			final int length = trip.getMiddle();
			boolean doRotation = CRConfig.rotateBeam.get();
			Quaternionf verticalRot;
			if(doRotation){
				verticalRot = Axis.YP.rotationDegrees(CRRenderUtil.getRenderTime(partialTicks, te.getLevel()) * 2F);
			}else{
				verticalRot = Axis.YP.rotationDegrees(45);//Constant 45 degree angle
			}
			matrix.translate(0, -0.5D, 0);
			matrix.mulPose(verticalRot);
			BeamRenderer.drawBeam(matrix, beamBuilder, length, width, trip.getLeft());
			matrix.popPose();
		}

		//Revolving rods

		float smallOffset = 0.0928F;
		float medOffset = 4F / 16;
		float largeOffset = 6F / 16F;
		int mediumLight = CRRenderUtil.calcMediumLighting(combinedLight);
		TextureAtlasSprite copSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.COPSHOWIUM_TEXTURE);
		TextureAtlasSprite quartzSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.QUARTZ_TEXTURE);
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());

		matrix.mulPose(Axis.YP.rotationDegrees(angle));
		addRod(matrix, builder, smallOffset, smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, smallOffset, -smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, -smallOffset, -smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, -smallOffset, smallOffset, copSprite, mediumLight);

		matrix.mulPose(Axis.YP.rotationDegrees(-2F * angle));

		addRod(matrix, builder, medOffset, largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, medOffset, -largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -medOffset, largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -medOffset, -largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, largeOffset, medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, largeOffset, -medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -largeOffset, medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -largeOffset, -medOffset, quartzSprite, mediumLight);
	}

	private static void addRod(PoseStack matrix, VertexConsumer builder, float x, float z, TextureAtlasSprite sprite, int light){
		float rad = 1F / 16F;
		float minY = 1F / 16F;
		float maxY = 15F / 16F;
		float uEn = sprite.getU(2F * rad * 16F);
		float vEn = sprite.getV(2F * rad * 16F);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getU0(), sprite.getV0(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getU0(), sprite.getV1(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, uEn, sprite.getV1(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, uEn, sprite.getV0(), 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, sprite.getU0(), sprite.getV0(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getV0(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getV1(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, sprite.getU0(), sprite.getV1(), 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getU0(), sprite.getV0(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, uEn, sprite.getV0(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, uEn, sprite.getV1(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getU0(), sprite.getV1(), -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, sprite.getU0(), sprite.getV0(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, sprite.getU0(), sprite.getV1(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getV1(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getV0(), 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, sprite.getU0(), sprite.getV0(), 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, uEn, sprite.getV0(), 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, uEn, vEn, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, sprite.getU0(), vEn, 0, -1, 0, light);
	}

	private static float calcAngle(BeaconHarnessTileEntity te, float partialTicks){
		return (float) Math.toDegrees(CRRenderUtil.getRenderTime(partialTicks, te.getLevel()) * (float) Math.PI / 20F);
	}

	@Override
	public boolean shouldRenderOffScreen(BeaconHarnessTileEntity te){
		return true;
	}
}
