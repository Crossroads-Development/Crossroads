package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.beams.LensFrameTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class LensFrameRenderer extends BeamRenderer<LensFrameTileEntity> {

	protected LensFrameRenderer(BlockEntityRenderDispatcher dispatcher){
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

				Direction.Axis axis = state.getValue(ESProperties.AXIS);
				switch(axis){
					case X:
						matrix.mulPose(Vector3f.YP.rotationDegrees(90));
						break;
					case Y:
						matrix.mulPose(Vector3f.XP.rotationDegrees(90));
						break;
				}

				itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrix, buffer);
			}

			matrix.popPose();
		}
	}
}
