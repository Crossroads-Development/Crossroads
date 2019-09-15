package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToServer;
import com.Da_Technomancer.crossroads.API.templates.RadioButtonGuiObject;
import com.Da_Technomancer.crossroads.API.templates.TexturedToggleButtonGuiObject;
import com.Da_Technomancer.crossroads.API.templates.ToggleButtonGuiObject;
import com.Da_Technomancer.crossroads.gui.container.BlankContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MathAxisTileEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import java.io.IOException;

public class MathAxisGuiContainer extends ContainerScreen{

	private final MathAxisTileEntity te;
	private ToggleButtonGuiObject[] buttonArray = new ToggleButtonGuiObject[MathAxisTileEntity.Mode.values().length];
	private RadioButtonGuiObject buttons;

	public MathAxisGuiContainer(MathAxisTileEntity te){
		super(new BlankContainer());
		xSize = 100;
		ySize = 100;
		this.te = te;
	}

	@Override
	public void initGui(){
		super.initGui();

		int buttonWidth = 20;
		int buttonHeight = 20;
		int perRow = xSize / buttonWidth;

		for(int i = 0; i < buttonArray.length; i++){
			buttonArray[i] = new TexturedToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, buttonWidth * (i % perRow), buttonHeight * (i / perRow), buttonWidth, MathAxisTileEntity.Mode.values()[i].getSprite(), 0, 0, 16, 16, 16, 16);
		}

		buttons = new RadioButtonGuiObject(buttonArray);
		buttons.setPressed(te.getMode().ordinal());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		if(buttons.mouseOver(mouseX, mouseY)){
			MathAxisTileEntity.Mode mode = te.getMode();

			for(int i = 0; i < buttonArray.length; i++){
				if(buttonArray[i].mouseOver(mouseX, mouseY)){
					mode = MathAxisTileEntity.Mode.values()[i];
					break;
				}
			}

			String key = "mode." + mode.toString().toLowerCase();

			drawHoveringText(ImmutableList.of(MiscUtil.localize(key), MiscUtil.localize(key + ".desc")), mouseX, mouseY);
		}
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		setMode();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		buttons.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		buttons.drawFore(mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(buttons.mouseClicked(x, y, button)){
			setMode();
		}
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!buttons.buttonPress(key, keyCode)){
			super.keyTyped(key, keyCode);
		}
	}

	private void setMode(){
		int mode = buttons.getPressed();
		if(mode != te.getMode().ordinal()){
			te.setMode(MathAxisTileEntity.Mode.values()[mode]);
			ModPackets.network.sendToServer(new SendIntToServer((byte) 0, mode, te.getPos(), te.getWorld().provider.getDimension()));
		}
	}
}
