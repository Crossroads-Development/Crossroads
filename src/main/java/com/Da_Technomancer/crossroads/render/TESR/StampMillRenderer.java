package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class StampMillRenderer extends TileEntitySpecialRenderer<StampMillTileEntity>{

	private static final ResourceLocation METAL_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/block_cast_iron.png");

	@Override
	public void render(StampMillTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		IAxleHandler axle = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, null);
		if(!te.getWorld().isBlockLoaded(te.getPos(), false) || state.getBlock() != ModBlocks.stampMill || axle == null){
			return;
		}
		
		Color ironColor = GearFactory.findMaterial("Iron").getColor();

		float prog = Math.signum(axle.getClientW()) * ((axle.getNextAngle() - axle.getAngle()) * partialTicks + axle.getAngle());
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + 1.5F, z + .5D);
		if(state.getValue(Properties.HORIZ_AXIS) == EnumFacing.Axis.Z){
			GlStateManager.rotate(90, 0, 1, 0);
		}

		//Axle
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-prog, 0, 1, 0);
		ModelAxle.render(ironColor);

		//Teeth
		for(int i = 0; i < 3; i++){
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -13F / 32F + 5F * i / 16F, 0);
			GlStateManager.rotate(i * 90 + 90, 0, 1, 0);
			GlStateManager.rotate(90, 0, 0, 1);
			GlStateManager.scale(0.4F, 0.5F, 0.4F);

			ModelAxle.render(ironColor);
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();

		Minecraft.getMinecraft().getTextureManager().bindTexture(METAL_TEXTURE);
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();
		double offset0 = (Math.sin(2D * Math.toRadians(prog)) + 1D) / 2D * 9D / 32D;
		double offset1 = (Math.sin(2D * Math.toRadians(prog - 90D)) + 1D) / 2D * 9D / 32D;


		GlStateManager.translate(-5F/ 16F, offset1, -2F / 8F);
		//Stamps
		for(int i = 0; i < 3; i++){
			GlStateManager.translate(0, i % 2 == 0 ? offset0 - offset1 : offset1 - offset0, 0);
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			//Rod
			float rodRad = 1F / 16F;
			float rodLen = 14F / 16F;
			buf.pos(-rodRad, 0, -rodRad).tex(rodRad, 0).endVertex();
			buf.pos(rodRad, 0, -rodRad).tex(3D * rodRad, 0).endVertex();
			buf.pos(rodRad, -rodLen, -rodRad).tex(3D * rodRad, rodLen).endVertex();
			buf.pos(-rodRad, -rodLen, -rodRad).tex(rodRad, rodLen).endVertex();

			buf.pos(-rodRad, 0, rodRad).tex(rodRad, 0).endVertex();
			buf.pos(-rodRad, -rodLen, rodRad).tex(rodRad, rodLen).endVertex();
			buf.pos(rodRad, -rodLen, rodRad).tex(3D * rodRad, rodLen).endVertex();
			buf.pos(rodRad, 0, rodRad).tex(3D * rodRad, 0).endVertex();

			buf.pos(-rodRad, 0, -rodRad).tex(rodRad, 0).endVertex();
			buf.pos(-rodRad, -rodLen, -rodRad).tex(rodRad, rodLen).endVertex();
			buf.pos(-rodRad, -rodLen, rodRad).tex(3D * rodRad, rodLen).endVertex();
			buf.pos(-rodRad, 0, rodRad).tex(3D * rodRad, 0).endVertex();

			buf.pos(rodRad, 0, -rodRad).tex(rodRad, 0).endVertex();
			buf.pos(rodRad, 0, rodRad).tex(3D * rodRad, 0).endVertex();
			buf.pos(rodRad, -rodLen, rodRad).tex(3D * rodRad, rodLen).endVertex();
			buf.pos(rodRad, -rodLen, -rodRad).tex(rodRad, rodLen).endVertex();

			buf.pos(-rodRad, 0, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(-rodRad, 0, rodRad).tex(rodRad, 3D * rodRad).endVertex();
			buf.pos(rodRad, 0, rodRad).tex(3D * rodRad, 3D * rodRad).endVertex();
			buf.pos(rodRad, 0, -rodRad).tex(3D * rodRad, rodRad).endVertex();

			//Pin
			buf.pos(rodRad, 0, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(2D * rodRad, 0, -rodRad).tex(2D * rodRad, rodRad).endVertex();
			buf.pos(2D * rodRad, -2D * rodRad, -rodRad).tex(2D * rodRad, 3D * rodRad).endVertex();
			buf.pos(rodRad, -2D * rodRad, -rodRad).tex(rodRad, 3D * rodRad).endVertex();

			buf.pos(rodRad, 0, rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(rodRad, -2D * rodRad, rodRad).tex(rodRad, 3D * rodRad).endVertex();
			buf.pos(2D * rodRad, -2D * rodRad, rodRad).tex(3D * rodRad, 3D * rodRad).endVertex();
			buf.pos(2D * rodRad, 0, rodRad).tex(3D * rodRad, rodRad).endVertex();

			buf.pos(2D * rodRad, 0, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(2D * rodRad, 0, rodRad).tex(3D * rodRad, rodRad).endVertex();
			buf.pos(2D * rodRad, -2D * rodRad, rodRad).tex(3D * rodRad, 3D * rodRad).endVertex();
			buf.pos(2D * rodRad, -2D * rodRad, -rodRad).tex(rodRad, 3D * rodRad).endVertex();

			buf.pos(rodRad, 0, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(rodRad, 0, rodRad).tex(rodRad, 3D * rodRad).endVertex();
			buf.pos(2D * rodRad, 0, rodRad).tex(2D * rodRad, 3D * rodRad).endVertex();
			buf.pos(2D * rodRad, 0, -rodRad).tex(2D * rodRad, rodRad).endVertex();

			buf.pos(rodRad, -2D * rodRad, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(2D * rodRad, -2D * rodRad, -rodRad).tex(2D * rodRad, rodRad).endVertex();
			buf.pos(2D * rodRad, -2D * rodRad, rodRad).tex(2D * rodRad, 3D * rodRad).endVertex();
			buf.pos(rodRad, -2D * rodRad, rodRad).tex(rodRad, 3D * rodRad).endVertex();

			//Stamp Head
			rodRad = 1F / 8F;
			float bottom = 1.25F;
			buf.pos(-rodRad, -rodLen, -rodRad).tex(rodRad, 0).endVertex();
			buf.pos(rodRad, -rodLen, -rodRad).tex(3D * rodRad, 0).endVertex();
			buf.pos(rodRad, -bottom, -rodRad).tex(3D * rodRad, bottom - rodLen).endVertex();
			buf.pos(-rodRad, -bottom, -rodRad).tex(rodRad, bottom - rodLen).endVertex();

			buf.pos(-rodRad, -rodLen, rodRad).tex(rodRad, 0).endVertex();
			buf.pos(-rodRad, -bottom, rodRad).tex(rodRad, bottom - rodLen).endVertex();
			buf.pos(rodRad, -bottom, rodRad).tex(3D * rodRad, bottom - rodLen).endVertex();
			buf.pos(rodRad, -rodLen, rodRad).tex(3D * rodRad, 0).endVertex();

			buf.pos(-rodRad, -rodLen, -rodRad).tex(rodRad, 0).endVertex();
			buf.pos(-rodRad, -bottom, -rodRad).tex(rodRad, bottom - rodLen).endVertex();
			buf.pos(-rodRad, -bottom, rodRad).tex(3D * rodRad, bottom - rodLen).endVertex();
			buf.pos(-rodRad, -rodLen, rodRad).tex(3D * rodRad, 0).endVertex();

			buf.pos(rodRad, -rodLen, -rodRad).tex(rodRad, 0).endVertex();
			buf.pos(rodRad, -rodLen, rodRad).tex(3D * rodRad, 0).endVertex();
			buf.pos(rodRad, -bottom, rodRad).tex(3D * rodRad, bottom - rodLen).endVertex();
			buf.pos(rodRad, -bottom, -rodRad).tex(rodRad, bottom - rodLen).endVertex();

			buf.pos(-rodRad, -rodLen, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(-rodRad, -rodLen, rodRad).tex(rodRad, 3D * rodRad).endVertex();
			buf.pos(rodRad, -rodLen, rodRad).tex(3D * rodRad, 3D * rodRad).endVertex();
			buf.pos(rodRad, -rodLen, -rodRad).tex(3D * rodRad, rodRad).endVertex();

			buf.pos(-rodRad, -bottom, -rodRad).tex(rodRad, rodRad).endVertex();
			buf.pos(rodRad, -bottom, -rodRad).tex(3D * rodRad, rodRad).endVertex();
			buf.pos(rodRad, -bottom, rodRad).tex(3D * rodRad, 3D * rodRad).endVertex();
			buf.pos(-rodRad, -bottom, rodRad).tex(rodRad, 3D * rodRad).endVertex();
			tes.draw();

			GlStateManager.translate(5F / 16F, 0, 0);
		}


		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
