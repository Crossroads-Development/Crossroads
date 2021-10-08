package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.SmelterContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class SmelterScreen extends MachineGUI<SmelterContainer, SmelterTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/heating_chamber_gui.png");

	public SmelterScreen(SmelterContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURES);

		blit(matrix, (this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2, 0, 0, imageWidth, imageHeight);

		blit(matrix, (this.width - this.imageWidth) / 2 + 79, (this.height - this.imageHeight) / 2 + 34, 176, 0, menu.cookProg.get() * 24 / SmelterTileEntity.REQUIRED, 17);
	}
}
