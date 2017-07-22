package com.Da_Technomancer.crossroads.gui;

import java.io.IOException;

import com.Da_Technomancer.crossroads.API.gui.TextBarGuiObject;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToServer;
import com.Da_Technomancer.crossroads.gui.container.RedstoneKeyboardContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneKeyboardTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;

public class RedstoneKeyboardGuiContainer extends GuiContainer{

	private final RedstoneKeyboardTileEntity te;
	private TextBarGuiObject textBar;

	public RedstoneKeyboardGuiContainer(RedstoneKeyboardTileEntity te){
		super(new RedstoneKeyboardContainer());
		xSize = 300;
		ySize = 20;
		this.te = te;
	}

	@Override
	public void initGui(){
		super.initGui();

		textBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 0, 0, 300, 25, null, (Character key) -> Character.isAlphabetic(key) || key == '.' || Character.isDigit(key));
		textBar.setText(doubleToString(te.output));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	private static String doubleToString(double d){
		String out = Double.toString(d);
		if(out.endsWith(".0")){
			out = out.substring(0, out.length() - 2);
		}
		return d == Math.PI ? "PI" : out;
	}
	
	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		setOutput();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		textBar.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		textBar.drawFore(mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		textBar.mouseClicked(x, y, button);
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!textBar.buttonPress(key, keyCode)){
			super.keyTyped(key, keyCode);
		}
	}

	private void setOutput(){
		double out = 0;
		boolean changed;
		if(textBar.getText().toLowerCase().equals("pi")){
			changed = true;
			out = Math.PI;
		}else{
			try{
				out = Double.parseDouble(textBar.getText());
				changed = true;
			}catch(NumberFormatException e){
				changed = false;
			}
		}
		out = Math.abs(out);
		if(changed && out != te.output){
			te.output = out;
			ModPackets.network.sendToServer(new SendDoubleToServer("newOutput", out, te.getPos(), te.getWorld().provider.getDimension()));
		}
	}
}
