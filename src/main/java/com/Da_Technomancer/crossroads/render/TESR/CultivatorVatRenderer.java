package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.CultivatorVatTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class CultivatorVatRenderer extends TileEntityRenderer<CultivatorVatTileEntity>{

	protected CultivatorVatRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(CultivatorVatTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.cultivatorVat){
			return;
		}
		//0: Empty; 1: Embryo; 2: Brain
		int contents = state.getValue(CRProperties.CONTENTS);

		if(contents == 0){
			return;
		}

		matrix.translate(.5F, .5F, .5F);
		if(contents == 2){
			//Brains rotate to follow the player
			ClientPlayerEntity player = Minecraft.getInstance().player;
			matrix.mulPose(Vector3f.YN.rotation((float) Math.atan2(player.getY() - (0.5D + te.getBlockPos().getY()), player.getX() - (0.5D + te.getBlockPos().getX()))));
		}
		//Size pulses slowly
		float scale = 1F + 0.2F * (float) Math.sin((te.getLevel().getGameTime() + partialTicks) / 10D);
		matrix.scale(scale, scale, scale);

		//TODO
	}
}
