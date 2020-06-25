package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DetailedCrafterScreen extends ContainerScreen<DetailedCrafterContainer>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");

	public DetailedCrafterScreen(DetailedCrafterContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void init(){
		super.init();
		AdvancementTracker.listen();//We use the path advancement
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
		if(EnumPath.TECHNOMANCY.isUnlocked(playerInventory.player)){
			blit(guiLeft + 124, guiTop + 60, 176, 0, 16, 16);
		}
		if(EnumPath.ALCHEMY.isUnlocked(playerInventory.player)){
			blit(guiLeft + 108, guiTop + 60, 176, 16, 16, 16);
		}
		if(EnumPath.WITCHCRAFT.isUnlocked(playerInventory.player)){
			blit(guiLeft + 140, guiTop + 60, 176, 32, 16, 16);
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
		font.drawString(I18n.format("container.detailed_crafting"), 28, 6, 0x404040);
        font.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);

	}
}
