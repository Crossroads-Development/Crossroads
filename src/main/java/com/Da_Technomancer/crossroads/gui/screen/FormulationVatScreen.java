package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.FormulationVatTileEntity;
import com.Da_Technomancer.crossroads.gui.container.FormulationVatContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FormulationVatScreen extends MachineScreen<FormulationVatContainer, FormulationVatTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/formulation_vat_gui.png");

	public FormulationVatScreen(FormulationVatContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();

		initFluidManager(0, 9, 64);
		initFluidManager(1, 79, 64);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrix.blit(TEXTURE, leftPos + 25, topPos + 35, 176, 0, menu.craftProgress.get() * 54 / FormulationVatTileEntity.REQUIRED, 19);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
