package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.beams.LensFrameTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sun.javafx.geom.Vec3f;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class LensFrameRenderer extends BeamRenderer<LensFrameTileEntity> {

	protected LensFrameRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(LensFrameTileEntity beam, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		super.render(beam, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

		//Render the item in the frame
		BlockState state = beam.getBlockState();

		if(state.getBlock() == CRBlocks.lensFrame){

			ItemStack stack = beam.getItem(0);

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

				itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrix, buffer);
			}

			matrix.popPose();
		}
	}
}
