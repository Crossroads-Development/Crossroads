package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.HydroponicsTroughTileEntity;
import com.Da_Technomancer.crossroads.gui.container.HydroponicsTroughContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HydroponicsTroughScreen extends MachineScreen<HydroponicsTroughContainer, HydroponicsTroughTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/hydroponics_trough_gui.png");

	public HydroponicsTroughScreen(HydroponicsTroughContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();

		initFluidManager(0, 28, 66);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrix.blit(TEXTURE, leftPos + 61, topPos + 33, 176, 0, 72, menu.progRef.get() * 18 / 100);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
