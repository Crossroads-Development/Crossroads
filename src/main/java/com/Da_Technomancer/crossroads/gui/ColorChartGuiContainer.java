package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.TextBarGuiObject;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.io.IOException;

public class ColorChartGuiContainer extends ContainerScreen{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_gui.png");
	private static final ResourceLocation BACKGROUND_MONO = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_mono_gui.png");
	private static final int xCENTER = 150;
	private static final int yCENTER = 150;
	private static final int RADIUS = 138;

	private TextBarGuiObject searchBar;

	public ColorChartGuiContainer(PlayerEntity player, World world, BlockPos pos){
		super(new ColorChartContainer(player, world, pos));
		xSize = 300;
		ySize = 300;
	}

	@Override
	public void init(){
		super.init();

		searchBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 0, 300, 300, 25, "Filter", Character::isAlphabetic);
	}

	private static Color getColor(int x, int y){
		return Color.getHSBColor(((float) (Math.atan2(y - yCENTER, x - xCENTER) / (2D * Math.PI))), (float) Math.min(Math.sqrt(Math.pow(x - xCENTER, 2) + Math.pow(y - yCENTER, 2)) / RADIUS, 1), 1);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		if(Math.pow(xCENTER - mouseX + guiLeft, 2) + Math.pow(yCENTER - mouseY + guiTop, 2) <= RADIUS * RADIUS){
			Color col = getColor(mouseX - guiLeft, mouseY - guiTop);
			EnumBeamAlignments elem = EnumBeamAlignments.getAlignment(col);
			CompoundNBT elementTag = StoreNBTToClient.clientPlayerTag.getCompound("elements");
			drawHoveringText(ImmutableList.of(elementTag.contains(elem.name()) ? elem.getLocalName(false) : "???", "R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue()), mouseX, mouseY, fontRenderer);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND_MONO);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawModalRectWithCustomSizedTexture(i, j, 0, 0, xSize, ySize, 300, 300);

		searchBar.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String search = searchBar.getText().toUpperCase();
		searchBar.drawFore(mouseX, mouseY, fontRenderer);

		mc.getTextureManager().bindTexture(BACKGROUND);
		int spotLength = 2;

		for(int i = 0; i < 2 * RADIUS / spotLength; i++){
			for(int j = 0; j < 2 * RADIUS / spotLength; j++){
				int xPos = spotLength * i + xCENTER - RADIUS;
				int yPos = spotLength * j + yCENTER - RADIUS;
				if(Math.pow(RADIUS - (spotLength * i), 2) + Math.pow(RADIUS - (spotLength * j), 2) <= RADIUS * RADIUS){
					EnumBeamAlignments elem = EnumBeamAlignments.getAlignment(getColor(xPos, yPos));
					CompoundNBT elementTag = StoreNBTToClient.clientPlayerTag.getCompound("elements");
					if(elementTag.contains(elem.name()) && (search.isEmpty() || elem.name().startsWith(search))){
						drawModalRectWithCustomSizedTexture(xPos, yPos, xPos, yPos, spotLength, spotLength, 300, 300);
					}
				}
			}
		}



		if(!search.isEmpty()){
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
		if(!searchBar.charTyped(key, keyCode)){
			super.keyTyped(key, keyCode);
		}
	}
}
