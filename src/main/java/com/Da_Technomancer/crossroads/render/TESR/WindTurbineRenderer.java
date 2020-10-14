package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindTurbineTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

public class WindTurbineRenderer extends TileEntityRenderer<WindTurbineTileEntity>{

	protected WindTurbineRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(WindTurbineTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		LazyOptional<IAxleHandler> axle = te.getCapability(Capabilities.AXLE_CAPABILITY, null);

		if(state.getBlock() != CRBlocks.windTurbine || !axle.isPresent()){
			return;
		}

		int[] bladeCols = te.bladeColors;
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.WINDMILL_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());
		Direction facing = state.get(CRProperties.HORIZ_FACING);
		int light = CRRenderUtil.getLightAtPos(te.getWorld(), te.getPos().offset(facing));//Get light from the block in front

		matrix.push();
		matrix.translate(.5F, .5F, .5F);
		matrix.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
		matrix.rotate(Vector3f.ZP.rotationDegrees((float) RotaryUtil.getCCWSign(facing) * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));

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
		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, -bladeLenSt, zEnWood, sprite.getMinU(), sprite.getInterpolatedV(4), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, -bladeLenSt, zEnWood, sprite.getMinU(), sprite.getInterpolatedV(8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, bladeLenSt, zEnWood, sprite.getInterpolatedU(4), sprite.getInterpolatedV(8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, bladeLenSt, zEnWood, sprite.getInterpolatedU(4), sprite.getInterpolatedV(4), 0, 0, 1, light);

//		//Center piece back (wood)
//		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, -bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(4), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, -bladeLenSt, bladeLenSt, zSt, sprite.getInterpolatedU(4), sprite.getInterpolatedV(4), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, bladeLenSt, zSt, sprite.getInterpolatedU(4), sprite.getInterpolatedV(8), 0, 0, 1, light);
//		CRRenderUtil.addVertexBlock(builder, matrix, bladeLenSt, -bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(8), 0, 0, 1, light);

		//Blades
		for(int i = 0; i < 4; i++){
			float[] rawCol = DyeColor.values()[bladeCols[i]].getColorComponentValues();
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
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenSt, zEnWood, sprite.getMinU(), sprite.getInterpolatedV(spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWood, sprite.getMinU(), sprite.getInterpolatedV(spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(spokeVSt), 0, 0, 1, light);

			//Wood spoke side (left)
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, 0, zSt, sprite.getMinU(), sprite.getInterpolatedV(spokeVEn), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, 0, zEnWood, sprite.getMinU(), sprite.getInterpolatedV(spokeVSt), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getMaxU(), sprite.getInterpolatedV(spokeVSt), -1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getMaxU(), sprite.getInterpolatedV(spokeVEn), -1, 0, 0, light);

			//Wood spoke side (right)
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, 0, zSt, sprite.getMinU(), sprite.getInterpolatedV(spokeVEn), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getMaxU(), sprite.getInterpolatedV(spokeVEn), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getMaxU(), sprite.getInterpolatedV(spokeVSt), 1, 0, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, 0, zEnWood, sprite.getMinU(), sprite.getInterpolatedV(spokeVSt), 1, 0, 0, light);

			//Wood spoke cap
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getMinU(), sprite.getInterpolatedV(spokeVSt), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zEnWood, sprite.getInterpolatedU(spokeUEdge), sprite.getInterpolatedV(spokeVSt), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWood, sprite.getInterpolatedU(spokeUEdge), sprite.getInterpolatedV(spokeVEn), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getMinU(), sprite.getInterpolatedV(spokeVEn), 0, 1, 0, light);

			//Wool panel
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWool, sprite.getMinU(), sprite.getInterpolatedV(bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getMinU(), sprite.getInterpolatedV(bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWool, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(bladeVSt), 0, 0, 1, light, col);

			//Back (wood)
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -spokeRad, bladeLenEn, zSt, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(spokeVSt), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(spokeVEn), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(spokeVEn), 0, 0, 1, light);

			//Back (wool)
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(bladeVSt), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(bladeVEn), 0, 0, 1, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVEn), 0, 0, 1, light, col);

			//Wool blade (side)
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVSt), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(bladeVSt), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, sprite.getInterpolatedU(bladeUEn), sprite.getInterpolatedV(bladeVEdge), 1, 0, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getMinU(), sprite.getInterpolatedV(bladeVEdge), 1, 0, 0, light, col);

			//Wool blade end
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenEn, zEnWool, sprite.getInterpolatedU(spokeUEdge), sprite.getInterpolatedV(bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zEnWool, sprite.getInterpolatedU(spokeUEdge), sprite.getInterpolatedV(bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenEn, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVEn), 0, 1, 0, light, col);

			//Wool blade inner end
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVSt), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zSt, sprite.getMinU(), sprite.getInterpolatedV(bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, bladeWid, bladeLenSt, zEnWool, sprite.getInterpolatedU(spokeUEdge), sprite.getInterpolatedV(bladeVEn), 0, 1, 0, light, col);
			CRRenderUtil.addVertexBlock(builder, matrix, spokeRad, bladeLenSt, zEnWool, sprite.getInterpolatedU(spokeUEdge), sprite.getInterpolatedV(bladeVSt), 0, 1, 0, light, col);

			matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		}

		matrix.pop();
	}
}
