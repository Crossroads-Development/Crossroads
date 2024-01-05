package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.rotary.MillstoneTileEntity;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MillstoneScreen extends MachineScreen<MillstoneContainer, MillstoneTileEntity>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/millstone_gui.png");

	public MillstoneScreen(MillstoneContainer cont, Inventory playerInv, Component text){
		super(cont, playerInv, text);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);
		matrix.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrix.blit(BACKGROUND, leftPos + 66, topPos + 35, 176, 0, 44, (int) Math.ceil(menu.progRef.get() * 17 / MillstoneTileEntity.REQUIRED));
	}
}
