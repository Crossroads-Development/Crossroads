package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
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
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
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
		Direction dir = state.get(ESProperties.FACING);
		matrix.rotate(dir.getRotation());

		//Rotate w/ gear angle
		matrix.rotate(Vector3f.YP.rotationDegrees(axle.orElseThrow(NullPointerException::new).getAngle(partialTicks) * dir.getAxisDirection().getOffset()));

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.DRILL_TEXTURE);

		//Applied color, yellow if golden, white otherwise
		int[] col = {255, 255, te.isGolden() ? 38 : 255, 255};

		//Render the te
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());

		//Grid aligned layers
		renderLayer(builder, matrix, -8, 10, sprite, col, combinedLight);
		renderLayer(builder, matrix, -2, 6, sprite, col, combinedLight);
		renderLayer(builder, matrix, 4, 2, sprite, col, combinedLight);

		//45* aligned layers
		matrix.rotate(Vector3f.YP.rotationDegrees(45));
		renderLayer(builder, matrix, -5, 8, sprite, col, combinedLight);
		renderLayer(builder, matrix, 1, 4, sprite, col, combinedLight);
	}

	private static void renderLayer(IVertexBuilder vb, MatrixStack matrix, float bottom, float width, TextureAtlasSprite sprite, int[] color, int light){

		bottom /= 16F;
		float height = 3F / 16F;
		float top = bottom + height;
		float texHeight = 3F;
		float texWidth = width;
		width /= 16F;

		float start = -width / 2;
		float end = width / 2;
		//Top
		vb.pos(matrix.getLast().getMatrix(), start, top, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		vb.pos(matrix.getLast().getMatrix(), start, top, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		vb.pos(matrix.getLast().getMatrix(), end, top, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();
		vb.pos(matrix.getLast().getMatrix(), end, top, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 1, 0).endVertex();

		//Bottom
		vb.pos(matrix.getLast().getMatrix(), start, bottom, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		vb.pos(matrix.getLast().getMatrix(), end, bottom, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		vb.pos(matrix.getLast().getMatrix(), end, bottom, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();
		vb.pos(matrix.getLast().getMatrix(), start, bottom, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, -1, 0).endVertex();

		//side
		vb.pos(matrix.getLast().getMatrix(), start, bottom, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		vb.pos(matrix.getLast().getMatrix(), start, top, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texHeight)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		vb.pos(matrix.getLast().getMatrix(), end, top, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(texHeight)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();
		vb.pos(matrix.getLast().getMatrix(), end, bottom, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texWidth), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, -1).endVertex();

		//side
		vb.pos(start, bottom, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		vb.pos(end, bottom, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texHeight), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		vb.pos(end, top, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texHeight), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();
		vb.pos(start, top, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 0, 0, 1).endVertex();

		//side
		vb.pos(start, bottom, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		vb.pos(start, top, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texHeight), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		vb.pos(start, top, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texHeight), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();
		vb.pos(start, bottom, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), -1, 0, 0).endVertex();

		//side
		vb.pos(end, bottom, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		vb.pos(end, bottom, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(0), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		vb.pos(end, top, start).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texHeight), sprite.getInterpolatedV(texWidth)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
		vb.pos(end, top, end).color(color[0], color[1], color[2], color[3]).tex(sprite.getInterpolatedU(texHeight), sprite.getInterpolatedV(0)).lightmap(light).normal(matrix.getLast().getNormal(), 1, 0, 0).endVertex();
	}
}
