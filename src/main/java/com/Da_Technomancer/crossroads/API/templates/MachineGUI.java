package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;

import java.io.IOException;
import java.util.ArrayList;

public abstract class MachineGUI extends GuiContainer{

	protected InventoryTE te;
	protected IInventory playerInv;
	protected ArrayList<String> tooltip = new ArrayList<>();
	protected IGuiObject[] guiObjects = new IGuiObject[0];

	protected MachineGUI(MachineContainer container){
		super(container);
		this.te = container.te;
		this.playerInv = container.playerInv;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		if(getSlotUnderMouse() == null){
			drawHoveringText(tooltip, mouseX, mouseY);
		}
		tooltip.clear();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		for(IGuiObject gui : guiObjects){
			if(gui == null){
				continue;
			}
			gui.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		for(IGuiObject gui : guiObjects){
			if(gui == null){
				continue;
			}
			gui.drawFore(mouseX, mouseY, fontRenderer);
		}

		fontRenderer.drawString(te.getDisplayName().getUnformattedText(), 8, 6, 0x404040);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), ((MachineContainer) inventorySlots).getInvStart()[0], ((MachineContainer) inventorySlots).getInvStart()[1] - 12, 4210752);


		if(te.useHeat()){
			String s = te.getField(2 * te.fluidTanks()) + "°C";
			fontRenderer.drawString(s, xSize - 8 - fontRenderer.getStringWidth(s), 6, 0x404040);
		}
		if(te.useRotary()){
			String s = te.getField(2 * te.fluidTanks() + (te.useHeat() ? 1 : 0)) / 100D + " rad/s";
			fontRenderer.drawString(s, xSize - 8 - fontRenderer.getStringWidth(s), te.useHeat() ? 16 : 6, 0x404040);
		}
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		for(IGuiObject gui : guiObjects){
			if(gui != null && gui.buttonPress(key, keyCode)){
				return;
			}
		}

		super.keyTyped(key, keyCode);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
		super.mouseClicked(x, y, button);
		for(IGuiObject gui : guiObjects){
			if(gui != null && gui.mouseClicked(x, y, button)){
				trigger(gui);
				return;
			}
		}
	}

	protected void trigger(IGuiObject clickedObject){

	}
}
