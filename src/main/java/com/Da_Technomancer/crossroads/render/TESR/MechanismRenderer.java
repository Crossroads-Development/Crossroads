package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

public class MechanismRenderer extends TileEntityRenderer<MechanismTileEntity>{

	protected MechanismRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(MechanismTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		matrix.translate(0.5D, 0.5D, 0.5D);

		for(int i = 0; i < 7; i++){
			if(te.members[i] != null){
				matrix.pushPose();
				te.members[i].doRender(te, matrix, buffer, combinedLight, partialTicks, te.mats[i], i == 6 ? null : Direction.from3DDataValue(i), te.getAxleAxis());
				matrix.popPose();
			}
		}
	}
}
