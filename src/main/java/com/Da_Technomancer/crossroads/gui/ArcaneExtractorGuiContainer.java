package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.ArcaneExtractorContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ArcaneExtractorGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/arcane_extractor_gui.png");
	private final IInventory playerInv;
	private final IInventory te;

	public ArcaneExtractorGuiContainer(IInventory playerInv, IInventory te){
		super(new ArcaneExtractorContainer(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(te.getDisplayName().getUnformattedText(), 8, 6, 0x404040);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 84 - 12, 0x404040);
	}
}
