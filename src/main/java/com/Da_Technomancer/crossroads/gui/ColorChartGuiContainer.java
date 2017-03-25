package com.Da_Technomancer.crossroads.gui;

import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.gui.TextBarGuiObject;
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

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/color_chart_gui.png");
	private static final ResourceLocation BLOCKED = new ResourceLocation("textures/items/barrier.png");
	
	private TextBarGuiObject searchBar;
	
	public ColorChartGuiContainer(EntityPlayer player, World world, BlockPos pos){
		super(new ColorChartContainer(player, world, pos));
		xSize = 300;
		ySize = 300;
	}

	@Override
	public void initGui(){
		super.initGui();
		
		searchBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 0, 300, 300, 25, "Filter", (Character key) -> Character.isAlphabetic(key));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawModalRectWithCustomSizedTexture(i, j, 0, 0, xSize, ySize, 300, 300);
		
		searchBar.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String search = searchBar.getText().toUpperCase();
		searchBar.drawFore(mouseX, mouseY, fontRendererObj);
		if(!search.isEmpty()){
			mc.getTextureManager().bindTexture(BLOCKED);
			for(Slot slot : inventorySlots.inventorySlots){
				if(slot.getStack().getItem() != Item.getItemFromBlock(Blocks.BARRIER) && !slot.getStack().getDisplayName().startsWith(search)){
					drawModalRectWithCustomSizedTexture(slot.xPos, slot.yPos, 0, 0, 16, 16, 16, 16);
				}
			}
		}
	}
@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		searchBar.mouseClicked(x, y, button);
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!searchBar.buttonPress(key)){
			super.keyTyped(key, keyCode);
		}
	}
}
