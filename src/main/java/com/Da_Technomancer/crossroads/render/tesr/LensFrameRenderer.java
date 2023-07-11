package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.beams.LensFrameTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class LensFrameRenderer extends BeamRenderer<LensFrameTileEntity> {

	protected LensFrameRenderer(BlockEntityRendererProvider.Context dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(LensFrameTileEntity beam, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		super.render(beam, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		//Render the item in the frame
		BlockState state = beam.getBlockState();

		if(state.getBlock() == CRBlocks.lensFrame){

			ItemStack stack = beam.getLensItem();

			matrix.pushPose();

			if(!stack.isEmpty()){
				ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

				matrix.translate(0.5F, 0.5F, 0.5F);

				Direction.Axis axis = state.getValue(CRProperties.AXIS);
				switch(axis){
					case X:
						matrix.mulPose(Axis.YP.rotationDegrees(90));
						break;
					case Y:
						matrix.mulPose(Axis.XP.rotationDegrees(90));
						break;
				}

				itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, matrix, buffer, beam.getLevel(), (int) beam.getBlockPos().asLong());
			}

			matrix.popPose();
		}
	}
}
