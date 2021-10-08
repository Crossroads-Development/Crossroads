package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class ColorChartScreen extends AbstractContainerScreen<ColorChartContainer>{

	//This texture is a 4x4 grid of 300x300 color charts, with different alignments colored in. The top second-left one is fully B&W
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_gui.png");
//	private static final ResourceLocation BACKGROUND_MONO = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_mono_gui.png");
	private static final int xCENTER = 150;
	private static final int yCENTER = 150;
	private static final int RADIUS = 138;
//	private static final int[] RESOLUTIONS = {1, 2, 3, 6};//UI color vs B&W resolution options for config. Lower numbers are better. All values must be factors of RADIUS

	private EditBox searchBar;

	public ColorChartScreen(ColorChartContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
		imageWidth = 300;
		imageHeight = 300;
	}

	@Override
	public void init(){
		super.init();

		AdvancementTracker.listen();//Used for beam alignments

		searchBar = new EditBox(font, leftPos, topPos + imageHeight, imageWidth, 18, new TranslatableComponent("container.search_bar"));
		searchBar.setCanLoseFocus(false);
		searchBar.setTextColor(-1);
		searchBar.setTextColorUneditable(-1);
		searchBar.setBordered(false);
		searchBar.setMaxLength(20);
		searchBar.setFilter(s -> {
			for(char c : s.toCharArray()){
				if(!Character.isAlphabetic(c)){
					return false;
				}
			}
			return true;
		});
		children.add(searchBar);
		setInitialFocus(searchBar);
	}

	private static Color getColor(int x, int y){
		return Color.getHSBColor((float) (Math.atan2(y - yCENTER, x - xCENTER) / (2D * Math.PI)), (float) Math.min(Math.sqrt(Math.pow(x - xCENTER, 2) + Math.pow(y - yCENTER, 2)) / RADIUS, 1), 1);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);

		//Tooltip
		if(Math.pow(xCENTER - mouseX + leftPos, 2) + Math.pow(yCENTER - mouseY + topPos, 2) <= RADIUS * RADIUS){
			Color col = getColor(mouseX - leftPos, mouseY - topPos);
			EnumBeamAlignments elem = EnumBeamAlignments.getAlignment(col);
			ArrayList<Component> tooltip = new ArrayList<>(2);
			if(elem.isDiscovered(inventory.player)){
				tooltip.add(new TextComponent(elem.getLocalName(false)));
			}else{
				tooltip.add(new TextComponent("???"));
			}
			tooltip.add(new TextComponent("R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue()));
			renderComponentTooltip(matrix, tooltip, mouseX, mouseY);//MCP note: renderTooltip
		}

		searchBar.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
		int i = leftPos;
		int j = topPos;
		blit(matrix, i, j, 300, 0, imageWidth, imageHeight, 1200, 1200);

		String search = searchBar.getValue().toUpperCase();

		Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
//		final int spotLength = RESOLUTIONS[CRConfig.colorChartResolution.get() - 1];

		for(EnumBeamAlignments elem : EnumBeamAlignments.values()){
			if(elem.isDiscovered(inventory.player) && (search.isEmpty() || elem.getLocalName(false).toLowerCase(Locale.US).startsWith(search.toLowerCase(Locale.US)))){
				//Render the colored overlay that alignment over the B&W base
				int imageIndex = elem.ordinal() + 2;
				blit(matrix, leftPos, topPos, imageWidth * (imageIndex % 4), imageWidth * (int) (imageIndex / 4), imageWidth, imageHeight, 1200, 1200);
			}
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button){
		return searchBar.mouseClicked(x, y, button) || super.mouseClicked(x, y, button);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
		if(p_keyPressed_1_ == 256){
			minecraft.player.closeContainer();
		}

		return searchBar.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || searchBar.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char key, int keyCode){
		for(GuiEventListener gui : children){
			if(gui.charTyped(key, keyCode)){
				return true;
			}
		}

		return super.charTyped(key, keyCode);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int p_230451_2_, int p_230451_3_){
		font.draw(matrix, title, titleLabelX, titleLabelY, 0x404040);
		//Render no inventory label
	}
}
