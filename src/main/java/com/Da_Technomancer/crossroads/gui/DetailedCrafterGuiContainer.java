package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class DetailedCrafterGuiContainer extends GuiContainer{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/detailed_crafter.png");
	
	public DetailedCrafterGuiContainer(InventoryPlayer playerInv, BlockPos pos){
		super(new DetailedCrafterContainer(playerInv, pos));
		
		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if(StoreNBTToClient.clientPlayerTag.getCompoundTag("path").getBoolean("technomancy")){
			drawTexturedModalRect(guiLeft + 124, guiTop + 60, 176, 0, 16, 16);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(I18n.format("container.detailed_crafting", new Object[0]), 28, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
        
	}
}
