package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.heat.FireboxTileEntity;
import com.Da_Technomancer.crossroads.gui.container.FireboxContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FireboxScreen extends MachineScreen<FireboxContainer, FireboxTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID + ":textures/gui/container/firebox_gui.png");

	public FireboxScreen(FireboxContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);

		imageWidth = 176;
		imageHeight = 136;
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, GUI_TEXTURES);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int burnTime = menu.burnProg.get();
		int k = burnTime == 0 ? 0 : 1 + (burnTime * 13 / 100);
		if(k != 0){
			blit(matrix, leftPos + 81, topPos + 19 - k, 176, 13 - k, 14, k);
		}
	}

}
