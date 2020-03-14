package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;

/**
 * An IGuiEventListener that MachineGuis will automatically render if added as a child
 */
public interface IGuiObject extends IGuiEventListener{

	/**
	 * Draws the background layer.
	 * 
	 * @param partialTicks Number of partial ticks, from 0 to 1
	 * @param mouseX X position of the mouse.
	 * @param mouseY Y position of the mouse.
	 * @param fontRenderer a FontRenderer object.
	 * @return Whether anything was drawn.
	 */
	boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer);
	
	/**
	 * Draws the foreground layer.
	 * 
	 * @param mouseX X position of the mouse.
	 * @param mouseY Y position of the mouse.
	 * @param fontRenderer a FontRenderer object.
	 * @return Whether anything was drawn.
	 */
	boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer);

}
