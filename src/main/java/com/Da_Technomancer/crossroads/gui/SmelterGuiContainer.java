package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.SmelterContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class SmelterGuiContainer extends MachineGUI{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID, "textures/gui/container/heating_chamber_gui.png");

	public SmelterGuiContainer(IInventory playerInv, SmelterTileEntity te){
		super(new SmelterContainer(playerInv, te));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

		drawTexturedModalRect(i + 79, j + 34, 176, 0, te.getField(te.getFieldCount() - 1) * 24 / SmelterTileEntity.REQUIRED, 17);
	}

}
