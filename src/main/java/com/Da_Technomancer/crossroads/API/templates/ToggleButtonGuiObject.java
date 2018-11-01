package com.Da_Technomancer.crossroads.API.templates;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;

public class ToggleButtonGuiObject implements IGuiObject{

	private final int x;
	private final int y;
	private final int endX;
	private final int endY;
	private final int baseX;
	private final int baseY;
	private final String text;
	
	private boolean depressed;
	
	/**
	 * 
	 * @param windowX X-coordinate where the GUI starts.
	 * @param windowY Y-coordinate where the GUI starts.
	 * @param x X-coordinate where the button starts, relative to the GUI.
	 * @param y Y-coordinate where the button starts, relative to the GUI.
	 * @param width Width of the button.
	 * @param text Text to display on the button.
	 */
	public ToggleButtonGuiObject(int windowX, int windowY, int x, int y, int width, String text){
		this.baseX = x;
		this.baseY = y;
		this.x = x + windowX;
		this.y = y + windowY;
		this.endX = width + this.x;
		this.endY = 20 + this.y;
		this.text = text;
	}
	
	@Override
	public boolean buttonPress(char key, int keyCode){
		return false;
	}

	@Override
	public boolean mouseClicked(int x, int y, int button){
		if(x >= this.x && x <= endX && y >= this.y && y <= endY){
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			depressed = !depressed;
			return true;
		}
		return false;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		Gui.drawRect(x, y, endX, endY, depressed ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
		GlStateManager.color(1, 1, 1);
		return true;
	}

	@Override
	public boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer){
		fontRenderer.drawStringWithShadow(text, 5 + baseX, 6 + baseY, Color.WHITE.getRGB());
		return true;
	}
	
	public boolean isDepressed(){
		return depressed;
	}
	
	public void setDepressed(boolean depressed){
		this.depressed = depressed;
	}
}
