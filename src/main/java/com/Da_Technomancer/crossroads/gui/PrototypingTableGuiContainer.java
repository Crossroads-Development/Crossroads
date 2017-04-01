package com.Da_Technomancer.crossroads.gui;

import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.gui.ButtonGuiObject;
import com.Da_Technomancer.crossroads.API.gui.OutputLogGuiObject;
import com.Da_Technomancer.crossroads.API.gui.TextBarGuiObject;
import com.Da_Technomancer.crossroads.gui.container.PrototypingTableContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class PrototypingTableGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID, "textures/gui/container/prototype_table_gui.png");
	
	private PrototypingTableTileEntity te;
	private IInventory playerInv;
	
	private TextBarGuiObject textBar;
	private ButtonGuiObject button;
	private OutputLogGuiObject log;

	public PrototypingTableGuiContainer(IInventory playerInv, PrototypingTableTileEntity te){
		super(new PrototypingTableContainer(playerInv, te));
		this.te = te;
		this.playerInv = playerInv;

		this.xSize = 176;
		this.ySize = 214;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		textBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 8, 98, 120, 25, "Name", (Character key) -> Character.isAlphabetic(key) || Character.isDigit(key) || key == ' ');
		button = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 8, 76, 70, "Prototype");
		log = new OutputLogGuiObject((width - xSize) / 2, (height - ySize) / 2, 8, 5, 160, 3, 35);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		
		textBar.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
		button.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
		log.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 120, 4210752);
		
		textBar.drawFore(mouseX, mouseY, fontRendererObj);
		log.drawFore(mouseX, mouseY, fontRendererObj);
		button.drawFore(mouseX, mouseY, fontRendererObj);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(textBar.mouseClicked(x, y, button)){
			return;
		}
		
		if(this.button.mouseClicked(x, y, button) && inventorySlots.getSlot(0).getHasStack() && !inventorySlots.getSlot(2).getHasStack()){
			//TODO the actual main purpose of this machine
		}
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!textBar.buttonPress(key)){
			super.keyTyped(key, keyCode);
		}
	}
	
	public OutputLogGuiObject getLog(){
		return log;
	}
}
