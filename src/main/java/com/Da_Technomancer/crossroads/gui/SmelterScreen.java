package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.SmelterContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SmelterScreen extends MachineGUI<SmelterContainer, SmelterTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/heating_chamber_gui.png");

	public SmelterScreen(SmelterContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);

		blit(i + 79, j + 34, 176, 0, container.cookProg.get() * 24 / SmelterTileEntity.REQUIRED, 17);
	}
}
