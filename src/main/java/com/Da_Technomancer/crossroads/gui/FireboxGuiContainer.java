package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.FireboxContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.FireboxTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class FireboxGuiContainer extends MachineGUI{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/coal_heater_gui.png");

	public FireboxGuiContainer(IInventory playerInv, FireboxTileEntity te){
		super(new FireboxContainer(playerInv, te));

		xSize = 176;
		ySize = 136;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

		int k = getBurnLeftScaled();
		if(k != 0){
			drawTexturedModalRect(i + 81, j + 6 + 13 - k, 176, 13 - k, 14, k);
		}
	}
	
	private int getBurnLeftScaled(){
		int burnTime = te.getField(0);
		return burnTime == 0 ? 0 : 1 + (te.getField(0) * 13 / 1600);
	}
}
