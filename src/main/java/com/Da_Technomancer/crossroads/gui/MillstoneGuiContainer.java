package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class MillstoneGuiContainer extends MachineGUI{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/millstone_gui.png");

	public MillstoneGuiContainer(IInventory playerInv, MillstoneTileEntity te){
		super(new MillstoneContainer(playerInv, te));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 66, guiTop + 35, 176, 0, 44, getScaledProgress());
	}

	private int getScaledProgress(){
		return (int) Math.ceil(te.getField(0) * 17 / MillstoneTileEntity.REQUIRED);
	}

}
