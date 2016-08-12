package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.container.SlottedChestContainer;
import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class SlottedChestGuiContainer extends GuiContainer{

	/** The ResourceLocation containing the chest GUI texture. */
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final IInventory playerInventory;
	private final SlottedChestTileEntity te;
	/** window height is calculated with these values; the more rows, the heigher */
	private final int inventoryRows;

	public SlottedChestGuiContainer(IInventory playerInventory, SlottedChestTileEntity chest){
		super(new SlottedChestContainer(playerInventory, chest));
		this.playerInventory = playerInventory;
		this.te = chest;
		this.allowUserInput = false;
		this.inventoryRows = chest.iInv.getSizeInventory() / 9;
		this.ySize = 114 + this.inventoryRows * 18;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		this.fontRendererObj.drawString(this.te.iInv.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
		this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}
}
