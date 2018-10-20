package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class MillstoneGuiContainer extends GuiContainer{

	private IInventory playerInv;
	private MillstoneTileEntity te;
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/millstone_gui.png");

	public MillstoneGuiContainer(IInventory playerInv, MillstoneTileEntity te){
		super(new MillstoneContainer(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;

		xSize = 176;
		ySize = 166;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 66, guiTop + 35, 176, 0, 44, getScaledProgress());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = I18n.format("container.millstone");
		fontRenderer.drawString(s, 88 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);
	}

	private int getScaledProgress(){
		return (int) Math.ceil(te.getProgress() * 17 / MillstoneTileEntity.REQUIRED);
	}

}
