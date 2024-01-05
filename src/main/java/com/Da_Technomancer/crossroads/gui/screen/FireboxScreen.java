package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.heat.FireboxTileEntity;
import com.Da_Technomancer.crossroads.gui.container.FireboxContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FireboxScreen extends MachineScreen<FireboxContainer, FireboxTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID + ":textures/gui/container/firebox_gui.png");

	public FireboxScreen(FireboxContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);

		imageWidth = 176;
		imageHeight = 136;
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);

		matrix.blit(GUI_TEXTURES, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int burnTime = menu.burnProg.get();
		int k = burnTime == 0 ? 0 : 1 + (burnTime * 13 / 100);
		if(k != 0){
			matrix.blit(GUI_TEXTURES, leftPos + 81, topPos + 19 - k, 176, 13 - k, 14, k);
		}
	}

}
