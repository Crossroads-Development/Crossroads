package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.crossroads.gui.container.WindingTableContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WindingTableScreen extends MachineScreen<WindingTableContainer, WindingTableTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/winding_table_gui.png");

	public WindingTableScreen(WindingTableContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		super.renderBg(matrix, partialTicks, mouseX, mouseY);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		matrix.blit(GUI_TEXTURES, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int prog = menu.progRef.get();
		int k = prog == 0 ? 0 : 1 + (prog * 13 / 100);
		if(k != 0){
			matrix.blit(GUI_TEXTURES, leftPos + 81, topPos + 44 - k, 176, 13 - k, 14, k);
		}
	}
}
