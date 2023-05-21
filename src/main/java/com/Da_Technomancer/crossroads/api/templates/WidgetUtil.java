package com.Da_Technomancer.crossroads.api.templates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WidgetUtil{

	public static boolean keyPressed(int keyCode, int scanCode, int modifiers, Screen screen){
		for(GuiEventListener widget : screen.children()){
			if(widget.keyPressed(keyCode, scanCode, modifiers)){
				return true;
			}
			if(widget instanceof EditBox box && box.canConsumeInput()){
				return true;
			}
		}
		return false;
	}

	public static boolean keyPressed(int keyCode, int scanCode, int modifiers, GuiEventListener... widgets){
		for(GuiEventListener widget : widgets){
			if(widget.keyPressed(keyCode, scanCode, modifiers)){
				return true;
			}
			if(widget instanceof EditBox box && box.canConsumeInput()){
				return true;
			}
		}
		return false;
	}

	public static void resize(Screen screen, Minecraft minecraft, int width, int height){
		List<? extends GuiEventListener> widgets = screen.children();
		String[] text = new String[widgets.size()];
		for(int i = 0; i < widgets.size(); i++){
			GuiEventListener widget = widgets.get(i);
			if(widget instanceof EditBox box){
				text[i] = box.getValue();
			}
		}
		screen.init(minecraft, width, height);
		for(int i = 0; i < widgets.size(); i++){
			GuiEventListener widget = widgets.get(i);
			if(widget instanceof EditBox box){
				box.setValue(text[i]);
			}
		}
	}

	/**
	 * When there are two buttons for decreasing and increasing a setting respectively, this is added as a listener to the container to grey-out the buttons as applicable whenever the setting value changes
	 */
	public static class SettingAdjustListener implements ContainerListener{

		private final int minValue;
		private final int maxValue;
		private final Button decreaseButton;
		private final Button increaseButton;
		private final int dataslotIndex;

		public SettingAdjustListener(int dataslotIndex, int minValue, int maxValue, Button decreaseButton, Button increaseButton){
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.decreaseButton = decreaseButton;
			this.increaseButton = increaseButton;
			this.dataslotIndex = dataslotIndex;
		}

		@Override
		public void slotChanged(AbstractContainerMenu menu, int p_39316_, ItemStack p_39317_){
			//No-op
		}

		@Override
		public void dataChanged(AbstractContainerMenu menu, int changedDataslotIndex, int newDataValue){
			if(changedDataslotIndex == dataslotIndex){
				updateButtons(newDataValue);
			}
		}

		public void updateButtons(int newDataValue){
			decreaseButton.active = newDataValue > minValue;
			increaseButton.active = newDataValue < maxValue;
		}
	}
}
