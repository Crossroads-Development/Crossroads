package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.templates.TextBarGuiObject;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ColorChartScreen extends ContainerScreen<ColorChartContainer>{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_gui.png");
	private static final ResourceLocation BACKGROUND_MONO = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_mono_gui.png");
	private static final int xCENTER = 150;
	private static final int yCENTER = 150;
	private static final int RADIUS = 138;

	private TextBarGuiObject searchBar;

	public ColorChartScreen(ColorChartContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
		xSize = 300;
		ySize = 300;
	}

	@Override
	public void init(){
		super.init();

		AdvancementTracker.listen();//Used for beam alignments
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
			renderTooltip(ImmutableList.of(elem.isDiscovered(playerInventory.player) ? elem.getLocalName(false) : "???", "R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue()), mouseX, mouseY, font);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color3f(1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND_MONO);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize, 300, 300);

		searchBar.drawBack(partialTicks, mouseX, mouseY, font);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String search = searchBar.getText().toUpperCase();
		searchBar.drawFore(mouseX, mouseY, font);

		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
		int spotLength = 2;

		for(int i = 0; i < 2 * RADIUS / spotLength; i++){
			for(int j = 0; j < 2 * RADIUS / spotLength; j++){
				int xPos = spotLength * i + xCENTER - RADIUS;
				int yPos = spotLength * j + yCENTER - RADIUS;
				if(Math.pow(RADIUS - (spotLength * i), 2) + Math.pow(RADIUS - (spotLength * j), 2) <= RADIUS * RADIUS){
					EnumBeamAlignments elem = EnumBeamAlignments.getAlignment(getColor(xPos, yPos));
					if(elem.isDiscovered(playerInventory.player) && (search.isEmpty() || elem.name().startsWith(search))){
						blit(xPos, yPos, xPos, yPos, spotLength, spotLength, 300, 300);
					}
				}
			}
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button){
		return super.mouseClicked(x, y, button) | searchBar.mouseClicked(x, y, button);
	}

	@Override
	public boolean charTyped(char key, int keyCode){
		if(!searchBar.charTyped(key, keyCode)){
			return super.charTyped(key, keyCode);
		}else{
			return true;
		}
	}
}
