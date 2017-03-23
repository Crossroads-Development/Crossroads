package com.Da_Technomancer.crossroads.gui;

import java.awt.Color;
import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToServer;
import com.Da_Technomancer.crossroads.gui.container.RedstoneKeyboardContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneKeyboardTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class RedstoneKeyboardGuiContainer extends GuiContainer{

	private static final ResourceLocation BAR = new ResourceLocation(Main.MODID, "textures/gui/container/search_bar.png");
	private final RedstoneKeyboardTileEntity te;
	
	public RedstoneKeyboardGuiContainer(RedstoneKeyboardTileEntity te){
		super(new RedstoneKeyboardContainer());
		xSize = 300;
		ySize = 20;
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		mc.getTextureManager().bindTexture(BAR);
		if(searchSelect){
			GlStateManager.color(1, 1, 0);
		}
		drawModalRectWithCustomSizedTexture(i, j, 0, 0, xSize, ySize, 300, 20);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		drawString(fontRendererObj, searchSelect ? search : te.output == Math.PI ? "PI" : Double.toString(te.output), 5, 5, Color.WHITE.getRGB());
	}

	private String search = "";
	private boolean searchSelect;

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		if(x >= i && x <= i + 300 && y >= j && y <= j + 20){
			if(searchSelect == true){
				setOutput();
			}else{
				search = te.output == Math.PI ? "PI" : Double.toString(te.output);
			}
			searchSelect = !searchSelect;
		}
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(searchSelect){
			//Enter & Esc
			if(key == 13 || key == 27){
				searchSelect = false;
				setOutput();
				return;
			//Backspace
			}else if(key == 8){
				if(!search.isEmpty()){
					search = search.substring(0, search.length() - 1);
					return;
				}
			}else{
				if((Character.isAlphabetic(key) || key == '.' || Character.isDigit(key))){
					if(search.length() <= 25){
						search += key;
					}
					return;
				}
			}
		}
		super.keyTyped(key, keyCode);
	}
	
	private void setOutput(){
		double out = 0;
		boolean changed;
		if(search.toLowerCase().equals("pi")){
			changed = true;
			out = Math.PI;
		}else{
			try{
				out = Double.parseDouble(search);
				changed = true;
			}catch(NumberFormatException e){
				changed = false;
			}
		}
		if(changed){
			out = Math.abs(out);
			te.output = out;
			ModPackets.network.sendToServer(new SendDoubleToServer("newOutput", out, te.getPos(), te.getWorld().provider.getDimension()));
		}
	}
}
