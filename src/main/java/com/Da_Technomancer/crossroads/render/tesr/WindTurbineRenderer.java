package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.WindTurbineTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class WindTurbineRenderer implements BlockEntityRenderer<WindTurbineTileEntity>{

	protected WindTurbineRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(WindTurbineTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		LazyOptional<IAxleHandler> axle = te.getCapability(Capabilities.AXLE_CAPABILITY, null);

		if(state.getBlock() != CRBlocks.windTurbine || !axle.isPresent()){
			return;
		}

		int[] bladeCols = te.bladeColors;
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.WINDMILL_TEXTURE);
		VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
		Direction facing = state.getValue(CRProperties.HORIZ_FACING);
		int light = CRRenderUtil.getLightAtPos(te.getLevel(), te.getBlockPos().relative(facing));//Get light from the block in front

		matrix.pushPose();
		matrix.translate(.5F, .5F, .5F);
		matrix.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
		matrix.mulPose(Vector3f.ZP.rotationDegrees((float) RotaryUtil.getCCWSign(facing) * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));

		final float scaleConst = 6F / 5F;

		final float zSt = 0.5F;
		final float zEnWood = zSt + 0.1F * scaleConst;
		final float zEnWool = zSt + 0.05F * scaleConst;
		final float bladeLenSt = 0.5F * scaleConst;
		final float bladeLenEn = 2.5F * scaleConst;
		final float spokeRad = 1F / 8F * scaleConst;
		final float bladeWid = 0.5F * scaleConst + spokeRad;

		final float spokeVSt = 1.5F;
		final float spokeVEn = 2.5F;
		final float bladeUEn = 12;
		final float spokeUEdge = 0.64F;
		final float bladeVSt = 8;
		final float bladeVEn = 11;
		final float bladeVEdge = 8.075F;

		//Center piece (wood)
		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, -bladeLenSt, zEnWood, sprite.getU0(), sprite.getV(4), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, -bladeLenSt, zEnWood, sprite.getU0(), sprite.getV(8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, bladeLenSt, zEnWood, sprite.getU(4), sprite.getV(8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, bladeLenSt, zEnWood, sprite.getU(4), sprite.getV(4), 0, 0, 1, light);

//		//Center piece back (wood)
//		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, -bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(4), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, bladeLenSt, zSt, sprite.getInterpolatedU(4), sprite.getInterpolatedV(4), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, bladeLenSt, zSt, sprite.getInterpolatedU(4), sprite.getInterpolatedV(8), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, -bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(8), 0, 0, 1, light);

		//Blades
		for(int i = 0; i < 4; i++){
			float[] rawCol = DyeColor.values()[bladeCols[i]].getTextureDiffuseColors();
			int[] col = {(int) (rawCol[0] * 255F), (int) (rawCol[1] * 255F), (int) (rawCol[2] * 255F), 255};

//			//Center cap (wood)
//			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.5F, sprite.getMinU(), sprite.getInterpolatedV(4), 0, 1, 0, light);
//			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(4), 0, 1, 0, light);
//			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(8), 0, 1, 0, light);
//			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.5F, sprite.getMinU(), sprite.getInterpolatedV(8), 0, 1, 0, light);

//			//Center cap (wool)
//			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.5F, sprite.getMinU(), sprite.getInterpolatedV(4 + woolTextVOffset), 0, 1, 0, light, col);
//			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(4 + woolTextVOffset), 0, 1, 0, light, col);
//			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(8 + woolTextVOffset), 0, 1, 0, light, col);
//			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.5F, sprite.getMinU(), sprite.getInterpolatedV(8 + woolTextVOffset), 0, 1, 0, light, col);

			//Wood spoke
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenSt, zEnWood, sprite.getU0(), sprite.getV(spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWood, sprite.getU0(), sprite.getV(spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getU(bladeUEn), sprite.getV(spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getU(bladeUEn), sprite.getV(spokeVSt), 0, 0, 1, light);

			//Wood spoke side (left)
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, 0, zSt, sprite.getU0(), sprite.getV(spokeVEn), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, 0, zEnWood, sprite.getU0(), sprite.getV(spokeVSt), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getU1(), sprite.getV(spokeVSt), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getU1(), sprite.getV(spokeVEn), -1, 0, 0, light);

			//Wood spoke side (right)
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, 0, zSt, sprite.getU0(), sprite.getV(spokeVEn), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU1(), sprite.getV(spokeVEn), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getU1(), sprite.getV(spokeVSt), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, 0, zEnWood, sprite.getU0(), sprite.getV(spokeVSt), 1, 0, 0, light);

			//Wood spoke cap
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getU0(), sprite.getV(spokeVSt), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getU(spokeUEdge), sprite.getV(spokeVSt), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getU(spokeUEdge), sprite.getV(spokeVEn), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU0(), sprite.getV(spokeVEn), 0, 1, 0, light);

			//Wool panel
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWool, sprite.getU0(), sprite.getV(bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getU0(), sprite.getV(bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, sprite.getU(bladeUEn), sprite.getV(bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWool, sprite.getU(bladeUEn), sprite.getV(bladeVSt), 0, 0, 1, light, col);

			//Back (wood)
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenSt, zSt, sprite.getU0(), sprite.getV(spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getU(bladeUEn), sprite.getV(spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU(bladeUEn), sprite.getV(spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getU0(), sprite.getV(spokeVEn), 0, 0, 1, light);

			//Back (wool)
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getU0(), sprite.getV(bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU(bladeUEn), sprite.getV(bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getU(bladeUEn), sprite.getV(bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getU0(), sprite.getV(bladeVEn), 0, 0, 1, light, col);

			//Wool blade (side)
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getU0(), sprite.getV(bladeVSt), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getU(bladeUEn), sprite.getV(bladeVSt), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, sprite.getU(bladeUEn), sprite.getV(bladeVEdge), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getU0(), sprite.getV(bladeVEdge), 1, 0, 0, light, col);

			//Wool blade end
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU0(), sprite.getV(bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWool, sprite.getU(spokeUEdge), sprite.getV(bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, sprite.getU(spokeUEdge), sprite.getV(bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getU0(), sprite.getV(bladeVEn), 0, 1, 0, light, col);

			//Wool blade inner end
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getU0(), sprite.getV(bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getU0(), sprite.getV(bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getU(spokeUEdge), sprite.getV(bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWool, sprite.getU(spokeUEdge), sprite.getV(bladeVSt), 0, 1, 0, light, col);

			matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
		}

		matrix.popPose();
	}
}
