package com.Da_Technomancer.crossroads.gui;

import java.awt.Color;
import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColorChartGuiContainer extends GuiContainer{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/colorChartGui.png");
	private static final ResourceLocation BLOCKED = new ResourceLocation("textures/items/barrier.png");
	private static final ResourceLocation BAR = new ResourceLocation(Main.MODID, "textures/gui/container/searchBar.png");

	public ColorChartGuiContainer(EntityPlayer player, World world, BlockPos pos){
		super(new ColorChartContainer(player, world, pos));
		xSize = 300;
		ySize = 300;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawModalRectWithCustomSizedTexture(i, j, 0, 0, xSize, ySize, 300, 300);
		mc.getTextureManager().bindTexture(BAR);
		if(searchSelect){
			GlStateManager.color(1, 1, 0);
		}
		drawModalRectWithCustomSizedTexture(i, j + 300, 0, 0, 300, 20, 300, 20);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		if(!search.isEmpty()){
			GlStateManager.color(1, 1, 1);
			drawString(fontRendererObj, search, 5, 305, Color.WHITE.getRGB());
			mc.getTextureManager().bindTexture(BLOCKED);

			for(Slot slot : inventorySlots.inventorySlots){
				if(slot.getStack().getItem() != Item.getItemFromBlock(Blocks.BARRIER) && !slot.getStack().getDisplayName().startsWith(search.toUpperCase())){
					drawModalRectWithCustomSizedTexture(slot.xPos, slot.yPos, 0, 0, 16, 16, 16, 16);
				}
			}
		}
	}

	private String search = "";
	private boolean searchSelect;

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		if(x >= i && x <= i + 300 && y >= j + 300 && y <= j + 320){
			searchSelect = !searchSelect;
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
				if(!search.isEmpty()){
					search = search.substring(0, search.length() - 1);
					return;
				}
			}else{
				if(Character.isAlphabetic(key) && search.length() <= 25){
					search += key;
					return;
				}
			}
		}
		super.keyTyped(key, keyCode);
	}
}
