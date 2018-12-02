package com.Da_Technomancer.crossroads.API.templates;

import java.awt.Color;
import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class OutputLogGuiObject implements IGuiObject{
	
	private static final ResourceLocation BAR = new ResourceLocation(Main.MODID, "textures/gui/container/log_back.png");
	private final int x;
	private final int y;
	private final int endX;
	private final int endY;
	private final int lines;
	private final int baseX;
	private final int baseY;
	private final int maxChar;
	
	private final ArrayList<Pair<String, Color>> text = new ArrayList<Pair<String, Color>>();
	
	/**
	 * @param windowX X-coordinate where the GUI starts.
	 * @param windowY Y-coordinate where the GUI starts.
	 * @param x X-coordinate where the text bar starts, relative to the GUI.
	 * @param y Y-coordinate where the text bar starts, relative to the GUI.
	 * @param width Width of the text bar.
	 * @param lines The number of lines to display. Height is 20 * lines.
	 * @param maxChar Maximum number of characters.
	 */
	public OutputLogGuiObject(int windowX, int windowY, int x, int y, int width, int lines, int maxChar){
		this.baseX = x;
		this.baseY = y;
		this.x = x + windowX;
		this.y = y + windowY;
		this.endX = width + this.x;
		this.endY = (20 * lines) + this.y;
		this.lines = lines;
		this.maxChar = maxChar;
	}
	
	@Override
	public boolean buttonPress(char key, int keyCode){
		return false;
	}

	@Override
	public boolean mouseClicked(int x, int y, int button){
		return false;
	}

	@Override
	public boolean mouseOver(int x, int y){
		return x >= this.x && x <= endX && y >= this.y && y <= endY;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		Minecraft.getMinecraft().getTextureManager().bindTexture(BAR);
		Gui.drawScaledCustomSizeModalRect(x, y, 0, 0, 300, 2, 2, endY - y, 300, 20);//left side (top to bottom)
		Gui.drawScaledCustomSizeModalRect(x + 2, y + 2, 2, 2, 296, 16, endX - x - 4, endY - y - 4, 300, 20);//middle
		Gui.drawScaledCustomSizeModalRect(endX - 2, y, 298, 0, 2, 20, 2, endY - y, 300, 20);//right side (top to bottom)
		Gui.drawScaledCustomSizeModalRect(x + 2, y, 2, 0, 296, 2, endX - x - 4, 2, 300, 20);//top (offset left to offset right)
		Gui.drawScaledCustomSizeModalRect(x + 2, endY - 2, 2, 18, 296, 2, endX - x - 4, 2, 300, 20);//bottom (offset left to offset right)
		return true;
	}

	@Override
	public boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer){
		if(text.isEmpty()){
			return false;
		}
		for(int i = 0; i < text.size(); i++){
			fontRenderer.drawStringWithShadow(text.get(i).getLeft(), 5 + baseX, 6 + baseY + (20 * i), text.get(i).getRight().getRGB());
		}
		GlStateManager.color(1, 1, 1);
		return true;
	}
	
	/**
	 * @param text The line of text to be displayed.
	 * @param col The color of the text (null for white).
	 */
	public void addText(@Nonnull String text, @Nullable Color col){
		this.text.add(Pair.of(text.substring(0, Math.min(text.length(), maxChar)), col == null ? Color.WHITE : col));
		if(this.text.size() > lines){
			this.text.remove(0);
		}
	}
	
	/**
	 * Clears all displayed text. 
	 */
	public void clearLog(){
		text.clear();
	}

	/**
	 * Not everything using OutputLogGuiObject needs to implement this interface, this is only for allowing logs to be manipulated by packets. This should be applied to the GuiContainer.
	 */
	public static interface ILogUser{

		@Nullable
		public OutputLogGuiObject getLog(String name);

	}
}
