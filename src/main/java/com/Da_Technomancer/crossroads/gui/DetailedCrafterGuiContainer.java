package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class DetailedCrafterGuiContainer extends ContainerScreen{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");

	public DetailedCrafterGuiContainer(PlayerInventory playerInv, BlockPos pos, boolean fake){
		super(new DetailedCrafterContainer(playerInv, pos, fake));

		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if(StoreNBTToClient.clientPlayerTag.getCompound("path").getBoolean("technomancy")){
			drawTexturedModalRect(guiLeft + 124, guiTop + 60, 176, 0, 16, 16);
		}
		if(StoreNBTToClient.clientPlayerTag.getCompound("path").getBoolean("alchemy")){
			drawTexturedModalRect(guiLeft + 108, guiTop + 60, 176, 16, 16, 16);
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(I18n.format("container.detailed_crafting"), 28, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
        
	}
}
