package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MillstoneScreen extends MachineGUI<MillstoneContainer, MillstoneTileEntity>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/millstone_gui.png");

	public MillstoneScreen(MillstoneContainer cont, Inventory playerInv, Component text){
		super(cont, playerInv, text);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 66, topPos + 35, 176, 0, 44, (int) Math.ceil(menu.progRef.get() * 17 / MillstoneTileEntity.REQUIRED));
	}
}
