package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendStringToServer;
import com.Da_Technomancer.crossroads.API.templates.*;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.PrototypingTableContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class PrototypingTableGuiContainer extends MachineGUI implements OutputLogGuiObject.ILogUser{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/prototype_table_gui.png");
	private TextBarGuiObject textBar;
	private ButtonGuiObject button;
	private ToggleButtonGuiObject disp;
	private OutputLogGuiObject log;

	public PrototypingTableGuiContainer(IInventory playerInv, PrototypingTableTileEntity te){
		super(new PrototypingTableContainer(playerInv, te));

		xSize = 176;
		ySize = 214;
	}
	
	@Override
	public void init(){
		super.init();
		textBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 8, 98, 120, 18, "Name", (Character key) -> StringUtils.isAsciiPrintable(String.valueOf(key)));
		button = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 8, 76, 70, "Prototype");
		log = new OutputLogGuiObject((width - xSize) / 2, (height - ySize) / 2, 8, 5, 160, 3, 60);
		disp = new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 80, 76, 30, "Show");
		disp.setDepressed(((PrototypingTableTileEntity) te).visible);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);

		textBar.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		button.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		log.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		disp.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		textBar.drawFore(mouseX, mouseY, fontRenderer);
		log.drawFore(mouseX, mouseY, fontRenderer);
		button.drawFore(mouseX, mouseY, fontRenderer);
		disp.drawFore(mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(textBar.mouseClicked(x, y, button)){
			return;
		}

		if(this.button.mouseClicked(x, y, button) && !inventorySlots.getSlot(2).getHasStack()){
			if(inventorySlots.getSlot(0).getHasStack()){
				if(!textBar.getText().isEmpty()){
					CrossroadsPackets.network.sendToServer(new SendStringToServer("create", textBar.getText(), te.getPos(), te.getWorld().provider.getDimension()));
				}else{
					log.addText("Name required.", null);
				}
			}else{
				log.addText("Insufficient Copshowium.", null);
			}
		}else if(disp.mouseClicked(x, y, button)){
			((PrototypingTableTileEntity) te).visible = !((PrototypingTableTileEntity) te).visible;
		}
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!textBar.charTyped(key, keyCode)){
			super.keyTyped(key, keyCode);
		}
	}

	@Override
	public OutputLogGuiObject getLog(String name){
		return name.equals("prototypeCreate") ? log : null;
	}
}
