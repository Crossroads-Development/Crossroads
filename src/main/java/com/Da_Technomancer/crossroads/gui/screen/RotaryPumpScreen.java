package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.gui.container.RotaryPumpContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RotaryPumpScreen extends MachineScreen<RotaryPumpContainer, RotaryPumpTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/rotary_pump_gui.png");

	public RotaryPumpScreen(RotaryPumpContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 70, 70);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
