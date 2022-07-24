package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidTankTileEntity;
import com.Da_Technomancer.crossroads.gui.container.FluidTankContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankScreen extends MachineScreen<FluidTankContainer, FluidTankTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fat_feeder_gui.png");

	public FluidTankScreen(FluidTankContainer cont, Inventory playerInv, Component text){
		super(cont, playerInv, text);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 80, 71);
	}


	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
