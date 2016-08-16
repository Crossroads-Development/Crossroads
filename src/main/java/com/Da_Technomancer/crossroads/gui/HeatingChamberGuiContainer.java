package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.container.HeatingChamberContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingChamberTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class HeatingChamberGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/heatingChamberGui.png");

	private IInventory playerInv;
	private HeatingChamberTileEntity te;

	public HeatingChamberGuiContainer(IInventory playerInv, HeatingChamberTileEntity te){
		super(new HeatingChamberContainer(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		this.drawTexturedModalRect(i + 79, j + 34, 176, 0, getBurnLeftScaled(24), 17);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = this.te.getDisplayName().getUnformattedText();
		this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);
	}

	private int getBurnLeftScaled(int pixels){
		return this.te.getField(0) * pixels / HeatingChamberTileEntity.REQUIRED;
	}

}
