package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

public class MechanismRenderer extends TileEntityRenderer<MechanismTileEntity>{

	protected MechanismRenderer(TileEntityRendererDispatcher rendererDispatcherIn){
		super(rendererDispatcherIn);
	}

	@Override
	public void render(MechanismTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
//		GlStateManager.pushMatrix();
//		GlStateManager.pushLightingAttributes();
//		GlStateManager.disableLighting();
//		GlStateManager.translated(x + .5D, y + .5D, z + .5D);

		matrix.translate(0.5D, 0.5D, 0.5D);

		for(int i = 0; i < 7; i++){
			if(te.members[i] != null){
				te.members[i].doRender(te, matrix, buffer, combinedLight, partialTicks, te.mats[i], i == 6 ? null : Direction.byIndex(i), te.getAxleAxis());
			}
		}

//		GlStateManager.enableLighting();
//		GlStateManager.popAttributes();
//		GlStateManager.popMatrix();


//		Color color;
//
//		for(EnumFacing side : EnumFacing.values()){
//			if(te.hasCapability(Capabilities.AXLE_CAPABILITY, side)){
//				IAxleHandler handler = te.getCapability(Capabilities.AXLE_CAPABILITY, side);
//				color = te.getMembers()[side.getIndex()].getColor();
//				GlStateManager.pushMatrix();
//				GlStateManager.pushLightingAttributes();
//				GlStateManager.disableLighting();
//				GlStateManager.translated(x + .5D, y + .5D, z + .5D);
//				GlStateManager.rotated(side == EnumFacing.DOWN ? 0 : side == EnumFacing.UP ? 180F : side == EnumFacing.NORTH || side == EnumFacing.EAST ? 90F : -90F, side.getAxis() == EnumFacing.Axis.Z ? 1 : 0, 0, side.getAxis() == EnumFacing.Axis.Z ? 0 : 1);
//				float angle = (float) (handler.getNextAngle() - handler.getAngle());
//				angle *= partialTicks;
//				angle += handler.getAngle();
//				GlStateManager.rotated(angle, 0F, 1F, 0F);
//				modelOct.draw(res, color);
//				GlStateManager.enableLighting();
//				GlStateManager.popAttributes();
//				GlStateManager.popMatrix();
//			}
//		}
	}
}
