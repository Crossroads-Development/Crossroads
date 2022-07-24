package com.Da_Technomancer.crossroads.api.templates;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.essentials.api.BlockMenuScreen;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class MachineScreen<T extends MachineContainer<U>, U extends InventoryTE> extends BlockMenuScreen<T, U>{

	protected U te;

	protected MachineScreen(T container, Inventory playerInventory, Component text){
		super(container, playerInventory, text);
		this.te = container.getTE();
	}

	/**
	 * Helper function to init a fluid manager on a per-screen level
	 * @param index The index of the fluid manager
	 * @param x The x position for it to start
	 * @param y The y position for it to start
	 */
	protected void initFluidManager(int index, int x, int y){
		te.fluidManagers[index].initScreen(leftPos, topPos, x, y, menu.fluidManagerRefs[index][0], menu.fluidManagerRefs[index][1]);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		super.renderBg(matrix, partialTicks, mouseX, mouseY);

		for(FluidSlotManager manager : te.fluidManagers){
			manager.render(matrix, partialTicks, mouseX, mouseY, font, tooltip);
		}
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		if(menu.heatRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.temp", menu.heatRef.get());
			font.draw(matrix, s, imageWidth - 8 - font.width(s), 6, 0x404040);
		}
		if(menu.rotRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.speed", menu.rotRef.get() / 100D);
			font.draw(matrix, s, imageWidth - 8 - font.width(s), te.useHeat() ? 16 : 6, 0x404040);
		}
	}
}
