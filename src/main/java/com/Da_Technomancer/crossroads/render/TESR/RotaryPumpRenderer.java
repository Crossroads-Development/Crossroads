package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;

import java.awt.*;

public class RotaryPumpRenderer extends TileEntityRenderer<RotaryPumpTileEntity>{

	protected RotaryPumpRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(RotaryPumpTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		//Render the screw
		matrix.push();
		matrix.translate(0.5D, 0.5D, 0.5D);
		LazyOptional<IAxleHandler> opt = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(opt.isPresent()){
			matrix.rotate(Vector3f.YP.rotationDegrees(opt.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		}
		CRModels.renderScrew(matrix, buffer, combinedLight);
		matrix.pop();

		//Render the liquid
		if(te.getCompletion() != 0){
			BlockPos fPos = te.getPos().offset(Direction.DOWN);
			IFluidState state = te.getWorld().getFluidState(fPos);
			TextureAtlasSprite lText;
			Color fCol;
			if(!state.isEmpty() && state.isSource()){
				ResourceLocation textLoc = state.getFluid().getAttributes().getStillTexture();
				lText = CRRenderUtil.getTextureSprite(textLoc);
				fCol = new Color(state.getFluid().getAttributes().getColor(te.getWorld(), fPos));
			}else{
				return;
			}

			int[] cols = {fCol.getRed(), fCol.getGreen(), fCol.getBlue(), fCol.getAlpha()};

			float xSt = 3F / 16F;
			float ySt = 0;
			float zSt = 3F / 16F;
			float xEn = 13F / 16F;
			float yEn = 7F / 16F * te.getCompletion();
			float zEn = 13F / 16F;
			float uSt = lText.getInterpolatedU(xSt * 16);
			float uEn = lText.getInterpolatedU(xEn * 16);
			float vSt = lText.getInterpolatedV(16 - (ySt * 16));
			float vEn = lText.getInterpolatedV(16 - (yEn * 16));

			//Draw liquid layer
			IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
			
			builder.pos(matrix.getLast().getMatrix(), xEn, ySt, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, ySt, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, yEn, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, yEn, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
//			vb.pos(xEn, ySt, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xSt, ySt, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
//			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			builder.pos(matrix.getLast().getMatrix(), xSt, ySt, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, ySt, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, yEn, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, yEn, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
//			vb.pos(xSt, ySt, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xEn, ySt, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
//			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			builder.pos(matrix.getLast().getMatrix(), xEn, ySt, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, ySt, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, yEn, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, yEn, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
//			vb.pos(xEn, ySt, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xEn, ySt, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
//			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			builder.pos(matrix.getLast().getMatrix(), xSt, ySt, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, ySt, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, yEn, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, yEn, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
//			vb.pos(xSt, ySt, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xSt, ySt, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
//			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
//			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			builder.pos(matrix.getLast().getMatrix(), xEn, yEn, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, yEn, zSt).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vSt).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xSt, yEn, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uSt, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
			builder.pos(matrix.getLast().getMatrix(), xEn, yEn, zEn).color(cols[0], cols[1], cols[2], cols[3]).tex(uEn, vEn).lightmap(combinedLight).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
//			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (zSt * 16))).endVertex();
//			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (zSt * 16))).endVertex();
//			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (zEn * 16))).endVertex();
//			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (zEn * 16))).endVertex();
		}
	}
}
