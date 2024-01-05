package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.AdvancementTracker;
import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DetailedCrafterScreen extends AbstractContainerScreen<DetailedCrafterContainer>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");

	public DetailedCrafterScreen(DetailedCrafterContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
		imageWidth = 176;
		imageHeight = 166;
	}

	@Override
	protected void init(){
		super.init();
		AdvancementTracker.listen();//We use the path advancement
	}

	@Override
	public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		//Background
		RenderSystem.setShaderColor(1, 1, 1, 1);
		matrix.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if(EnumPath.TECHNOMANCY.isUnlocked(minecraft.player)){
			matrix.blit(BACKGROUND, leftPos + 124, topPos + 60, 176, 0, 16, 16);
		}
		if(EnumPath.ALCHEMY.isUnlocked(minecraft.player)){
			matrix.blit(BACKGROUND, leftPos + 108, topPos + 60, 176, 16, 16, 16);
		}
		if(EnumPath.WITCHCRAFT.isUnlocked(minecraft.player)){
			matrix.blit(BACKGROUND, leftPos + 140, topPos + 60, 176, 32, 16, 16);
		}
	}
}
