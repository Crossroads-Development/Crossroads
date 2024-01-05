package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.IncubatorTileEntity;
import com.Da_Technomancer.crossroads.gui.container.IncubatorContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class IncubatorScreen extends MachineScreen<IncubatorContainer, IncubatorTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/incubator_gui.png");

	public IncubatorScreen(IncubatorContainer container, Inventory playerInv, Component name){
		super(container, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		matrix.blit(TEXTURE, leftPos + 43, topPos + 35, 176, 0, menu.progressRef.get() * 54 / IncubatorTileEntity.REQUIRED, 10);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		//Changes color based on whether within target temperature
		int targetTemp = menu.targetRef.get();
		String s = MiscUtil.localize("container.crossroads.incubator.target", targetTemp);
		matrix.drawString(font, s, imageWidth - 8 - font.width(s), 16, IncubatorTileEntity.withinTarget(menu.heatRef.get(), targetTemp) ? Color.GREEN.getRGB() : Color.RED.getRGB(), false);
	}
}
