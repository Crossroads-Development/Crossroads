package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.fluid.SteamerTileEntity;
import com.Da_Technomancer.crossroads.gui.container.SteamerContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SteamerScreen extends MachineScreen<SteamerContainer, SteamerTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/steamer_gui.png");

	public SteamerScreen(SteamerContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 33, 70);
		initFluidManager(1, 145, 70);
	}


	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);
		matrix.blit(GUI_TEXTURES, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		matrix.blit(GUI_TEXTURES, leftPos + 79, topPos + 34, 176, 0, menu.cookProg.get() * 24 / SteamerTileEntity.REQUIRED, 17);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
