package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.FluidGuiObject;
import com.Da_Technomancer.crossroads.API.templates.IGuiObject;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class FluidCoolerGuiContainer extends MachineGUI{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/container/fluid_cooler_gui.png");

	public FluidCoolerGuiContainer(IInventory playerInv, InventoryTE te){
		super(new FluidCoolerContainer(playerInv, te));
	}

	@Override
	public void initGui(){
		super.initGui();
		guiObjects = new IGuiObject[1];
		guiObjects[0] = new FluidGuiObject(this, 0, 1,4_000, (width - xSize) / 2, (height - ySize) / 2, 10, 70);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(TEXTURE);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);


		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
