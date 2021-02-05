package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.FluxSinkTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

public class FluxSinkRenderer extends EntropyRenderer<FluxSinkTileEntity>{

	protected FluxSinkRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(FluxSinkTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		float runtime = te.getRunDuration() + partialTicks;
		if(runtime <= 0){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);

		//Render entropy arcs to the plates
		if(te.renderPortals[0] != -1){
			IVertexBuilder entropyBuilder = buffer.getBuffer(CRRenderTypes.FLUX_TRANSFER_TYPE);
			long worldTime = te.getWorld().getGameTime();
			for(int portalIndex : te.renderPortals){
				if(portalIndex == -1){
					continue;
				}
				matrix.push();
				float[] portalPos = getPortalCenterPos(portalIndex, runtime);
				matrix.rotate(Vector3f.YP.rotation((float) Math.atan2(portalPos[0], portalPos[2])));
				matrix.rotate(Vector3f.XP.rotation((float) (Math.atan2(-portalPos[1], Math.sqrt(portalPos[0] * portalPos[0] + portalPos[2] * portalPos[2])) + Math.PI / 2F)));
				EntropyRenderer.renderArc((float) Math.sqrt(portalPos[0] * portalPos[0] + portalPos[1] * portalPos[1] + portalPos[2] * portalPos[2]), matrix, entropyBuilder, worldTime, partialTicks);
				matrix.pop();
			}
		}

		IVertexBuilder builder = buffer.getBuffer(CRRenderTypes.FLUX_SINK_TYPE);

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//Render an icosahedron

		float scale = (float) Math.min(1D, runtime / FluxSinkTileEntity.STARTUP_TIME) * 2.5F;//Expand slowly to full size when starting up

		int medLight = CRRenderUtil.calcMediumLighting(combinedLight);

		//Ring of plates
		matrix.push();
		matrix.rotate(Vector3f.YP.rotationDegrees(runtime / 10F));
		final float len = 4;
		final float plateScale = Math.min(runtime / (FluxSinkTileEntity.STARTUP_TIME * 2F / 3F), 1) * 0.5F;

		for(int i = 0; i < 8; i++){
			float yOffset = 0.4F * (float) Math.sin(runtime / 100 + i * 5);
			float textVSt = ((i + (int) (runtime / 8)) % 4) * 0.25F;//Animated texture

			builder.pos(matrix.getLast().getMatrix(), len, yOffset - plateScale, -plateScale).color(255, 255, 255, 255).tex(0.75F, textVSt).lightmap(medLight).endVertex();
			builder.pos(matrix.getLast().getMatrix(), len, yOffset + plateScale, -plateScale).color(255, 255, 255, 255).tex(0.75F, textVSt + 0.25F).lightmap(medLight).endVertex();
			builder.pos(matrix.getLast().getMatrix(), len, yOffset + plateScale, plateScale).color(255, 255, 255, 255).tex(1, textVSt + 0.25F).lightmap(medLight).endVertex();
			builder.pos(matrix.getLast().getMatrix(), len, yOffset - plateScale, plateScale).color(255, 255, 255, 255).tex(1, textVSt).lightmap(medLight).endVertex();

			matrix.rotate(Vector3f.YP.rotationDegrees(360F / 8F));
		}

		matrix.pop();

		//Wobble effect
		matrix.rotate(Vector3f.XP.rotationDegrees(8F * (float) Math.sin(runtime / 40D)));
		matrix.rotate(Vector3f.ZP.rotationDegrees(8F * (float) Math.cos(runtime / 40D)));

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

	private static void drawIcos(IVertexBuilder builder, MatrixStack matrix, float scale, float cornerU, float cornerV, int[] col, int light){
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

				builder.pos(matrix.getLast().getMatrix(), 0, largeLen, smallLen).color(col[0], col[1], col[2], col[3]).tex(cornerU, cornerV).lightmap(light).endVertex();
				builder.pos(matrix.getLast().getMatrix(), 0, largeLen, -smallLen).color(col[0], col[1], col[2], col[3]).tex(uEn, vSt).lightmap(light).endVertex();
				builder.pos(matrix.getLast().getMatrix(), largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).tex(uSt, vEn).lightmap(light).endVertex();
				builder.pos(matrix.getLast().getMatrix(), largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).tex(uSt, vEn).lightmap(light).endVertex();//Repeat for triangle

				builder.pos(matrix.getLast().getMatrix(), 0, largeLen, smallLen).color(col[0], col[1], col[2], col[3]).tex(cornerU, cornerV).lightmap(light).endVertex();
				builder.pos(matrix.getLast().getMatrix(), 0, largeLen, -smallLen).color(col[0], col[1], col[2], col[3]).tex(uEn, vSt).lightmap(light).endVertex();
				builder.pos(matrix.getLast().getMatrix(), -largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).tex(uSt, vEn).lightmap(light).endVertex();
				builder.pos(matrix.getLast().getMatrix(), -largeLen, smallLen, 0).color(col[0], col[1], col[2], col[3]).tex(uSt, vEn).lightmap(light).endVertex();//Repeat for triangle

				matrix.rotate(rotationAxis.rotationDegrees(72));
			}
			matrix.rotate(rotationAxis.rotationDegrees(36));
			matrix.rotate(rotationCounterAxis.rotationDegrees(180));
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
