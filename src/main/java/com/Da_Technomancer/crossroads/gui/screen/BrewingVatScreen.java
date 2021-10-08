package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BrewingVatContainer;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.BrewingVatTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class BrewingVatScreen extends MachineGUI<BrewingVatContainer, BrewingVatTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/brewing_vat_gui.png");

	public BrewingVatScreen(BrewingVatContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 25, topPos + 35, 176, 0, menu.craftProgress.get() * 54 / BrewingVatTileEntity.REQUIRED, 19);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
