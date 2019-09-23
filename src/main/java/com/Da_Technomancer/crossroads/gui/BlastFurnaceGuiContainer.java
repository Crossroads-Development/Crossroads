package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.IGuiObject;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.BlastFurnaceTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class BlastFurnaceGuiContainer extends MachineGUI{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/blast_furnace_gui.png");

	public BlastFurnaceGuiContainer(IInventory playerInv, InventoryTE te){
		super(new BlastFurnaceContainer(playerInv, te));
	}

	@Override
	public void initGui(){
		super.initGui();
		guiObjects = new IGuiObject[1];
		guiObjects[0] = new FluidGuiObject(this, 0, 1,4_000, (width - xSize) / 2, (height - ySize) / 2, 63, 70);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(TEXTURE);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 25, guiTop + 38, 176, 0, 38 * te.getField(te.getFieldCount() - 2) / BlastFurnaceTileEntity.REQUIRED_PRG, 14);
		drawRect(guiLeft + 50, guiTop + 36 - te.getField(te.getFieldCount() - 1) * 16 / BlastFurnaceTileEntity.CARBON_LIMIT, guiLeft + 52, guiTop + 36, 0xFF000000);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
