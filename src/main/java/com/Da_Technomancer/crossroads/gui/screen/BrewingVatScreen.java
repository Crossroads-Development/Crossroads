package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BrewingVatTileEntity;
import com.Da_Technomancer.crossroads.gui.container.BrewingVatContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BrewingVatScreen extends MachineScreen<BrewingVatContainer, BrewingVatTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/brewing_vat_gui.png");

	public BrewingVatScreen(BrewingVatContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrix.blit(TEXTURE, leftPos + 25, topPos + 35, 176, 0, menu.craftProgress.get() * 54 / BrewingVatTileEntity.REQUIRED, 19);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
