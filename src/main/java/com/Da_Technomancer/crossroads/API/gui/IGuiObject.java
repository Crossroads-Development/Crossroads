package com.Da_Technomancer.crossroads.API.gui;

import net.minecraft.client.gui.FontRenderer;

public interface IGuiObject{
	
	/**
	 * @param key The key pressed.
	 * @param keyCode the Key Code.
	 * @return Whether it handled the key press.
	 */
	public boolean buttonPress(char key, int keyCode);
	
	/**
	 * @param x X coordinate of click.
	 * @param y Y coordinate of click.
	 * @param button The mouse button pressed. 0: left, 1: right.
	 * @return Whether it handled the mouse click.
	 */
	public boolean mouseClicked(int x, int y, int button);
	
	/**
	 * Draws the background layer.
	 * 
	 * @param partialTicks
	 * @param mouseX X position of the mouse.
	 * @param mouseY Y position of the mouse.
	 * @param fontRenderer a FontRenderer object.
	 * @return Whether anything was drawn.
	 */
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer);
	
	/**
	 * Draws the foreground layer.
	 * 
	 * @param mouseX X position of the mouse.
	 * @param mouseY Y position of the mouse.
	 * @param fontRenderer a FontRenderer object.
	 * @return Whether anything was drawn.
	 */
	public boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer);
}
