package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.gui.container.CrucibleContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrucibleScreen extends MachineScreen<CrucibleContainer, HeatingCrucibleTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fat_collector_gui.png");

	public CrucibleScreen(CrucibleContainer container, Inventory playerInv, Component name){
		super(container, playerInv, name);
	}

	@Override
	protected void init(){
		super.init();
		initFluidManager(0, 70, 70);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrix.blit(TEXTURE, leftPos + 42, topPos + 35, 176, 0, menu.meltProgress.get() * 28 / HeatingCrucibleTileEntity.REQUIRED, 18);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
