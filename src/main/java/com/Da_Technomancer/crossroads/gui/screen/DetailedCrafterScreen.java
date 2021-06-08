package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DetailedCrafterScreen extends ContainerScreen<DetailedCrafterContainer>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");

	public DetailedCrafterScreen(DetailedCrafterContainer cont, PlayerInventory playerInv, ITextComponent name){
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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		//Background
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if(EnumPath.TECHNOMANCY.isUnlocked(inventory.player)){
			blit(matrix, leftPos + 124, topPos + 60, 176, 0, 16, 16);
		}
		if(EnumPath.ALCHEMY.isUnlocked(inventory.player)){
			blit(matrix, leftPos + 108, topPos + 60, 176, 16, 16, 16);
		}
		if(EnumPath.WITCHCRAFT.isUnlocked(inventory.player)){
			blit(matrix, leftPos + 140, topPos + 60, 176, 32, 16, 16);
		}
	}
}
