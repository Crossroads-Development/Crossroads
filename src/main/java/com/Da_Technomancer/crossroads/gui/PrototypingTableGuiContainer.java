package com.Da_Technomancer.crossroads.gui;

import java.awt.Color;
import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.PrototypingTableContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class PrototypingTableGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID, "textures/gui/container/prototype_table_gui.png");
	private static final ResourceLocation BAR = new ResourceLocation(Main.MODID, "textures/gui/container/search_bar.png");

	private PrototypingTableTileEntity te;
	private IInventory playerInv;

	public PrototypingTableGuiContainer(IInventory playerInv, PrototypingTableTileEntity te){
		super(new PrototypingTableContainer(playerInv, te));
		this.te = te;
		this.playerInv = playerInv;

		this.xSize = 176;
		this.ySize = 214;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		
		mc.getTextureManager().bindTexture(BAR);
		if(searchSelect){
			GlStateManager.color(1, 1, 0);
		}
		drawModalRectWithCustomSizedTexture(i + 8, j + 100, 0, 0, 120, 20, 120, 20);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 120, 4210752);
		
		//Name
		drawString(fontRendererObj, name.isEmpty() ? "Name" : name, 13, 106, name.isEmpty() ? Color.GRAY.getRGB() : Color.WHITE.getRGB());
		
		//Log
		drawString(fontRendererObj, log[0], 10, 10, Color.WHITE.getRGB());
		drawString(fontRendererObj, log[1], 10, 25, Color.WHITE.getRGB());
		drawString(fontRendererObj, log[2], 10, 40, Color.WHITE.getRGB());
		
		//Prototype Button
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		if(!searchSelect && mouseX >= i + 8 && mouseX <= i + 79 && mouseY >= j + 76 && mouseY <= j + 96){
			drawString(fontRendererObj, "Prototype", 13, 82, Color.YELLOW.getRGB());
		}else{
			drawString(fontRendererObj, "Prototype", 13, 82, Color.WHITE.getRGB());
		}
	}
	
	private String name = "";
	private boolean searchSelect;
	
	private String[] log = new String[] {"TEST 1", "TEST 2", "TEST 3"};//TODO Log management

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		if(x >= i + 8 && x <= i + 128 && y >= j + 100 && y <= j + 119){
			searchSelect = !searchSelect;
		}
		
		if(!searchSelect && x >= i + 8 && x <= i + 79 && y >= j + 76 && y <= j + 96){
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			//TODO the actual main purpose of this machine
		}
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(searchSelect){
			//Enter & Esc
			if(key == 13 || key == 27){
				searchSelect = false;
				return;
			//Backspace
			}else if(key == 8){
				if(!name.isEmpty()){
					name = name.substring(0, name.length() - 1);
					return;
				}
			}else{
				if(Character.isAlphabetic(key) || Character.isDigit(key) || key == ' '){
					if(name.length() <= 25){
						name += key;
					}
					return;
				}
			}
		}
		super.keyTyped(key, keyCode);
	}
}
