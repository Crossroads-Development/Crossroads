package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.HamsterWheelTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;

public class HamsterWheelRenderer extends TileEntityRenderer<HamsterWheelTileEntity>{

	protected HamsterWheelRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(HamsterWheelTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(!te.getBlockState().func_235901_b_(CRProperties.HORIZ_FACING)){
			return;
		}
		Direction facing = te.getBlockState().get(CRProperties.HORIZ_FACING);

		matrix.translate(0.5D, 0.5D, 0.5D);
		matrix.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle() + 180));
		matrix.rotate(Vector3f.XP.rotationDegrees(90));

		float angle = te.nextAngle - te.angle;
		angle *= partialTicks;
		angle += te.angle;
		angle *= -facing.getAxisDirection().getOffset();

		//Feet
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.HAMSTER_TEXTURE);

		matrix.push();
		matrix.translate(-.2D, -.25D, .30D);
		float peakAngle = 60;
		float degreesPerCycle = 50;
		float feetAngle = Math.abs((4 * peakAngle * Math.abs(angle) / degreesPerCycle) % (4 * peakAngle) - (2 * peakAngle)) - peakAngle;
		float xRad = .025F;
		float yRad = .035F;
		float zRad = .03125F;
		float sideUEn = sprite.getInterpolatedU(8);
		int[] col = {255, 255, 255, 255};

		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++){
				matrix.push();
				matrix.translate(j == 0 ? 0 : .4D, i == 0 ? -.065D : .065D, 0);
				matrix.rotate(Vector3f.YP.rotationDegrees(i + j % 2 == 0 ? feetAngle : -feetAngle));
				
				//Ends
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, -yRad, -zRad, sprite.getMinU(), sprite.getMinV(), 0, -1, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, -yRad, -zRad, sprite.getMaxU(), sprite.getMinV(), 0, -1, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, -yRad, zRad, sprite.getMaxU(), sprite.getMaxV(), 0, -1, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, -yRad, zRad, sprite.getMinU(), sprite.getMaxV(), 0, -1, 0, combinedLight, col);

				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, yRad, zRad, sprite.getMinU(), sprite.getMaxV(), 0, 1, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, yRad, zRad, sprite.getMaxU(), sprite.getMaxV(), 0, 1, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, yRad, -zRad, sprite.getMaxU(), sprite.getMinV(), 0, 1, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, yRad, -zRad, sprite.getMinU(), sprite.getMinV(), 0, 1, 0, combinedLight, col);

				//Sides
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, yRad, -zRad, sprite.getMinU(), sprite.getMaxV(), 0, 0, -1, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, yRad, -zRad, sideUEn, sprite.getMaxV(), 0, 0, -1, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, -yRad, -zRad, sideUEn, sprite.getMinV(), 0, 0, -1, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, -yRad, -zRad, sprite.getMinU(), sprite.getMinV(), 0, 0, -1, combinedLight, col);

				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, -yRad, zRad, sideUEn, sprite.getMinV(), 0, 0, 1, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, -yRad, zRad, sprite.getMinU(), sprite.getMinV(), 0, 0, 1, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, yRad, zRad, sprite.getMinU(), sprite.getMaxV(), 0, 0, 1, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, yRad, zRad, sideUEn, sprite.getMaxV(), 0, 0, 1, combinedLight, col);

				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, -yRad, zRad, sprite.getMinU(), sprite.getMinV(), -1, 0, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, yRad, zRad, sprite.getMinU(), sprite.getMaxV(), -1, 0, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, yRad, -zRad, sideUEn, sprite.getMaxV(), -1, 0, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, -xRad, -yRad, -zRad, sideUEn, sprite.getMinV(), -1, 0, 0, combinedLight, col);

				CRRenderUtil.addVertexBlock(builder, matrix, xRad, yRad, -zRad, sprite.getMinU(), sprite.getMaxV(), 1, 0, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, yRad, zRad, sideUEn, sprite.getMaxV(), 1, 0, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, -yRad, zRad, sideUEn, sprite.getMinV(), 1, 0, 0, combinedLight, col);
				CRRenderUtil.addVertexBlock(builder, matrix, xRad, -yRad, -zRad, sprite.getMinU(), sprite.getMinV(), 1, 0, 0, combinedLight, col);
				
				matrix.pop();
			}
		}
		matrix.pop();

		//Wheel
		matrix.rotate(Vector3f.YP.rotationDegrees(angle));

		//Axle Support
		matrix.push();
		matrix.translate(0, -.4375D, 0);
		matrix.scale(1, .8F, 1);
		matrix.rotate(Vector3f.XP.rotationDegrees(90));
		CRModels.drawAxle(matrix, buffer, combinedLight, GearFactory.findMaterial("iron").getColor());
		matrix.pop();

		float lHalf = .375F;

		for(int i = 0; i < 8; i++){
			matrix.push();
			matrix.rotate(Vector3f.YP.rotationDegrees(45F * (float) i));
			matrix.translate(lHalf, -.25F, 0);
			matrix.scale(.41F, i % 2 == 0 ? .5F : .45F, 7.5F * lHalf);

			CRModels.drawAxle(matrix, buffer, combinedLight, Color.GRAY);
			matrix.pop();
		}
	}
}
