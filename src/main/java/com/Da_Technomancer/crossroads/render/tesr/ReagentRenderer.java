package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.templates.IReagRenderTE;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;
public class ReagentRenderer<T extends BlockEntity & IReagRenderTE> implements BlockEntityRenderer<T>{

	public ReagentRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(T te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		int[][] colors = te.getReagentColorClient();

		int totalPhases = 0;
		for(int[] phaseColor : colors){
			if(phaseColor != null){
				totalPhases += 1;
			}
		}

		if(totalPhases != 0){
			VertexConsumer builder = buffer.getBuffer(RenderType.translucentMovingBlock());
			TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.REAGENT_TEXTURE);
			for(Pair<Vector3f, Vector3f> volume : te.getRenderVolumes()){
				if(volume == null){
					continue;
				}
				Vector3f start = volume.getLeft();
				Vector3f end = volume.getRight();
				float phaseHeight = (end.y - start.y) / totalPhases;
				int phaseIndex = 0;
				for(int i = 0; i < colors.length; i++){
					int[] phaseColor = colors[i];
					if(phaseColor != null){
						int phaseLight = i == EnumMatterPhase.FLAME.ordinal() ? CRRenderUtil.calcMediumLighting(combinedLight) : combinedLight;
						float yEn = end.y - phaseHeight * phaseIndex;
						float ySt = yEn - phaseHeight;
						//Texture uv mapping done to maintain square pixels. Scale of pixels varies widely
						float uSt = sprite.getU(8 * (i%2));
						float vSt = sprite.getV(8 * (i/2));
						float uEn, vEn;
						float width, height;

						//X
						width = end.z - start.z;
						height = yEn - ySt;
						if(width > height){
							uEn = sprite.getU(8 * (i%2) + 8);
							vEn = sprite.getV(8 * (i/2) + (height / width) * 8);
						}else{
							uEn = sprite.getU(8 * (i%2) + (width / height) * 8);
							vEn = sprite.getV(8 * (i/2) + 8);
						}
						//-X
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, ySt, start.z, uSt, vSt, -1, 0, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, ySt, end.z, uEn, vSt, -1, 0, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, yEn, end.z, uEn, vEn, -1, 0, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, yEn, start.z, uSt, vEn, -1, 0, 0, phaseLight, phaseColor);
						//+X
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, ySt, start.z, uSt, vSt, 1, 0, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, yEn, start.z, uSt, vEn, 1, 0, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, yEn, end.z, uEn, vEn, 1, 0, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, ySt, end.z, uEn, vSt, 1, 0, 0, phaseLight, phaseColor);

						//Z
						width = end.x - start.x;
						height = yEn - ySt;
						if(width > height){
							uEn = sprite.getU(8 * (i%2) + 8);
							vEn = sprite.getV(8 * (i/2) + (height / width) * 8);
						}else{
							uEn = sprite.getU(8 * (i%2) + (width / height) * 8);
							vEn = sprite.getV(8 * (i/2) + 8);
						}
						//-Z
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, ySt, start.z, uSt, vSt, 0, 0, -1, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, yEn, start.z, uSt, vEn, 0, 0, -1, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, yEn, start.z, uEn, vEn, 0, 0, -1, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, ySt, start.z, uEn, vSt, 0, 0, -1, phaseLight, phaseColor);
						//+Z
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, ySt, end.z, uSt, vSt, 0, 0, 1, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, ySt, end.z, uEn, vSt, 0, 0, 1, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, yEn, end.z, uEn, vEn, 0, 0, 1, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, yEn, end.z, uSt, vEn, 0, 0, 1, phaseLight, phaseColor);

						//Y
						width = end.x - start.x;
						height = end.z - start.z;
						if(width > height){
							uEn = sprite.getU(8 * (i%2) + 8);
							vEn = sprite.getV(8 * (i/2) + (height / width) * 8);
						}else{
							uEn = sprite.getU(8 * (i%2) + (width / height) * 8);
							vEn = sprite.getV(8 * (i/2) + 8);
						}
						//-Y
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, ySt, start.z, uSt, vSt, 0, -1, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, ySt, start.z, uEn, vSt, 0, -1, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, ySt, end.z, uEn, vEn, 0, -1, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, ySt, end.z, uSt, vEn, 0, -1, 0, phaseLight, phaseColor);
						//+Y
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, yEn, start.z, uSt, vSt, 0, 1, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, start.x, yEn, end.z, uSt, vEn, 0, 1, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, yEn, end.z, uEn, vEn, 0, 1, 0, phaseLight, phaseColor);
						CRRenderUtil.addVertexBlock(builder, matrix, end.x, yEn, start.z, uEn, vSt, 0, 1, 0, phaseLight, phaseColor);

						phaseIndex++;
					}
				}
			}
		}
	}
}
