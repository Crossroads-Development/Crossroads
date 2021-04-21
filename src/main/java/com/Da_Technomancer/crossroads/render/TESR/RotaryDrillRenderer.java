package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

public class RotaryDrillRenderer extends TileEntityRenderer<RotaryDrillTileEntity>{

	protected RotaryDrillRenderer(TileEntityRendererDispatcher rendererDispatcherIn){
		super(rendererDispatcherIn);
	}

	@Override
	public void render(RotaryDrillTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		LazyOptional<IAxleHandler> axle = te.getCapability(Capabilities.AXLE_CAPABILITY, null);

		if(!(state.getBlock() instanceof RotaryDrill) || !axle.isPresent()){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);

		//Rotate to face dir
		Direction dir = state.getValue(ESProperties.FACING);
		matrix.mulPose(dir.getRotation());

		//Rotate w/ gear angle
		matrix.mulPose(Vector3f.YP.rotationDegrees(axle.orElseThrow(NullPointerException::new).getAngle(partialTicks) * (float) RotaryUtil.getCCWSign(dir)));

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.DRILL_TEXTURE);

		//Applied color, yellow if golden, white otherwise
		int[] col = {255, 255, te.isGolden() ? 38 : 255, 255};

		//Render the te
		IVertexBuilder builder = buffer.getBuffer(RenderType.solid());

		//Grid aligned layers
		renderLayer(builder, matrix, -8, 10, sprite, col, combinedLight);
		renderLayer(builder, matrix, -2, 6, sprite, col, combinedLight);
		renderLayer(builder, matrix, 4, 2, sprite, col, combinedLight);

		//45* aligned layers
		matrix.mulPose(Vector3f.YP.rotationDegrees(45));
		renderLayer(builder, matrix, -5, 8, sprite, col, combinedLight);
		renderLayer(builder, matrix, 1, 4, sprite, col, combinedLight);
	}

	private static void renderLayer(IVertexBuilder builder, MatrixStack matrix, float bottom, float width, TextureAtlasSprite sprite, int[] color, int light){

		bottom /= 16F;
		float height = 3F / 16F;
		float top = bottom + height;
		float texHeight = 3F;
		float texWidth = width;
		width /= 16F;

		float start = -width / 2;
		float end = width / 2;

		float uSt = sprite.getU0();
		float uEn = sprite.getU(texWidth);
		float uHe = sprite.getU(texHeight);
		float vSt = sprite.getV0();
		float vEn = sprite.getV(texWidth);
//		float vHe = sprite.getInterpolatedV(texHeight);

		//Top
		CRRenderUtil.addVertexBlock(builder, matrix, start, top, start, uSt, vSt, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, top, end, uSt, vEn, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, top, end, uEn, vEn, 0, 1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, top, start, uEn, vSt, 0, 1, 0, light, color);

		//Bottom
		CRRenderUtil.addVertexBlock(builder, matrix, start, bottom, start, uSt, vSt, 0, -1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, bottom, start, uEn, vSt, 0, -1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, bottom, end, uEn, vEn, 0, -1, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, bottom, end, uSt, vEn, 0, -1, 0, light, color);
		
		//side
		CRRenderUtil.addVertexBlock(builder, matrix, start, bottom, start, uSt, vSt, 0, 0, -1, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, top, start, uHe, vSt, 0, 0, -1, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, top, start, uHe, vEn, 0, 0, -1, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, bottom, start, uSt, vEn, 0, 0, -1, light, color);

		//side
		CRRenderUtil.addVertexBlock(builder, matrix, start, bottom, end, uSt, vEn, 0, 0, 1, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, bottom, end, uSt, vSt, 0, 0, 1, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, top, end, uHe, vSt, 0, 0, 1, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, top, end, uHe, vEn, 0, 0, 1, light, color);

		//side
		CRRenderUtil.addVertexBlock(builder, matrix, start, bottom, end, uSt, vEn, -1, 0, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, top, end, uHe, vEn, -1, 0, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, top, start, uHe, vSt, -1, 0, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, start, bottom, start, uSt, vSt, -1, 0, 0, light, color);

		//side
		CRRenderUtil.addVertexBlock(builder, matrix, end, bottom, end, uSt, vSt, 1, 0, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, bottom, start, uSt, vEn, 1, 0, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, top, start, uHe, vEn, 1, 0, 0, light, color);
		CRRenderUtil.addVertexBlock(builder, matrix, end, top, end, uHe, vSt, 1, 0, 0, light, color);
	}
}
