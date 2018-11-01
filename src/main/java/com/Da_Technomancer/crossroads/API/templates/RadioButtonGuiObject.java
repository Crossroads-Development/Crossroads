package com.Da_Technomancer.crossroads.API.templates;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;

/**
 * Takes multiple ToggleButtonGuiObjects and causes them to act as radio buttons. When one is pressed, the others will release.
 * Do not directly interact with the ToggleButtonGuiObjects used to form radio buttons, instead do so through this. 
 * 
 * The buttons passed will be referred to by index.
 */
public class RadioButtonGuiObject implements IGuiObject{
	
	private final List<ToggleButtonGuiObject> buttons;
	private int pressed;
	private boolean locked;
	
	/**
	 * @param buttons The buttons which will be linked as radio buttons.
	 * @throws IllegalArgumentException If no buttons are passed. 
	 */
	public RadioButtonGuiObject(ToggleButtonGuiObject... buttons) throws IllegalArgumentException{
		this(Arrays.asList(buttons));
	}
	
	/**
	 * @param buttons The buttons which will be linked as radio buttons.
	 * @throws IllegalArgumentException If no buttons are passed. 
	 */
	public RadioButtonGuiObject(List<ToggleButtonGuiObject> buttons) throws IllegalArgumentException{
		this.buttons = buttons;
		if(buttons.isEmpty()){
			throw new IllegalArgumentException("Empty RadioButton created.");
		}
		buttons.get(0).setDepressed(true);
	}

	@Override
	public boolean buttonPress(char key, int keyCode){
		return false;
	}

	@Override
	public boolean mouseClicked(int x, int y, int button){
		if(!locked){
			for(int i = 0; i < buttons.size(); i++){
				if(i != pressed){
					if(buttons.get(i).mouseClicked(x, y, button)){
						buttons.get(pressed).setDepressed(false);
						pressed = i;
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		for(ToggleButtonGuiObject button : buttons){
			button.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		}
		return true;
	}

	@Override
	public boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer){
		for(ToggleButtonGuiObject button : buttons){
			button.drawFore(mouseX, mouseY, fontRenderer);
		}
		return true;
	}
	
	public int getPressed(){
		return pressed;
	}
	
	public void setPressed(int pressed){
		if(!locked){
			buttons.get(this.pressed).setDepressed(false);
			buttons.get(pressed).setDepressed(true);
		}
		this.pressed = pressed;
	}
	
	public boolean isLocked(){
		return locked;
	}
	
	public void setLocked(boolean locked){
		if(locked){
			for(ToggleButtonGuiObject button : buttons){
				button.setDepressed(true);
			}
		}else{
			for(ToggleButtonGuiObject button : buttons){
				button.setDepressed(false);
			}
			buttons.get(pressed).setDepressed(true);
		}
		this.locked = locked;
	}
}
