package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ItemCannonTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ItemCannonRenderer implements BlockEntityRenderer<ItemCannonTileEntity>{

	public ItemCannonRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(ItemCannonTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light, int combinedOverlayIn){
		if(te.getBlockState().getBlock() != CRBlocks.itemCannon){
			return;
		}
		matrix.pushPose();
		matrix.translate(0.5, 0.5, 0.5);
		matrix.mulPose(te.getBlockState().getValue(CRProperties.FACING).getRotation());

		TextureAtlasSprite bronzeSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.CAST_IRON_TEXTURE);
		TextureAtlasSprite barrelSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.ITEM_CANNON_BARREL_TEXTURE);

		BeamCannonRenderer.renderCannonModel(matrix, buffer, light, bronzeSprite, barrelSprite, te);

		matrix.popPose();
	}
}
