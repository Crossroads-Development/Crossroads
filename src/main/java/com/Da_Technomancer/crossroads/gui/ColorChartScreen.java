package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class ColorChartScreen extends ContainerScreen<ColorChartContainer>{

	//This texture is a 4x4 grid of 300x300 color charts, with different alignments colored in. The top second-left one is fully B&W
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_gui.png");
//	private static final ResourceLocation BACKGROUND_MONO = new ResourceLocation(Crossroads.MODID, "textures/gui/container/color_chart_mono_gui.png");
	private static final int xCENTER = 150;
	private static final int yCENTER = 150;
	private static final int RADIUS = 138;
//	private static final int[] RESOLUTIONS = {1, 2, 3, 6};//UI color vs B&W resolution options for config. Lower numbers are better. All values must be factors of RADIUS

	private TextFieldWidget searchBar;

	public ColorChartScreen(ColorChartContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
		xSize = 300;
		ySize = 300;
	}

	@Override
	public void init(){
		super.init();

		AdvancementTracker.listen();//Used for beam alignments

		searchBar = new TextFieldWidget(font, guiLeft, guiTop + ySize, xSize, 18, new TranslationTextComponent("container.search_bar"));
		searchBar.setCanLoseFocus(false);
		searchBar.setTextColor(-1);
		searchBar.setDisabledTextColour(-1);
		searchBar.setEnableBackgroundDrawing(false);
		searchBar.setMaxStringLength(20);
		searchBar.setValidator(s -> {
			for(char c : s.toCharArray()){
				if(!Character.isAlphabetic(c)){
					return false;
				}
			}
			return true;
		});
		children.add(searchBar);
		setFocusedDefault(searchBar);
	}

	private static Color getColor(int x, int y){
		return Color.getHSBColor((float) (Math.atan2(y - yCENTER, x - xCENTER) / (2D * Math.PI)), (float) Math.min(Math.sqrt(Math.pow(x - xCENTER, 2) + Math.pow(y - yCENTER, 2)) / RADIUS, 1), 1);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);

		//Tooltip
		if(Math.pow(xCENTER - mouseX + guiLeft, 2) + Math.pow(yCENTER - mouseY + guiTop, 2) <= RADIUS * RADIUS){
			Color col = getColor(mouseX - guiLeft, mouseY - guiTop);
			EnumBeamAlignments elem = EnumBeamAlignments.getAlignment(col);
			ArrayList<ITextComponent> tooltip = new ArrayList<>(2);
			if(elem.isDiscovered(playerInventory.player)){
				tooltip.add(new StringTextComponent(elem.getLocalName(false)));
			}else{
				tooltip.add(new StringTextComponent("???"));
			}
			tooltip.add(new StringTextComponent("R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue()));
			renderToolTip(matrix, tooltip, mouseX, mouseY, font);
		}

		searchBar.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
		int i = guiLeft;
		int j = guiTop;
		blit(matrix, i, j, 300, 0, xSize, ySize, 1200, 1200);

		String search = searchBar.getText().toUpperCase();

		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
//		final int spotLength = RESOLUTIONS[CRConfig.colorChartResolution.get() - 1];

		for(EnumBeamAlignments elem : EnumBeamAlignments.values()){
			if(elem.isDiscovered(playerInventory.player) && (search.isEmpty() || elem.getLocalName(false).toLowerCase(Locale.US).startsWith(search.toLowerCase(Locale.US)))){
				//Render the colored overlay that alignment over the B&W base
				int imageIndex = elem.ordinal() + 2;
				blit(matrix, guiLeft, guiTop, xSize * (imageIndex % 4), xSize * (int) (imageIndex / 4), xSize, ySize, 1200, 1200);
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

	@Override
	protected void func_230451_b_(MatrixStack matrix, int p_230451_2_, int p_230451_3_){
		font.func_238422_b_(matrix, title, field_238742_p_, field_238743_q_, 0x404040);
		//Render no inventory label
	}
}
