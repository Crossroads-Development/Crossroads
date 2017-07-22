package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.gui.container.SlottedChestContainer;
import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SlottedChestGuiContainer extends GuiContainer{
	
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final IInventory playerInventory;
	private final SlottedChestTileEntity te;
	
	public SlottedChestGuiContainer(IInventory playerInventory, SlottedChestTileEntity chest){
		super(new SlottedChestContainer(playerInventory, chest));
		this.playerInventory = playerInventory;
		te = chest;
		allowUserInput = false;
		ySize = 222;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(te.iInv.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, ySize - 94, 4210752);
		GlStateManager.pushAttrib();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		for(int i = 0; i < 54; i++){
			ItemStack filter = te.lockedInv[i];
			Slot renderSlot = inventorySlots.getSlot(i);
			if(!filter.isEmpty() && !renderSlot.getHasStack()){
				itemRender.renderItemAndEffectIntoGUI(mc.player, filter, renderSlot.xPos, renderSlot.yPos);
	            itemRender.renderItemOverlayIntoGUI(fontRenderer, filter, renderSlot.xPos, renderSlot.yPos, "0");
			}
		}
		GlStateManager.enableLighting();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, 125);
		drawTexturedModalRect(i, j + 125, 0, 126, xSize, 96);
	}
}
