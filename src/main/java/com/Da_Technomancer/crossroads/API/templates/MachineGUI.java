package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
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
		te.fluidManagers[index].initScreen((width - xSize) / 2, (height - ySize) / 2, x, y, container.fluidManagerRefs[index][0], container.fluidManagerRefs[index][1]);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		for(FluidSlotManager manager : te.fluidManagers){
			manager.renderBack(partialTicks, mouseX, mouseY, font);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		if(container.heatRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.temp", container.heatRef.get());
			font.drawString(s, xSize - 8 - font.getStringWidth(s), 6, 0x404040);
		}
		if(container.rotRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.speed", container.rotRef.get() / 100D);
			font.drawString(s, xSize - 8 - font.getStringWidth(s), te.useHeat() ? 16 : 6, 0x404040);
		}

		for(FluidSlotManager manager : te.fluidManagers){
			manager.renderFore(mouseX, mouseY, font, tooltip);
		}
	}
}
