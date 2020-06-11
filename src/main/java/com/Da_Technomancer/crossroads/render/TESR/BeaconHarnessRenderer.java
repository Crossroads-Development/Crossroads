package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;

public class BeaconHarnessRenderer extends LinkLineRenderer<BeaconHarnessTileEntity>{

	protected BeaconHarnessRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(BeaconHarnessTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		IVertexBuilder beamBuilder = buffer.getBuffer(CRRenderTypes.BEAM_TYPE);

//		Tessellator tes = Tessellator.getInstance();
//		BufferBuilder buf = tes.getBuffer();

		matrix.translate(0.5D, 0.5D, 0.5D);
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
//			GlStateManager.pushMatrix();
//			GlStateManager.pushLightingAttributes();
//			GlStateManager.translated(x + 0.5D, y + 0.5D, z + 0.5D);
//			GlStateManager.color3f(trip.getLeft().getRed() / 255F, trip.getLeft().getGreen() / 255F, trip.getLeft().getBlue() / 255F);
//			GlStateManager.disableLighting();
//			GlStateManager.rotated(180, 1, 0, 0);

//			CRRenderUtil.setBrightLighting();
//			Minecraft.getInstance().getTextureManager().bindTexture(BeamUtil.BEAM_TEXT);

			final float width = trip.getRight().floatValue() / 8F / (float) Math.sqrt(2);//Convert diagonal radius to side length
			final int length = trip.getMiddle();
			final float stOffset = 7F / 16F;
			boolean doRotation = CRConfig.rotateBeam.get();
			Quaternion verticalRot;
			if(doRotation){
				verticalRot = Vector3f.YP.rotationDegrees(CRRenderUtil.getRenderTime(partialTicks, te.getWorld()) * 2F);
			}else{
				verticalRot = Vector3f.YP.rotationDegrees(45);//Constant 45 degree angle
			}
			matrix.rotate(verticalRot);
			matrix.translate(0, stOffset, 0);
			BeamRenderer.drawBeam(matrix, beamBuilder, length, width, trip.getLeft());
			matrix.pop();
//			if(CRConfig.rotateBeam.get()){
//				GlStateManager.rotated((te.getWorld().getGameTime() + partialTicks) * 2F, 0, 1, 0);
//			}
//
//
//			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//			//+Z
//			buf.pos(-rad, length, rad).tex(1, 0).endVertex();
//			buf.pos(-rad, stOffset, rad).tex(1, length - stOffset).endVertex();
//			buf.pos(rad, stOffset, rad).tex(0, length - stOffset).endVertex();
//			buf.pos(rad, length, rad).tex(0, 0).endVertex();
//			//-Z
//			buf.pos(rad, length, -rad).tex(1, 0).endVertex();
//			buf.pos(rad, stOffset, -rad).tex(1, length - stOffset).endVertex();
//			buf.pos(-rad, stOffset, -rad).tex(0, length - stOffset).endVertex();
//			buf.pos(-rad, length, -rad).tex(0, 0).endVertex();
//			//-X
//			buf.pos(-rad, length, -rad).tex(1, 0).endVertex();
//			buf.pos(-rad, stOffset, -rad).tex(1, length - stOffset).endVertex();
//			buf.pos(-rad, stOffset, rad).tex(0, length - stOffset).endVertex();
//			buf.pos(-rad, length, rad).tex(0, 0).endVertex();
//			//+X
//			buf.pos(rad, length, rad).tex(1, 0).endVertex();
//			buf.pos(rad, stOffset, rad).tex(1, length - stOffset).endVertex();
//			buf.pos(rad, stOffset, -rad).tex(0, length - stOffset).endVertex();
//			buf.pos(rad, length, -rad).tex(0, 0).endVertex();
//			tes.draw();
//
//			//Ends
//			Minecraft.getInstance().getTextureManager().bindTexture(BeamUtil.BEAM_END_TEXT);
//			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//
//			//Out end
//			buf.pos(-rad, length, rad).tex(1, 0).endVertex();
//			buf.pos(-rad, length, -rad).tex(1, 1).endVertex();
//			buf.pos(rad, length, -rad).tex(0, 1).endVertex();
//			buf.pos(rad, length, rad).tex(0, 0).endVertex();
//
//			//Start end
//			buf.pos(-rad, stOffset, rad).tex(1, 0).endVertex();
//			buf.pos(-rad, stOffset, -rad).tex(1, 1).endVertex();
//			buf.pos(rad, stOffset, -rad).tex(0, 1).endVertex();
//			buf.pos(rad, stOffset, rad).tex(0, 0).endVertex();
//			tes.draw();
//
//			GlStateManager.color3f(1, 1, 1);
////				CRRenderUtil.restoreLighting(lighting);
//			GlStateManager.enableLighting();
//			GlStateManager.popAttributes();
//			GlStateManager.popMatrix();
		}

		//Revolving rods
//		GlStateManager.pushMatrix();
//		GlStateManager.pushLightingAttributes();
//		GlStateManager.disableLighting();
//		GlStateManager.translated(x + 0.5D, y, z + 0.5D);
//		CRRenderUtil.setMediumLighting();

		float smallOffset = 0.0928F;
		float medOffset = 4F / 16;
		float largeOffset = 6F / 16F;
		int mediumLight = CRRenderUtil.calcMediumLighting(combinedLight);
		TextureAtlasSprite copSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.COPSHOWIUM_TEXTURE);
		TextureAtlasSprite quartzSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.QUARTZ_TEXTURE);

		matrix.rotate(Vector3f.YP.rotationDegrees(angle));
//		GlStateManager.rotated(angle, 0, 1, 0);
//		Minecraft.getInstance().getTextureManager().bindTexture(CRRenderTypes.COPSHOWIUM_TEXTURE);
//		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(matrix, builder, smallOffset, smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, smallOffset, -smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, -smallOffset, -smallOffset, copSprite, mediumLight);
		addRod(matrix, builder, -smallOffset, smallOffset, copSprite, mediumLight);
//		tes.draw();

//		GlStateManager.rotated(-2F * angle, 0, 1, 0);
		matrix.rotate(Vector3f.YP.rotationDegrees(-2F * angle));

//		Minecraft.getInstance().getTextureManager().bindTexture(CRRenderTypes.QUARTZ_TEXTURE);
//		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addRod(matrix, builder, medOffset, largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, medOffset, -largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -medOffset, largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -medOffset, -largeOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, largeOffset, medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, largeOffset, -medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -largeOffset, medOffset, quartzSprite, mediumLight);
		addRod(matrix, builder, -largeOffset, -medOffset, quartzSprite, mediumLight);
//		tes.draw();

//		CRRenderUtil.restoreLighting(prev);
//		GlStateManager.enableLighting();
//		GlStateManager.popAttributes();
//		GlStateManager.popMatrix();
	}

	private static void addRod(MatrixStack matrix, IVertexBuilder builder, float x, float z, TextureAtlasSprite sprite, int light){
		float rad = 1F / 16F;
		float minY = 1F / 16F;
		float maxY = 15F / 16F;
		float uEn = sprite.getInterpolatedU(2F * rad * 16F);

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, uEn, sprite.getMaxV(), 0, 0, -1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, uEn, sprite.getMinV(), 0, 0, -1, light);
//		buf.pos(x - rad, minY, z - rad).tex(0, 0).endVertex();
//		buf.pos(x - rad, maxY, z - rad).tex(0, 1).endVertex();
//		buf.pos(x + rad, maxY, z - rad).tex(2F * rad, 1).endVertex();
//		buf.pos(x + rad, minY, z - rad).tex(2F * rad, 0).endVertex();

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z + rad, sprite.getMinU(), sprite.getMinV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getMinV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getMaxV(), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z + rad, sprite.getMinU(), sprite.getMaxV(), 0, 0, 1, light);
//		buf.pos(x - rad, minY, z + rad).tex(0, 0).endVertex();
//		buf.pos(x + rad, minY, z + rad).tex(2F * rad, 0).endVertex();
//		buf.pos(x + rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
//		buf.pos(x - rad, maxY, z + rad).tex(0, 1).endVertex();

		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, minY, z - rad, uEn, sprite.getMinV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, uEn, sprite.getMaxV(), -1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x - rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), -1, 0, 0, light);
//		buf.pos(x - rad, minY, z - rad).tex(0, 0).endVertex();
//		buf.pos(x - rad, minY, z + rad).tex(2F * rad, 0).endVertex();
//		buf.pos(x - rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
//		buf.pos(x - rad, maxY, z - rad).tex(0, 1).endVertex();

		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z - rad, sprite.getMinU(), sprite.getMinV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z - rad, sprite.getMinU(), sprite.getMaxV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, maxY, z + rad, uEn, sprite.getMaxV(), 1, 0, 0, light);
		CRRenderUtil.addVertexBlock(builder, matrix, x + rad, minY, z + rad, uEn, sprite.getMinV(), 1, 0, 0, light);
//		buf.pos(x + rad, minY, z - rad).tex(0, 0).endVertex();
//		buf.pos(x + rad, maxY, z - rad).tex(0, 1).endVertex();
//		buf.pos(x + rad, maxY, z + rad).tex(2F * rad, 1).endVertex();
//		buf.pos(x + rad, minY, z + rad).tex(2F * rad, 0).endVertex();
	}

	private static float calcAngle(BeaconHarnessTileEntity te, float partialTicks){
		return (float) Math.toDegrees(CRRenderUtil.getRenderTime(partialTicks, te.getWorld()) * (float) Math.PI / 20F);
	}

	@Override
	public boolean isGlobalRenderer(BeaconHarnessTileEntity te){
		return true;
	}
}
