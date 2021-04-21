package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.TileEntityGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BeamExtractorContainer;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamExtractorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BeamExtractorScreen extends TileEntityGUI<BeamExtractorContainer, BeamExtractorTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID + ":textures/gui/container/arcane_extractor_gui.png");

	public BeamExtractorScreen(BeamExtractorContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		super.renderBg(matrix, partialTicks, mouseX, mouseY);

		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURES);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		int prog = menu.progRef.get();
		int k = prog == 0 ? 0 : 1 + (prog * 13 / 100);
		if(k != 0){
			blit(matrix, leftPos + 81, topPos + 43 - k, 176, 13 - k, 14, k);
		}
	}
}
