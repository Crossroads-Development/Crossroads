package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.technomancy.ChunkAcceleratorTileEntity;
import com.Da_Technomancer.crossroads.items.item_sets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkAcceleratorRenderer extends EntropyRenderer<ChunkAcceleratorTileEntity>{


	protected ChunkAcceleratorRenderer(BlockEntityRendererProvider.Context dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(ChunkAcceleratorTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.chunkAccelerator){
			return;
		}
		super.render(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		matrix.translate(0.5D, 0.5D, 0.5D);

		//Render the two rotating octagons
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TEXTURE);
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());
		int[] col = CRRenderUtil.convertColor(GearFactory.findMaterial("copshowium").getColor());
		float angle = CRRenderUtil.getRenderTime(partialTicks, te.getLevel());
		float lHalf = 7F / 16F;//Half the side length of the octagon
		int medLight = CRRenderUtil.calcMediumLighting(combinedLight);

		matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
		matrix.translate(0, 5F / 16F, 0);
		matrix.scale(2F * lHalf * 0.8F, 1, 2F * lHalf * 0.8F);
		CRModels.draw8Core(builder, matrix, col, medLight, sprite);

		matrix.mulPose(Vector3f.YP.rotationDegrees(-2F * angle));
		matrix.translate(0, -10F / 16F, 0);
		CRModels.draw8Core(builder, matrix, col, medLight, sprite);
	}
}
