package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismLargeGearCore;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismLargeGearEdge;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class MechanismRenderer implements BlockEntityRenderer<MechanismTileEntity>{

	protected MechanismRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(MechanismTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		matrix.translate(0.5D, 0.5D, 0.5D);

		for(int i = 0; i < 7; i++){
			if(te.members[i] != null){
				matrix.pushPose();
				te.members[i].doRender(te, matrix, buffer, combinedLight, partialTicks, te.mats[i], i == 6 ? null : Direction.from3DDataValue(i), te.getAxleAxis());
				matrix.popPose();
			}
		}
	}

	private static final AABB LARGE_GEAR_RENDER_BOX = new AABB(-1.5, -1.5, -1.5, 2.5, 2.5, 2.5);

	@Override
	public AABB getRenderBoundingBox(MechanismTileEntity te){
		for(int i = 0; i < 6; i++){
			if(te.members[i] == MechanismLargeGearCore.INSTANCE){
				//This contains at least one 3x3 render
				return LARGE_GEAR_RENDER_BOX.move(te.getBlockPos());
			}
		}
		return BlockEntityRenderer.super.getRenderBoundingBox(te);
	}
}
