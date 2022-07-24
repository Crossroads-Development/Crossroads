package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.fluid.RotaryPumpTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

public class RotaryPumpRenderer implements BlockEntityRenderer<RotaryPumpTileEntity>{

	protected RotaryPumpRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(RotaryPumpTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		//Render the screw
		matrix.pushPose();
		matrix.translate(0.5D, 0, 0.5D);
		LazyOptional<IAxleHandler> opt = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(opt.isPresent()){
			matrix.mulPose(Vector3f.YP.rotationDegrees(opt.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		}
		CRModels.renderScrew(matrix, buffer, combinedLight);
		matrix.popPose();

		//Render the liquid
		if(te.getCompletion() != 0){
			BlockPos fPos = te.getBlockPos().relative(Direction.DOWN);
			FluidStack pumpedFluid = RotaryPumpTileEntity.getFluidFromBlock(te.getLevel().getBlockState(fPos), te.getLevel(), fPos);
			TextureAtlasSprite lText;
			Color fCol;
			if(!pumpedFluid.isEmpty()){
				IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(pumpedFluid.getFluid());
				ResourceLocation textLoc = renderProps.getStillTexture();
				lText = CRRenderUtil.getTextureSprite(textLoc);
				fCol = new Color(renderProps.getTintColor(te.getLevel().getFluidState(fPos), te.getLevel(), fPos));
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
			float uSt = lText.getU(xSt * 16);
			float uEn = lText.getU(xEn * 16);
			float vSt = lText.getV(16 - (ySt * 16));
			float vEn = lText.getV(16 - (yEn * 16));

			//Draw liquid layer
			VertexConsumer builder = buffer.getBuffer(RenderType.translucentNoCrumbling());

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
		}
	}
}
