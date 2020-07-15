package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

public class LargeGearRenderer extends TileEntityRenderer<LargeGearMasterTileEntity>{

	protected LargeGearRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(LargeGearMasterTileEntity gear, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(gear.getBlockState().getBlock() != CRBlocks.largeGearMaster){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);
		Direction facing = gear.getFacing();
		LazyOptional<IAxleHandler> handler = gear.getCapability(Capabilities.AXLE_CAPABILITY, facing);
		float dirMult = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1 : 1;

		matrix.rotate(facing.getOpposite().getRotation());

		if(handler.isPresent()){
			matrix.rotate(Vector3f.YP.rotationDegrees(handler.orElseThrow(NullPointerException::new).getAngle(partialTicks) * dirMult));
			matrix.push();
			matrix.scale(3, 1, 3);
			CRModels.draw24Gear(matrix, buffer, combinedLight, gear.getMember().getColor());
			matrix.pop();

			if(gear.isRenderedOffset()){
				matrix.rotate(Vector3f.YP.rotationDegrees(-7.5F));
			}
			CRModels.drawAxle(matrix, buffer, combinedLight, gear.getMember().getColor());
		}
	}
}
