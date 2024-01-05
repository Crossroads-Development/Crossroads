package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentFilterTileEntity;
import com.Da_Technomancer.crossroads.gui.container.ReagentFilterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReagentFilterScreen extends com.Da_Technomancer.essentials.api.BlockMenuScreen<ReagentFilterContainer, ReagentFilterTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/reagent_filter_gui.png");

	public ReagentFilterScreen(ReagentFilterContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		super.renderBg(matrix, partialTicks, mouseX, mouseY);

		RenderSystem.setShaderColor(1, 1, 1, 1);

		matrix.blit(GUI_TEXTURES, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
