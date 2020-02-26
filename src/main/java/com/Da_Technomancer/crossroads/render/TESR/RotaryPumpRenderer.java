package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RotaryPumpRenderer extends TileEntityRenderer<RotaryPumpTileEntity>{

	@Override
	public void render(RotaryPumpTileEntity pump, double x, double y, double z, float partialTicks, int destroyStage){
		if(pump == null || !pump.getWorld().isBlockLoaded(pump.getPos())){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);
		GlStateManager.translated(0.5F, 0F, .5F);
		GlStateManager.rotated(pump.getCapability(Capabilities.AXLE_CAPABILITY, null).orElseThrow(NullPointerException::new).getAngle(partialTicks), 0F, 1F, 0F);
		GlStateManager.disableLighting();

		CRModels.renderScrew();
		
		GlStateManager.popMatrix();
		
		if(pump.getCompletion() != 0){
			BlockPos fPos = pump.getPos().offset(Direction.DOWN);
			IFluidState state = pump.getWorld().getFluidState(fPos);
			TextureAtlasSprite lText;
			Color fCol;
			if(!state.isEmpty() && state.isSource()){
				ResourceLocation textLoc = state.getFluid().getAttributes().getStillTexture();
				lText = Minecraft.getInstance().getTextureMap().getSprite(textLoc);
				fCol = new Color(state.getFluid().getAttributes().getColor(pump.getWorld(), fPos));
			}else{
				return;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translated(x, y, z);
			GlStateManager.color4f(fCol.getRed() / 255F, fCol.getGreen() / 255F, fCol.getBlue() / 255F, fCol.getAlpha() / 255F);

			float xSt = 3F / 16F;
			float ySt = 0;
			float zSt = 3F / 16F;
			float xEn = 13F / 16F;
			float yEn = 7F / 16F * pump.getCompletion();
			float zEn = 13F / 16F;

			//Draw liquid layer
			Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			BufferBuilder vb = Tessellator.getInstance().getBuffer();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(xEn, ySt, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, ySt, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			vb.pos(xSt, ySt, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, ySt, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			vb.pos(xEn, ySt, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, ySt, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			vb.pos(xSt, ySt, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, ySt, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();

			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (zSt * 16))).endVertex();
			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (zSt * 16))).endVertex();
			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (zEn * 16))).endVertex();
			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (zEn * 16))).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();

		}
	}
}
