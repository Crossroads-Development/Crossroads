package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleArrayToServer;
import com.Da_Technomancer.crossroads.API.packets.SendIntToServer;
import com.Da_Technomancer.crossroads.API.templates.ButtonGuiObject;
import com.Da_Technomancer.crossroads.API.templates.OutputLogGuiObject;
import com.Da_Technomancer.crossroads.API.templates.TextBarGuiObject;
import com.Da_Technomancer.crossroads.API.templates.ToggleButtonGuiObject;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.RedstoneKeyboardContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class RedstoneRegistryGuiContainer extends GuiContainer{

	private final RedstoneRegistryTileEntity te;
	private TextBarGuiObject textBar;
	private OutputLogGuiObject log;
	private ButtonGuiObject remove;
	private ButtonGuiObject up;
	private ButtonGuiObject down;
	private ButtonGuiObject select;
	private ButtonGuiObject add;

	private ButtonGuiObject clearButton;
	private ToggleButtonGuiObject multButton;
	private ToggleButtonGuiObject divButton;
	private ButtonGuiObject piButton;
	private ButtonGuiObject eulerButton;

	private int index;
	private double[] output;
	private int focus;

	public RedstoneRegistryGuiContainer(RedstoneRegistryTileEntity te){
		super(new RedstoneKeyboardContainer());
		xSize = 320;
		ySize = 120;
		this.te = te;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void initGui(){
		super.initGui();
		index = te.getIndex();
		focus = index;
		output = Arrays.copyOf(te.getOutput(), te.getOutput().length);

		textBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 90, 260, 25, null, (Character key) -> key == '.' || Character.isDigit(key));
		textBar.setText(doubleToString(output[index]));

		log = new OutputLogGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 10, 260, 3, 25);
		log.addText(index + ": " + doubleToString(output[index]), Color.YELLOW);
		updateLog();

		remove = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 290, 30, 20, "-");
		up = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 270, 10, 20, "▲");
		down = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 270, 30, 20, "▼");
		select = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 270, 50, 20, "#");
		add = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 290, 10, 20, "+");

		clearButton = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 70, 20, "C");
		multButton = new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 30, 70, 20, "⨉");
		divButton = new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 50, 70, 20, "÷");
		piButton = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 70, 70, 20, "π");
		eulerButton = new ButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 90, 70, 20, "e");
	}

	private static String doubleToString(double d){
		String out = Double.toString(d);
		if(out.endsWith(".0")){
			out = out.substring(0, out.length() - 2);
		}
		return out;
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		setOutput();
	}

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/redstone_registry_gui.png");

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawModalRectWithCustomSizedTexture(i, j, 0, 0, xSize, ySize, 320, 120);

		textBar.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		log.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		remove.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		up.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		down.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		select.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		add.drawBack(partialTicks, mouseX, mouseY, fontRenderer);

		clearButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		multButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		divButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		piButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		eulerButton.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		textBar.drawFore(mouseX, mouseY, fontRenderer);
		log.drawFore(mouseX, mouseY, fontRenderer);
		remove.drawFore(mouseX, mouseY, fontRenderer);
		up.drawFore(mouseX, mouseY, fontRenderer);
		down.drawFore(mouseX, mouseY, fontRenderer);
		select.drawFore(mouseX, mouseY, fontRenderer);
		add.drawFore(mouseX, mouseY, fontRenderer);

		clearButton.drawFore(mouseX, mouseY, fontRenderer);
		multButton.drawFore(mouseX, mouseY, fontRenderer);
		divButton.drawFore(mouseX, mouseY, fontRenderer);
		piButton.drawFore(mouseX, mouseY, fontRenderer);
		eulerButton.drawFore(mouseX, mouseY, fontRenderer);
	}

	private boolean wasBarSelected;

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(textBar.mouseClicked(x, y, button)){
			if(textBar.isSelected() != wasBarSelected && wasBarSelected){
				setIndexValue();
			}
			wasBarSelected = textBar.isSelected();
			return;
		}
		if(log.mouseClicked(x, y, button)){
			return;
		}
		if(remove.mouseClicked(x, y, button)){
			if(output.length == 1){
				output[0] = 0;
				updateLog();
				return;
			}
			double[] newOutput = new double[output.length - 1];
			for(int i = 0; i < output.length; i++){
				if(i != focus){
					newOutput[i >= focus ? i - 1 : i] = output[i];
				}
			}
			output = newOutput;
			updateLog();
			return;
		}
		if(up.mouseClicked(x, y, button)){
			focus--;
			updateLog();
			return;
		}
		if(down.mouseClicked(x, y, button)){
			focus++;
			updateLog();
			return;
		}
		if(select.mouseClicked(x, y, button)){
			index = focus;
			updateLog();
			return;
		}
		if(add.mouseClicked(x, y, button)){
			if(output.length < 64){
				double[] newOutput = new double[output.length + 1];
				for(int i = 0; i < output.length; i++){
					newOutput[i > focus ? i + 1 : i] = output[i];
				}
				output = newOutput;
				updateLog();
			}
			return;
		}

		if(clearButton.mouseClicked(x, y, button)){
			textBar.setText("0");
			setIndexValue();
		}else if(multButton.mouseClicked(x, y, button)){
			if(multButton.isDepressed()){
				if(divButton.isDepressed()){
					divButton.setDepressed(false);
					textBar.setText(Double.toString(prevValue));
				}
				try{
					prevValue = Double.parseDouble(textBar.getText());
				}catch(NumberFormatException e){
					multButton.setDepressed(false);
				}
				textBar.setText("");
			}else{
				try{
					double value = Double.parseDouble(textBar.getText());
					if(!Double.isFinite(value)){
						textBar.setText(Double.toString(prevValue));
					}else{
						value *= prevValue;
						textBar.setText(Double.toString(value));
						setIndexValue();
					}
				}catch(NumberFormatException e){
					textBar.setText(Double.toString(prevValue));
				}

			}
		}else if(divButton.mouseClicked(x, y, button)){
			if(divButton.isDepressed()){
				if(multButton.isDepressed()){
					multButton.setDepressed(false);
					textBar.setText(Double.toString(prevValue));
				}
				try{
					prevValue = Double.parseDouble(textBar.getText());
				}catch(NumberFormatException e){
					divButton.setDepressed(false);
				}
				textBar.setText("");
			}else{
				try{
					double value = Double.parseDouble(textBar.getText());
					if(Math.abs(value) == 0 || !Double.isFinite(value)){
						textBar.setText(Double.toString(prevValue));
					}else{
						value = prevValue / value;
						textBar.setText(Double.toString(value));
						setIndexValue();
					}
				}catch(NumberFormatException e){
					textBar.setText(Double.toString(prevValue));
				}

			}
		}else if(piButton.mouseClicked(x, y, button)){
			textBar.setText(Double.toString(Math.PI));
			setIndexValue();
		}else if(eulerButton.mouseClicked(x, y, button)){
			textBar.setText(Double.toString(Math.E));
			setIndexValue();
		}
	}

	private double prevValue = 0;

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(key == 0){
			if(keyCode == 200){
				focus--;
				updateLog();
				return;
			}
			if(keyCode == 208){
				focus++;
				updateLog();
				return;
			}
		}
		if(textBar.buttonPress(key, keyCode)){
			if(textBar.isSelected() != wasBarSelected && wasBarSelected){
				setIndexValue();
			}
			wasBarSelected = textBar.isSelected();
		}else{
			super.keyTyped(key, keyCode);
		}
	}

	private void updateLog(){
		focus = Math.max(Math.min(focus, output.length - 1), 0);
		log.clearLog();
		index = Math.min(index, output.length - 1);

		for(int i = 0; i < 3; i++){
			if(focus + i < output.length){
				log.addText((focus + i) + ": " + doubleToString(output[focus + i]), focus + i == index ? Color.YELLOW : null);
			}
		}
		textBar.setText(doubleToString(output[focus]));
	}

	private void setIndexValue(){
		String heldText = textBar.getText();
		double out = 0;
		boolean errored = false;
		try{
			out = Double.parseDouble(heldText);
		}catch(NumberFormatException e){
			errored = true;
		}
		if(!errored){
			out = Math.abs(out);
			output[focus] = out;
		}
		updateLog();
	}

	private void setOutput(){
		if(!Arrays.equals(output, te.getOutput())){
			te.setOutput(output);
			ModPackets.network.sendToServer(new SendDoubleArrayToServer("newOutput", output, te.getPos(), te.getWorld().provider.getDimension()));
		}
		if(index != te.getIndex()){
			te.setIndex(index);
			ModPackets.network.sendToServer(new SendIntToServer(0, index, te.getPos(), te.getWorld().provider.getDimension()));
		}
	}
}
