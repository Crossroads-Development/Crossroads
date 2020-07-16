package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public abstract class MachineGUI<T extends MachineContainer<U>, U extends InventoryTE> extends TileEntityGUI<T, U>{

	protected U te;

	protected MachineGUI(T container, PlayerInventory playerInventory, ITextComponent text){
		super(container, playerInventory, text);
		this.te = container.te;
	}

	/**
	 * Helper function to init a fluid manager on a per-screen level
	 * @param index The index of the fluid manager
	 * @param x The x position for it to start
	 * @param y The y position for it to start
	 */
	protected void initFluidManager(int index, int x, int y){
		te.fluidManagers[index].initScreen(guiLeft, guiTop, x, y, container.fluidManagerRefs[index][0], container.fluidManagerRefs[index][1]);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		super.drawGuiContainerBackgroundLayer(matrix, partialTicks, mouseX, mouseY);

		for(FluidSlotManager manager : te.fluidManagers){
			manager.render(matrix, partialTicks, mouseX, mouseY, font, tooltip);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY){
		super.drawGuiContainerForegroundLayer(matrix, mouseX, mouseY);

		if(container.heatRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.temp", container.heatRef.get());
			font.drawString(matrix, s, xSize - 8 - font.getStringWidth(s), 6, 0x404040);
		}
		if(container.rotRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.speed", container.rotRef.get() / 100D);
			font.drawString(matrix, s, xSize - 8 - font.getStringWidth(s), te.useHeat() ? 16 : 6, 0x404040);
		}
	}
}
