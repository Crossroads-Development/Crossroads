package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.heat.SmelterTileEntity;
import com.Da_Technomancer.crossroads.gui.container.SmelterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SmelterScreen extends MachineScreen<SmelterContainer, SmelterTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/heating_chamber_gui.png");

	public SmelterScreen(SmelterContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);

		matrix.blit(GUI_TEXTURES, (this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2, 0, 0, imageWidth, imageHeight);

		matrix.blit(GUI_TEXTURES, (this.width - this.imageWidth) / 2 + 79, (this.height - this.imageHeight) / 2 + 34, 176, 0, menu.cookProg.get() * 24 / SmelterTileEntity.REQUIRED, 17);
	}
}
