package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.FluidGuiObject;
import com.Da_Technomancer.crossroads.API.templates.IGuiObject;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.FatFeederContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class FatFeederGuiContainer extends MachineGUI{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/container/fat_feeder_gui.png");

	public FatFeederGuiContainer(IInventory playerInv, InventoryTE te){
		super(new FatFeederContainer(playerInv, te));
	}

	@Override
	public void initGui(){
		super.initGui();
		guiObjects = new IGuiObject[1];
		guiObjects[0] = new FluidGuiObject(this, 0, 1,10_000, (width - xSize) / 2, (height - ySize) / 2, 80, 71);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
