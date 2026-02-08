package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearMasterTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class LargeGearRenderer implements BlockEntityRenderer<LargeGearMasterTileEntity>{

	protected LargeGearRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(LargeGearMasterTileEntity gear, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		if(gear.getBlockState().getBlock() != CRBlocks.largeGearMaster){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);
		Direction facing = gear.getFacing();
		IAxleHandler handler = gear.getAxleHandler(facing);
		float dirMult = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1 : 1;

		matrix.mulPose(facing.getOpposite().getRotation());

		matrix.mulPose(Axis.YP.rotationDegrees(handler.getAngle(partialTicks) * dirMult));
		matrix.pushPose();
		matrix.translate(0, -0.4375D, 0);
		CRModels.draw24GearMilled(matrix, buffer, combinedLight, gear.getMember().getColor(), CRModels.generateZFightFactor(gear.getBlockPos(), 0));
		matrix.popPose();
		CRModels.drawAxle(matrix, buffer, combinedLight, gear.getMember().getColor());
//
//		if(gear.isRenderedOffset()){
//			matrix.mulPose(Axis.YP.rotationDegrees(-7.5F));
//		}
	}


	private static final AABB RENDER_BOX = new AABB(-1.5, -1.5, -1.5, 2.5, 2.5, 2.5);

	@Override
	public AABB getRenderBoundingBox(LargeGearMasterTileEntity te){
		return RENDER_BOX.move(te.getBlockPos());
	}
}
