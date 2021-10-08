package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.WindingTableContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class WindingTableScreen extends MachineGUI<WindingTableContainer, WindingTableTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/winding_table_gui.png");

	public WindingTableScreen(WindingTableContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		super.renderBg(matrix, partialTicks, mouseX, mouseY);

		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURES);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int prog = menu.progRef.get();
		int k = prog == 0 ? 0 : 1 + (prog * 13 / 100);
		if(k != 0){
			blit(matrix, leftPos + 81, topPos + 44 - k, 176, 13 - k, 14, k);
		}
	}
}
