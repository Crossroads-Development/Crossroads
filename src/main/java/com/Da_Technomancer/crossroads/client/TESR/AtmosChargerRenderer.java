package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AtmosChargerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AtmosChargerRenderer extends TileEntitySpecialRenderer<AtmosChargerTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/block_cast_iron.png");

	@Override
	public void render(AtmosChargerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		//TODO
	}
}
