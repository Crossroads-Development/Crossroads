package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class MillstoneGuiContainer extends MachineGUI{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/millstone_gui.png");

	public MillstoneGuiContainer(IInventory playerInv, MillstoneTileEntity te){
		super(new MillstoneContainer(playerInv, te));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
		blit(guiLeft + 66, guiTop + 35, 176, 0, 44, (int) Math.ceil(te.getField(te.getFieldCount() - 1) * 17 / MillstoneTileEntity.REQUIRED));
	}
}
