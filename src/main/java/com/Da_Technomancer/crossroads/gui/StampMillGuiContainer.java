package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.gui.container.StampMillContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;

public class StampMillGuiContainer extends GuiContainer{

	private StampMillTileEntity te;
	private IInventory playerInv;

	public StampMillGuiContainer(IInventory playerInv, StampMillTileEntity te){
		super(new StampMillContainer(playerInv, te));
		this.te = te;
		this.playerInv = playerInv;
		//TODO
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		//TODO
	}
}
