package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.templates.TextBarGuiObject;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.util.Locale;

public class ColorChartScreen extends ContainerScreen<ColorChartContainer>{

	//This texture is a 4x4 grid of 300x300 color charts, with different alignments colored in. The top second-left one is fully B&W
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_gui.png");
//	private static final ResourceLocation BACKGROUND_MONO = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_mono_gui.png");
	private static final int xCENTER = 150;
	private static final int yCENTER = 150;
	private static final int RADIUS = 138;
//	private static final int[] RESOLUTIONS = {1, 2, 3, 6};//UI color vs B&W resolution options for config. Lower numbers are better. All values must be factors of RADIUS

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
		children.add(searchBar);
	}

	private static Color getColor(int x, int y){
		return Color.getHSBColor((float) (Math.atan2(y - yCENTER, x - xCENTER) / (2D * Math.PI)), (float) Math.min(Math.sqrt(Math.pow(x - xCENTER, 2) + Math.pow(y - yCENTER, 2)) / RADIUS, 1), 1);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);

		//Tooltip
		if(Math.pow(xCENTER - mouseX + guiLeft, 2) + Math.pow(yCENTER - mouseY + guiTop, 2) <= RADIUS * RADIUS){
			Color col = getColor(mouseX - guiLeft, mouseY - guiTop);
			EnumBeamAlignments elem = EnumBeamAlignments.getAlignment(col);
			renderTooltip(ImmutableList.of(elem.isDiscovered(playerInventory.player) ? elem.getLocalName(false) : "???", "R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue()), mouseX, mouseY, font);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color3f(1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 300, 0, xSize, ySize, 1200, 1200);

		searchBar.drawBack(partialTicks, mouseX, mouseY, font);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String search = searchBar.getText().toUpperCase();
		searchBar.drawFore(mouseX, mouseY, font);

		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
//		final int spotLength = RESOLUTIONS[CRConfig.colorChartResolution.get() - 1];

		for(EnumBeamAlignments elem : EnumBeamAlignments.values()){
			if(elem.isDiscovered(playerInventory.player) && (search.isEmpty() || elem.getLocalName(false).toLowerCase(Locale.US).startsWith(search.toLowerCase(Locale.US)))){
				//Render the colored overlay that alignment over the B&W base
				int imageIndex = elem.ordinal() + 2;
				blit(0, 0, xSize * (imageIndex % 4), xSize * (int) (imageIndex / 4), xSize, ySize, 1200, 1200);
			}
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button){
		return searchBar.mouseClicked(x, y, button) || super.mouseClicked(x, y, button);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
		for(IGuiEventListener gui : children){
			if(gui.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)){
				return true;
			}
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char key, int keyCode){
		for(IGuiEventListener gui : children){
			if(gui.charTyped(key, keyCode)){
				return true;
			}
		}

		return super.charTyped(key, keyCode);
	}
}
