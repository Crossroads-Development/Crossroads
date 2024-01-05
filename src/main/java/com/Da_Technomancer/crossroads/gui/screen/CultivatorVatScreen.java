package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.CultivatorVatTileEntity;
import com.Da_Technomancer.crossroads.gui.container.CultivatorVatContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CultivatorVatScreen extends MachineScreen<CultivatorVatContainer, CultivatorVatTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/cultivator_vat_gui.png");

	public CultivatorVatScreen(CultivatorVatContainer container, Inventory playerInv, Component name){
		super(container, playerInv, name);
	}

	@Override
	protected void init(){
		super.init();
		initFluidManager(0, 30, 70);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		matrix.blit(TEXTURE, leftPos + 79, topPos + 17, 176, 0, menu.progressRef.get() * 54 / CultivatorVatTileEntity.REQUIRED_PROGRESS, 54);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
