package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.WindTurbineTileEntity;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class WindTurbineRenderer implements BlockEntityRenderer<WindTurbineTileEntity>{

	protected WindTurbineRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(WindTurbineTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		IAxleHandler axle = te.getAxleHandler(null);

		if(state.getBlock() != CRBlocks.windTurbine || axle == null){
			return;
		}

		int[] bladeCols = te.bladeColors;
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.WINDMILL_TEXTURE);
		VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
		Direction facing = state.getValue(CRProperties.HORIZ_FACING);
		int light = CRRenderUtil.getLightAtPos(te.getLevel(), te.getBlockPos().relative(facing));//Get light from the block in front

		matrix.pushPose();
		matrix.translate(.5F, .5F, .5F);
		matrix.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		matrix.mulPose(Axis.ZP.rotationDegrees((float) RotaryUtil.getCCWSign(facing) * axle.getAngle(partialTicks)));

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
		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, -bladeLenSt, zEnWood, sprite.getU0(), CRRenderUtil.getScaledV(sprite, 4), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, -bladeLenSt, zEnWood, sprite.getU0(), CRRenderUtil.getScaledV(sprite, 8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, bladeLenSt, zEnWood, CRRenderUtil.getScaledU(sprite, 4), CRRenderUtil.getScaledV(sprite, 8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, bladeLenSt, zEnWood, CRRenderUtil.getScaledU(sprite, 4), CRRenderUtil.getScaledV(sprite, 4), 0, 0, 1, light);

//		//Center piece back (wood)
//		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, -bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(4), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, bladeLenSt, zSt, sprite.getInterpolatedU(4), sprite.getInterpolatedV(4), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, bladeLenSt, zSt, sprite.getInterpolatedU(4), sprite.getInterpolatedV(8), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, -bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(8), 0, 0, 1, light);

		//Blades
		for(int i = 0; i < 4; i++){
			int rawCol = DyeColor.values()[bladeCols[i]].getTextureDiffuseColor();
			int[] col = {FastColor.ABGR32.red(rawCol), FastColor.ABGR32.green(rawCol), FastColor.ABGR32.blue(rawCol), 255};

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
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenSt, zEnWood, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWood, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, spokeVSt), 0, 0, 1, light);

			//Wood spoke side (left)
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, 0, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVEn), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, 0, zEnWood, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVSt), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getU1(), CRRenderUtil.getScaledV(sprite, spokeVSt), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getU1(), CRRenderUtil.getScaledV(sprite, spokeVEn), -1, 0, 0, light);

			//Wood spoke side (right)
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, 0, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVEn), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU1(), CRRenderUtil.getScaledV(sprite, spokeVEn), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getU1(), CRRenderUtil.getScaledV(sprite, spokeVSt), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, 0, zEnWood, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVSt), 1, 0, 0, light);

			//Wood spoke cap
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVSt), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, CRRenderUtil.getScaledU(sprite, spokeUEdge), CRRenderUtil.getScaledV(sprite, spokeVSt), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, CRRenderUtil.getScaledU(sprite, spokeUEdge), CRRenderUtil.getScaledV(sprite, spokeVEn), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVEn), 0, 1, 0, light);

			//Wool panel
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWool, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWool, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 0, 1, light, col);

			//Back (wood)
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, spokeVEn), 0, 0, 1, light);

			//Back (wool)
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 0, 1, light, col);

			//Wool blade (side)
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVSt), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, bladeVSt), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, CRRenderUtil.getScaledU(sprite, bladeUEn), CRRenderUtil.getScaledV(sprite, bladeVEdge), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVEdge), 1, 0, 0, light, col);

			//Wool blade end
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWool, CRRenderUtil.getScaledU(sprite, spokeUEdge), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, CRRenderUtil.getScaledU(sprite, spokeUEdge), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 1, 0, light, col);

			//Wool blade inner end
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getU0(), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, CRRenderUtil.getScaledU(sprite, spokeUEdge), CRRenderUtil.getScaledV(sprite, bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWool, CRRenderUtil.getScaledU(sprite, spokeUEdge), CRRenderUtil.getScaledV(sprite, bladeVSt), 0, 1, 0, light, col);

			matrix.mulPose(Axis.ZP.rotationDegrees(90));
		}

		matrix.popPose();
	}

	@Override
	public AABB getRenderBoundingBox(WindTurbineTileEntity te){
		return WindTurbineTileEntity.RENDER_BOX.move(te.getBlockPos());
	}
}
