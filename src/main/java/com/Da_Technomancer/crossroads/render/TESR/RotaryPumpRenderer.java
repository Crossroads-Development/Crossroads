package com.Da_Technomancer.crossroads.render.TESR;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelPump;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public class RotaryPumpRenderer extends TileEntitySpecialRenderer<RotaryPumpTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/model/pump.png");
	private static final ModelPump MODEL = new ModelPump();

	@Override
	public void render(RotaryPumpTileEntity pump, double x, double y, double z, float partialTicks, int destroyStage, float alpha){

		if(pump == null || !pump.getWorld().isBlockLoaded(pump.getPos(), false)){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(0.5F, 0F, .5F);
		GlStateManager.rotate(pump.getCompletion() * 360F, 0F, 1F, 0F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		MODEL.renderScrew();
		
		GlStateManager.popMatrix();
		
		if(pump.getCompletion() != 0){
			IBlockState state = pump.getWorld().getBlockState(pump.getPos().offset(EnumFacing.DOWN));
			TextureAtlasSprite lText = null;

			if(state == null){
				return;
			}

			if(state.getBlock() instanceof IFluidBlock){
				lText = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(((IFluidBlock) state.getBlock()).getFluid().getStill().toString());
			}else if(state.getBlock() instanceof BlockLiquid){
				lText = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry((state.getMaterial() == Material.LAVA ? FluidRegistry.LAVA.getStill() : FluidRegistry.WATER.getStill()).toString());
			}else{
				return;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			BufferBuilder vb = Tessellator.getInstance().getBuffer();

			float xSt = 3F / 16F;
			float ySt = 0;
			float zSt = 3F / 16F;
			float xEn = 13F / 16F;
			float yEn = 7F / 16F * pump.getCompletion();
			float zEn = 13F / 16F;

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(xEn, ySt, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, ySt, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(xSt, ySt, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, ySt, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(xEn, ySt, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, ySt, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(xSt, ySt, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, ySt, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (ySt * 16))).endVertex();
			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(zEn * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(zSt * 16), lText.getInterpolatedV(16 - (yEn * 16))).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(xEn, yEn, zSt).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (zSt * 16))).endVertex();
			vb.pos(xSt, yEn, zSt).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (zSt * 16))).endVertex();
			vb.pos(xSt, yEn, zEn).tex(lText.getInterpolatedU(xSt * 16), lText.getInterpolatedV(16 - (zEn * 16))).endVertex();
			vb.pos(xEn, yEn, zEn).tex(lText.getInterpolatedU(xEn * 16), lText.getInterpolatedV(16 - (zEn * 16))).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.popMatrix();

		}
	}
}
