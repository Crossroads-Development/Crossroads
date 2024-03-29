package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.util.LazyOptional;
import org.joml.Quaternionf;

import java.awt.*;

public class RotaryPumpRenderer implements BlockEntityRenderer<RotaryPumpTileEntity>{

	protected RotaryPumpRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(RotaryPumpTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		//Render the screw
		matrix.pushPose();
		matrix.translate(0.5D, 0.5D, 0.5D);
		LazyOptional<IAxleHandler> opt = te.getCapability(Capabilities.AXLE_CAPABILITY, null);

		double screwOffset = 0.1D;
		float screwScale = 0.45F;
		for(int i = 0; i < 2; i++){
			matrix.pushPose();
			matrix.translate((2 * i - 1) * screwOffset, -0.25D, 0);
			if(opt.isPresent()){
				matrix.mulPose(Axis.YP.rotationDegrees((1F - 2F * i) * opt.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
			}
			//Draw central axle
			CRModels.drawAxle(matrix, buffer, combinedLight, CRMaterialLibrary.findMaterial("iron").getColor());

			//Draw screw threads
			matrix.scale(screwScale, screwScale, screwScale);
			TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);

			VertexConsumer vb = buffer.getBuffer(RenderType.solid());

			Quaternionf rotation = Axis.YP.rotationDegrees(90 * (2F*i - 1F));
			Quaternionf rotationToReverseStupidHardCodedBladeAngle = null;
			if(i == 1){
				rotationToReverseStupidHardCodedBladeAngle = Axis.ZP.rotation((float) Math.atan(1F / 3F));
			}
			float screwDensity = 30;
			for(int j = 0; j < 1.5 * screwDensity; j++){
				matrix.pushPose();
				matrix.translate(0, (j - screwDensity / 2) / screwDensity - 0.5F + i * 2F / screwDensity, 0);
				if(i == 1){
					matrix.mulPose(rotationToReverseStupidHardCodedBladeAngle);
				}
				CRModels.drawTurbineBlade(vb, matrix, -1F / 16F, combinedLight, sprite);
				matrix.popPose();
				matrix.mulPose(rotation);
			}

			matrix.popPose();
		}
		matrix.popPose();

		//Render the liquid
		float completion = te.getCompletion(partialTicks);
		if(completion != 0 && te.getActiveTexture() != null){
			TextureAtlasSprite lText = CRRenderUtil.getTextureSprite(te.getActiveTexture());
			Color fCol = te.getCol();

			int[] cols = {fCol.getRed(), fCol.getGreen(), fCol.getBlue(), fCol.getAlpha()};

			float xSt = 4F / 16F;
			float ySt;
			float zSt = 6F / 16F;
			float xEn = 12F / 16F;
			float yEn;
			float zEn = 10F / 16F;
			float uSt = lText.getU(xSt * 16);
			float uEn = lText.getU(xEn * 16);
			float vSt;
			float vEn;
			if(completion > 0){
				//Pumping up
				ySt = -0.25F;
				yEn = (8F / 16F - ySt) * completion + ySt;
				vSt = lText.getV(16);
				vEn = lText.getV(16 - (yEn - ySt) * 16);
			}else{
				//Pumping down
				completion = -completion;//Now ranges from 0 to +1
				yEn = 8F / 16F;
				ySt = (-0.25F - yEn) * completion + yEn;
				float vEnCoord = 16 - (yEn - -0.25F) * 16;
				vSt = lText.getV((16 - vEnCoord) * completion + vEnCoord);
				vEn = lText.getV(vEnCoord);
			}

			//Draw liquid layer
//			VertexConsumer builder = buffer.getBuffer(RenderType.translucentNoCrumbling());
			//Not really sure why this works, but it lets the fluid be rendered through translucent models
			VertexConsumer builder = buffer.getBuffer(CRRenderTypes.STABLE_TRANSLUCENT_TYPE);

			CRRenderUtil.addVertexBlock(builder, matrix, xEn, ySt, zSt, uEn, vSt, 0, 0, -1, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, ySt, zSt, uSt, vSt, 0, 0, -1, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, yEn, zSt, uSt, vEn, 0, 0, -1, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, yEn, zSt, uEn, vEn, 0, 0, -1, combinedLight, cols);

			CRRenderUtil.addVertexBlock(builder, matrix, xSt, ySt, zEn, uSt, vSt, 0, 0, 1, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, ySt, zEn, uEn, vSt, 0, 0, 1, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, yEn, zEn, uEn, vEn, 0, 0, 1, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, yEn, zEn, uSt, vEn, 0, 0, 1, combinedLight, cols);

			CRRenderUtil.addVertexBlock(builder, matrix, xSt, ySt, zSt, uSt, vSt, -1, 0, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, ySt, zEn, uEn, vSt, -1, 0, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, yEn, zEn, uEn, vEn, -1, 0, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, yEn, zSt, uSt, vEn, -1, 0, 0, combinedLight, cols);

			CRRenderUtil.addVertexBlock(builder, matrix, xEn, yEn, zSt, uEn, vSt, 1, 0, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, yEn, zEn, uEn, vEn, 1, 0, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, ySt, zEn, uSt, vEn, 1, 0, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, ySt, zSt, uSt, vSt, 1, 0, 0, combinedLight, cols);

			CRRenderUtil.addVertexBlock(builder, matrix, xSt, yEn, zSt, lText.getU0(), lText.getV0(), 0, 1, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, yEn, zEn, lText.getU0(), lText.getV1(), 0, 1, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, yEn, zEn, lText.getU1(), lText.getV1(), 0, 1, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, yEn, zSt, lText.getU1(), lText.getV0(), 0, 1, 0, combinedLight, cols);

			CRRenderUtil.addVertexBlock(builder, matrix, xSt, ySt, zSt, lText.getU0(), lText.getV0(), 0, -1, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, ySt, zSt, lText.getU1(), lText.getV0(), 0, -1, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xEn, ySt, zEn, lText.getU1(), lText.getV1(), 0, -1, 0, combinedLight, cols);
			CRRenderUtil.addVertexBlock(builder, matrix, xSt, ySt, zEn, lText.getU0(), lText.getV1(), 0, -1, 0, combinedLight, cols);
		}
	}
}
