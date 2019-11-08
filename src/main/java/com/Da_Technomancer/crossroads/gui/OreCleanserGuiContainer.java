package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.IGuiObject;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class OreCleanserGuiContainer extends MachineGUI{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/ore_cleanser_gui.png");

	public OreCleanserGuiContainer(IInventory playerInv, InventoryTE te){
		super(new OreCleanserContainer(playerInv, te));
	}

	@Override
	public void init(){
		super.init();
		guiObjects = new IGuiObject[2];
		guiObjects[0] = new FluidGuiObject(this, 0, 1,1_000, (width - xSize) / 2, (height - ySize) / 2, 8, 70);
		guiObjects[1] = new FluidGuiObject(this, 2, 3,1_000, (width - xSize) / 2, (height - ySize) / 2, 62, 70);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(TEXTURE);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);
		blit(guiLeft + 25, guiTop + 21, 176, 0, 36 * te.getField(te.getFieldCount() - 1) / 50, 10);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
