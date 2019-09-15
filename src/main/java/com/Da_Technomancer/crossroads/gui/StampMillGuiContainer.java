package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.StampMillContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class StampMillGuiContainer extends MachineGUI{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/stamp_mill_gui.png");

	public StampMillGuiContainer(IInventory playerInv, StampMillTileEntity te){
		super(new StampMillContainer(playerInv, te));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(TEXTURE);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);


		//Time meter
		drawTexturedModalRect(i + 55, j + 34, 176, 0, Math.min(66, te.getField(te.getFieldCount() - 2) * 66 / StampMillTileEntity.TIME_LIMIT), 9);
		//Progress meter
		drawTexturedModalRect(i + 55, j + 45, 176, 0, (int) Math.min(66, te.getField(te.getFieldCount() - 1) * 66 / StampMillTileEntity.REQUIRED), 9);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
