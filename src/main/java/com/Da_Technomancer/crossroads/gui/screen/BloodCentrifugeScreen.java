package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MathUtil;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BloodCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.gui.container.BloodCentrifugeContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BloodCentrifugeScreen extends MachineScreen<BloodCentrifugeContainer, BloodCentrifugeTileEntity>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/blood_centrifuge_gui.png");

	public BloodCentrifugeScreen(BloodCentrifugeContainer cont, Inventory playerInv, Component text){
		super(cont, playerInv, text);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);
		matrix.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		matrix.blit(BACKGROUND, leftPos + 50, topPos + 48, 176, 0, Math.min(66, menu.progRef.get() * 66 / BloodCentrifugeTileEntity.REQUIRED), 9);
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		String s = MiscUtil.localize("container.crossroads.blood_centrifuge.target", MathUtil.preciseRound(BloodCentrifugeTileEntity.getTargetSpeed(menu.progRef.get()), 1));
		matrix.drawString(font, s, imageWidth - 8 - font.width(s), 18, 0x404040, false);
	}
}
