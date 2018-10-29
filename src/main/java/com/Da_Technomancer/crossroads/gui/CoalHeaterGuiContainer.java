package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.CoalHeaterContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.FuelHeaterTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class CoalHeaterGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/coal_heater_gui.png");

	private FuelHeaterTileEntity te;
	private IInventory playerInv;

	public CoalHeaterGuiContainer(IInventory playerInv, FuelHeaterTileEntity te){
		super(new CoalHeaterContainer(playerInv, te));
		this.te = te;
		this.playerInv = playerInv;

		this.xSize = 176;
		this.ySize = 136;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		int k = getBurnLeftScaled();
		if(k != 0){
			drawTexturedModalRect(i + 81, j + 6 + 13 - k, 176, 13 - k, 14, k);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = te.getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, 136 - fontRenderer.getStringWidth(s) / 2, 42, 4210752);
		fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 42, 4210752);
	}

	private int getBurnLeftScaled(){
		int burnTime = te.getField(0);
		return burnTime == 0 ? 0 : 1 + (te.getField(0) * 13 / 1600);
	}

}
