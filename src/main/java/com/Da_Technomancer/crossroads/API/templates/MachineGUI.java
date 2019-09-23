package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;

public abstract class MachineGUI extends ContainerScreen<MachineContainer>{

	protected InventoryTE te;
	protected PlayerInventory playerInv;
	protected ArrayList<String> tooltip = new ArrayList<>();

	protected MachineGUI(MachineContainer container, PlayerInventory playerInventory, ITextComponent text){
		super(container, playerInventory, text);
		this.te = container.te;
		this.playerInv = playerInventory;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		if(getSlotUnderMouse() == null){
			renderTooltip(tooltip, mouseX, mouseY);
		}
		tooltip.clear();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		for(IGuiEventListener gui : children){
			if(gui instanceof IGuiObject){
				((IGuiObject) gui).drawBack(partialTicks, mouseX, mouseY, font);
			}
		}
		for(FluidSlotManager manager : te.fluidManagers){
			manager.renderBack(partialTicks, mouseX, mouseY, font);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		for(IGuiEventListener gui : children){
			if(gui instanceof IGuiObject){
				((IGuiObject) gui).drawFore(mouseX, mouseY, font);
			}
		}

		font.drawString(title.getFormattedText(), 8, 6, 0x404040);
		font.drawString(playerInv.getDisplayName().getFormattedText(), container.getInvStart()[0], container.getInvStart()[1] - 12, 4210752);


		if(te.heatReference != null){
			String s = te.heatReference.get() + "°C";
			font.drawString(s, xSize - 8 - font.getStringWidth(s), 6, 0x404040);
		}
		if(te.rotaryReference != null){
			String s = te.rotaryReference.get() / 100D + " rad/s";
			font.drawString(s, xSize - 8 - font.getStringWidth(s), te.useHeat() ? 16 : 6, 0x404040);
		}

		for(FluidSlotManager manager : te.fluidManagers){
			manager.renderFore(mouseX, mouseY, font, tooltip);
		}
	}
}
