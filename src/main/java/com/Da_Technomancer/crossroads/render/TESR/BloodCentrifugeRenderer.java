package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.BloodCentrifugeTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class BloodCentrifugeRenderer extends TileEntityRenderer<BloodCentrifugeTileEntity>{


	protected BloodCentrifugeRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(BloodCentrifugeTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.bloodCentrifuge){
			return;
		}
		//TODO
	}
}
