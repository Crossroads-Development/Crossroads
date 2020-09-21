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
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
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

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.WINDMILL_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());
		Direction facing = state.get(CRProperties.HORIZ_FACING);
		int light = CRRenderUtil.getLightAtPos(te.getWorld(), te.getPos().offset(facing));//Get light from the block in front

		matrix.push();
		matrix.translate(.5F, .5F, .5F);
		matrix.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
		matrix.rotate(Vector3f.ZP.rotationDegrees((float) RotaryUtil.getCCWSign(facing) * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));

		//Center piece
		CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, -0.25F, 0.6F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(4), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, -0.25F, 0.6F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(4), sprite.getInterpolatedV(8), 0, 0, 1, light);
		CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(4), sprite.getInterpolatedV(4), 0, 0, 1, light);

		//Blades
		for(int i = 0; i < 4; i++){
			//Center cap
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(4), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(4), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(8), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(8), 0, 1, 0, light);

			//Wood spoke
			CRRenderUtil.addVertexBlock(builder, matrix, -0.0625F, 0.25F, 0.6F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(1.5F), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.0625F, 0.25F, 0.6F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(2.5F), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.0625F, 2, 0.6F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(2.5F), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.0625F, 2, 0.6F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(1.5F), 0, 0, 1, light);

			//Wool panel
			Vector3d normal = CRRenderUtil.findNormal(new Vector3d(-0.0625D, 0.25D, 0.6D), new Vector3d(-.25D, 0.25D, 0.5D), new Vector3d(-0.0625D, 2, 0.6D));
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(0), (float) normal.x, (float) normal.y, (float) normal.z, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.0625F, 0.25F, 0.6F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(1.5D), (float) normal.x, (float) normal.y, (float) normal.z, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.0625F, 2, 0.6F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(1.5D), (float) normal.x, (float) normal.y, (float) normal.z, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 2, 0.5F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(0), (float) normal.x, (float) normal.y, (float) normal.z, light);

			//Wool panel
			normal = CRRenderUtil.findNormal(new Vector3d(.25D, 0.25D, 0.5D), new Vector3d(0.0625D, 0.25D, 0.6D), new Vector3d(.25D, 2, 0.5D));
			CRRenderUtil.addVertexBlock(builder, matrix, 0.0625F, 0.25F, 0.6F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(0), (float) normal.x, (float) normal.y, (float) normal.z, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(1.5D), (float) normal.x, (float) normal.y, (float) normal.z, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 2, 0.5F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(1.5D), (float) normal.x, (float) normal.y, (float) normal.z, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.0625F, 2, 0.6F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(0), (float) normal.x, (float) normal.y, (float) normal.z, light);

			//Back
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 0.25F, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(0), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 2, 0.5F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(0), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 2, 0.5F, sprite.getInterpolatedU(12), sprite.getInterpolatedV(4), 0, 0, 1, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 0.25F, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(4), 0, 0, 1, light);

			//End cap
			CRRenderUtil.addVertexBlock(builder, matrix, -0.25F, 2, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(8), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, -0.0625F, 2, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(6.5D), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.0625F, 2, 0.6F, sprite.getInterpolatedU(0.8D), sprite.getInterpolatedV(5.5D), 0, 1, 0, light);
			CRRenderUtil.addVertexBlock(builder, matrix, 0.25F, 2, 0.5F, sprite.getInterpolatedU(0), sprite.getInterpolatedV(4), 0, 1, 0, light);

			matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		}

		matrix.pop();
	}
}
