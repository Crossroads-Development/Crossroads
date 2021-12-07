package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.TileEntityGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BeamExtractorContainer;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamExtractorTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BeamExtractorScreen extends TileEntityGUI<BeamExtractorContainer, BeamExtractorTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID + ":textures/gui/container/arcane_extractor_gui.png");

	public BeamExtractorScreen(BeamExtractorContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		super.renderBg(matrix, partialTicks, mouseX, mouseY);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, GUI_TEXTURES);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int prog = menu.progRef.get();
		int k = prog == 0 ? 0 : 1 + (prog * 13 / 100);
		if(k != 0){
			blit(matrix, leftPos + 81, topPos + 43 - k, 176, 13 - k, 14, k);
		}
	}
}
