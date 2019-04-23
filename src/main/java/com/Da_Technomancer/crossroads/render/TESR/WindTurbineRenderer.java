package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindTurbineTileEntity;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
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

public class WindTurbineRenderer extends TileEntitySpecialRenderer<WindTurbineTileEntity>{

	private static final ResourceLocation TEXTURE_BLADE = new ResourceLocation(Main.MODID, "textures/model/wind_turbine_blade.png");

	@Override
	public void render(WindTurbineTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!te.getWorld().isBlockLoaded(te.getPos(), false)){
			return;
		}
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		IAxleHandler axle = te.getCapability(Capabilities.AXLE_CAPABILITY, null);

		if(state.getBlock() != ModBlocks.windTurbine || axle == null){
			return;
		}

		EnumFacing facing = state.getValue(Properties.HORIZ_FACING);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5F, y + .5F, z + .5F);
		GlStateManager.rotate(-facing.getHorizontalAngle(), 0, 1, 0);
		GlStateManager.rotate(facing.getAxisDirection().getOffset() * axle.getAngle(partialTicks), 0, 0, 1);

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_BLADE);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		//Center piece
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-0.25D, -0.25D, 0.6D).tex(0, 0.25F).endVertex();
		vb.pos(0.25D, -0.25D, 0.6D).tex(0, 0.5F).endVertex();
		vb.pos(0.25D, 0.25D, 0.6D).tex(0.25F, 0.5F).endVertex();
		vb.pos(-0.25D, 0.25D, 0.6D).tex(0.25F, 0.25F).endVertex();
		Tessellator.getInstance().draw();

		//Blades
		for(int i = 0; i < 4; i++){
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			//Center cap
			vb.pos(-0.25D, 0.25D, 0.5D).tex(0, 0.25F).endVertex();
			vb.pos(-0.25D, 0.25D, 0.6D).tex(0.05F, 0.25F).endVertex();
			vb.pos(0.25D, 0.25D, 0.6D).tex(0.05F, 0.5F).endVertex();
			vb.pos(0.25D, 0.25D, 0.5D).tex(0, 0.5F).endVertex();

			//Wood spoke
			vb.pos(-0.0625D, 0.25D, 0.6D).tex(0, .09375F).endVertex();
			vb.pos(0.0625D, 0.25D, 0.6D).tex(0, .15625F).endVertex();
			vb.pos(0.0625D, 2, 0.6D).tex(.75F, .15625F).endVertex();
			vb.pos(-0.0625D, 2, 0.6D).tex(.75F, .09375F).endVertex();

			//Wool panel
			vb.pos(-.25D, 0.25D, 0.5D).tex(0, 0F).endVertex();
			vb.pos(-0.0625D, 0.25D, 0.6D).tex(0, .09375F).endVertex();
			vb.pos(-0.0625D, 2, 0.6D).tex(.75F, .09375F).endVertex();
			vb.pos(-.25D, 2, 0.5D).tex(.75F, 0F).endVertex();

			//Wool panel
			vb.pos(0.0625D, 0.25D, 0.6D).tex(0, 0F).endVertex();
			vb.pos(.25D, 0.25D, 0.5D).tex(0, .09375F).endVertex();
			vb.pos(.25D, 2, 0.5D).tex(.75F, .09375F).endVertex();
			vb.pos(0.0625D, 2, 0.6D).tex(.75F, 0F).endVertex();

			//Back
			vb.pos(-0.25D, 0.25D, 0.5D).tex(0, 0).endVertex();
			vb.pos(-0.25D, 2, 0.5D).tex(.75F, 0).endVertex();
			vb.pos(0.25D, 2, 0.5D).tex(.75F, .25F).endVertex();
			vb.pos(0.25D, 0.25D, 0.5D).tex(0, .25F).endVertex();

			//End cap
			vb.pos(-0.25D, 2, 0.5D).tex(0, 0.5F).endVertex();
			vb.pos(-0.0625D, 2, 0.6D).tex(0.05F, 0.40625F).endVertex();
			vb.pos(0.0625D, 2, 0.6D).tex(0.05F, 0.34375F).endVertex();
			vb.pos(0.25D, 2, 0.5D).tex(0, 0.25F).endVertex();

			Tessellator.getInstance().draw();
			GlStateManager.rotate(90, 0, 0, 1);
		}

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
