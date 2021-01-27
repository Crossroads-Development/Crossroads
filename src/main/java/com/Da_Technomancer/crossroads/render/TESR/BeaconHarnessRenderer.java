package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;

public class BeaconHarnessRenderer extends EntropyRenderer<BeaconHarnessTileEntity>{

	protected BeaconHarnessRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(BeaconHarnessTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		IVertexBuilder beamBuilder = buffer.getBuffer(CRRenderTypes.BEAM_TYPE);

		matrix.translate(0.5D, 0, 0.5D);
		float angle = 0;

		//Render output beam
		int[] beamPacket = te.getRenderedBeams();
		//Beacon harness only outputs beams down
		Triple<Color, Integer, Integer> trip = BeamManager.getTriple(beamPacket[0]);
		if(trip.getRight() != 0){
			//We are running. Calculate angle for rods
			angle = calcAngle(te, partialTicks);

			matrix.push();
			matrix.rotate(Vector3f.XP.rotationDegrees(180));

			final float width = trip.getRight().floatValue() / 8F / (float) Math.sqrt(2);//Convert diagonal radius to side length
			final int length = trip.getMiddle();
			boolean doRotation = CRConfig.rotateBeam.get();
			Quaternion verticalRot;
			if(doRotation){
				verticalRot = Vector3f.YP.rotationDegrees(CRRenderUtil.getRenderTime(partialTicks, te.getWorld()) * 2F);
			}else{
				verticalRot = Vector3f.YP.rotationDegrees(45);//Constant 45 degree angle
			}
			matrix.translate(0, -0.5D, 0);
			matrix.rotate(verticalRot);
			BeamRenderer.drawBeam(matrix, beamBuilder, length, width, trip.getLeft());
			matrix.pop();
		}

		//Revolving rods

		float smallOffset = 0.0928F;
		float medOffset = 4F / 16;
		float largeOffset = 6F / 16F;
		int mediumLight = CRRenderUtil.calcMediumLighting(combinedLight);
		TextureAtlasSprite copSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.COPSHOWIUM_TEXTURE);
		TextureAtlasSprite quartzSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.QUARTZ_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());

		matrix.rotate(Vector3f.YP.rotationDegrees(angle));
		addRod(matrix, builder, smallOffset, smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, smallOffset, -smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, -smallOffset, -smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, -smallOffset, smallOffset, copSprite, mediumLight);

		matrix.rotate(Vector3f.YP.rotationDegrees(-2F * angle));

		addRod(matrix, builder, medOffset, largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, medOffset, -largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -medOffset, largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -medOffset, -largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, largeOffset, medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, largeOffset, -medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -largeOffset, medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -largeOffset, -medOffset, quartzSprite, mediumLight);
	}

	private static void addRod(MatrixStack matrix, IVertexBuilder builder, float x, float z, TextureAtlasSprite sprite, int light){
		float rad = 1F / 16F;
		float minY = 1F / 16F;
		float maxY = 15F / 16F;
		float uEn = sprite.getInterpolatedU(2F * rad * 16F);
		float vEn = sprite.getInterpolatedV(2F * rad * 16F);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, uEn, sprite.getMaxV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, uEn, sprite.getMinV(), 0, 0, -1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, sprite.getMinU(), sprite.getMinV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getMinV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getMaxV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, sprite.getMinU(), sprite.getMaxV(), 0, 0, 1, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, uEn, sprite.getMinV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, uEn, sprite.getMaxV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), -1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getMaxV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getMinV(), 1, 0, 0, light);

		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, sprite.getMinU(), sprite.getMinV(), 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, uEn, sprite.getMinV(), 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, uEn, vEn, 0, -1, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, sprite.getMinU(), vEn, 0, -1, 0, light);
	}

	private static float calcAngle(BeaconHarnessTileEntity te, float partialTicks){
		return (float) Math.toDegrees(CRRenderUtil.getRenderTime(partialTicks, te.getWorld()) * (float) Math.PI / 20F);
	}

	@Override
	public boolean isGlobalRenderer(BeaconHarnessTileEntity te){
		return true;
	}
}
