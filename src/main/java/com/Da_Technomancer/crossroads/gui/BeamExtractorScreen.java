package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BeamExtractorContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BeamExtractorScreen extends ContainerScreen<BeamExtractorContainer>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID + ":textures/gui/container/arcane_extractor_gui.png");

	public BeamExtractorScreen(BeamExtractorContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		font.drawString(title.getFormattedText(), 8, 6, 0x404040);
		font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, 84 - 12, 0x404040);
	}
}
