package com.Da_Technomancer.crossroads.API.gui;

import javax.annotation.Nullable;

/**
 * Not everything using OutputLogGuiObject needs to implement this interface, this is only for allowing logs to be manipulated by packets. This should be applied to the GuiContainer.
 */
public interface ILogUser{
	
	@Nullable
	public OutputLogGuiObject getLog(String name);

}
