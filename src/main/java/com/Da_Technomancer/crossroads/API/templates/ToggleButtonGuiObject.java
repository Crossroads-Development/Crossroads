package com.Da_Technomancer.crossroads.API.templates;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.SoundEvents;

import java.awt.*;

public class ToggleButtonGuiObject implements IGuiObject{

	protected final int x;
	protected final int y;
	protected final int endX;
	protected final int endY;
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
	public boolean charTyped(char key, int keyCode){
		return false;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button){
		if(isMouseOver(x, y)){
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			depressed = !depressed;
			return true;
		}
		return false;
	}

	@Override
	public boolean isMouseOver(double x, double y){
		return x >= this.x && x <= endX && y >= this.y && y <= endY;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		AbstractGui.fill(x, y, endX, endY, depressed ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
		GlStateManager.color3f(1, 1, 1);
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
