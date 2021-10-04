package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.CultivatorVatTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
		//When active, use a fixed light level (block light 6) due to liquid and internal lamp. Otherwise, get light from above this blockspace
		int light = state.getValue(CRProperties.ACTIVE) ? 6 << 4 : CRRenderUtil.getLightAtPos(te.getLevel(), te.getBlockPos().above());
		IVertexBuilder builder = buffer.getBuffer(RenderType.solid());
		int[] col = {255, 255, 255, 255};

		if(contents == 1){
			//Embryo
			//It's a weird blob

			//Size pulses slowly
			float sin = (float) Math.sin((te.getLevel().getGameTime() + partialTicks) / 10D);
			float scale0 = 1F + 0.1F * sin;
			float scale1 = 1F - 0.1F * sin;
			TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.EMBRYO_TEXTURE);

			float uSt = sprite.getU0();
			float uEn = sprite.getU1();
			float vSt = sprite.getV0();
			float vEn = sprite.getV1();
			matrix.pushPose();
			matrix.scale(scale0, scale0, scale0);
			CRModels.drawBox(matrix, builder, light, col, 0.17F, 0.17F, 0.17F, uSt, vSt, uEn, vEn, uSt, vSt, uEn, vEn, uSt, vSt, uEn, vEn);
			matrix.popPose();
			matrix.translate(0.15F, 0.15F, 0.15F);
			matrix.scale(scale1, scale1, scale1);
			CRModels.drawBox(matrix, builder, light, col, 0.12F, 0.12F, 0.12F, uSt, vSt, uEn, vEn, uSt, vSt, uEn, vEn, uSt, vSt, uEn, vEn);
		}

		if(contents == 2){
			//Brain
			//Bobs up and down slightly
			matrix.translate(0, 0.15F + 0.05F * Math.sin((te.getLevel().getGameTime() + partialTicks) / 20F), 0);

			//Brains rotate to follow the player
			ClientPlayerEntity player = Minecraft.getInstance().player;

			matrix.mulPose(Vector3f.YN.rotation((float) Math.atan2(player.getZ() - (0.5D + te.getBlockPos().getZ()), player.getX() - (0.5D + te.getBlockPos().getX()))));
			matrix.scale(0.4F, 0.4F, 0.4F);

			TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.VILLAGER_BRAIN_TEXTURE);

			//Brain
			float uTopMin = sprite.getU0();
			float vTopMin = sprite.getV(8);
			float uTopMax = sprite.getU(8);
			float vTopMax = sprite.getV1();
			float uBackMin = sprite.getU(8);
			float vBackMin = sprite.getV0();
			float uBackMax = sprite.getU1();
			float vBackMax = sprite.getV(4);
			float vFrontMin = sprite.getV(8);
			float vFrontMax = sprite.getV(12);
			float uSideMin = sprite.getU0();
			float uSideMax = sprite.getU(8);
			float vSideMin = sprite.getV(8);
			float vSideMax = sprite.getV1();
			CRModels.drawBox(matrix, builder, light, col, 0.5F, 0.25F, 0.5F, uTopMin, vTopMin, uTopMax, vTopMax, uBackMin, vBackMin, uBackMax, vBackMax, uSideMin, vSideMin, uSideMax, vSideMax);
			//Front side (for a different texture on the front)
			matrix.translate(0.5F + 0.01F, 0, 0);
			CRModels.drawBox(matrix, builder, light, col, 0.01F, 0.25F, 0.5F, uTopMin, vTopMin, uTopMax, vTopMax, uBackMin, vFrontMin, uBackMax, vFrontMax, uSideMin, vSideMin, uSideMax, vSideMax);

			//Nose
			matrix.translate(1/16F, -0.25F, 0);
			float nUTopMin = sprite.getU(2);
			float nVTopMin = sprite.getV0();
			float nUTopMax = sprite.getU(3);
			float nVTopMax = sprite.getV(1);
			float nUSideMin = sprite.getU(1);
			float nVSideMin = nVTopMax;
			float nUSideMax = nUTopMin;
			float nVSideMax = sprite.getV(3);
			CRModels.drawBox(matrix, builder, light, col, 2F / 16F, 4F / 16F, 2F / 16F, nUTopMin, nVTopMin, nUTopMax, nVTopMax, nUSideMin, nVSideMin, nUSideMax, nVSideMax, nUSideMin, nVSideMin, nUSideMax, nVSideMax);
		}
	}
}
