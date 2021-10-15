package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

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
		LazyOptional<IAxleHandler> handler = gear.getCapability(Capabilities.AXLE_CAPABILITY, facing);
		float dirMult = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1 : 1;

		matrix.mulPose(facing.getOpposite().getRotation());

		if(handler.isPresent()){
			matrix.mulPose(Vector3f.YP.rotationDegrees(handler.orElseThrow(NullPointerException::new).getAngle(partialTicks) * dirMult));
			matrix.pushPose();
			matrix.scale(3, 1, 3);
			CRModels.draw24Gear(matrix, buffer, combinedLight, gear.getMember().getColor());
			matrix.popPose();

			if(gear.isRenderedOffset()){
				matrix.mulPose(Vector3f.YP.rotationDegrees(-7.5F));
			}
			CRModels.drawAxle(matrix, buffer, combinedLight, gear.getMember().getColor());
		}
	}
}
