package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Predicate;

public class TextBarGuiObject implements IGuiObject{

	private static final ResourceLocation BAR = new ResourceLocation(Crossroads.MODID, "textures/gui/container/search_bar.png");
	private final int x;
	private final int y;
	private final int endX;
	private final int endY;
	private final int maxChar;
	private final String emptyText;
	private final Predicate<Character> acceptedChar;
	private final int baseX;
	private final int baseY;

	private boolean selected;
	private String text = "";
	private int index;

	/**
	 *
	 * @param windowX X-coordinate where the GUI starts.
	 * @param windowY Y-coordinate where the GUI starts.
	 * @param x X-coordinate where the text bar starts, relative to the GUI.
	 * @param y Y-coordinate where the text bar starts, relative to the GUI.
	 * @param width Width of the text bar.
	 * @param maxChar Maximum number of characters.
	 * @param emptyText Text to display when the box is empty. May be null for no text. 
	 * @param acceptedChar A predicate to determine if the text box can hold a character.
	 */
	public TextBarGuiObject(int windowX, int windowY, int x, int y, int width, int maxChar, @Nullable String emptyText, Predicate<Character> acceptedChar){
		this.baseX = x;
		this.baseY = y;
		this.x = x + windowX;
		this.y = y + windowY;
		this.endX = width + this.x;
		this.endY = 20 + this.y;
		this.maxChar = maxChar;
		this.emptyText = emptyText;
		this.acceptedChar = acceptedChar;
	}

	@Override
	public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_){
		switch(key){
			case 257:
			case 335:
				//Enter?
				selected = false;
				index = text.length();
				return true;
			case 259:
				//Backspace
				if(!text.isEmpty() && index != 0){
					text = text.substring(0, index - 1) + (index == text.length() ? "" : text.substring(index));
					index--;
					return true;
				}
				break;
			case 261:
				//Delete
				if(index < text.length()){
					text = text.substring(0, index) + text.substring(index + 1);
					return true;
				}
				break;
			case 263:
				//Left arrow
				if(index > 0){
					index--;
					return true;
				}
				break;
			case 262:
				//Right arrow
				if(index < text.length()){
					index++;
					return true;
				}
				break;

		}
		return false;
	}

	@Override
	public boolean charTyped(char key, int keyCode){
		if(selected){
			if(acceptedChar.test(key)){//Normal typing
				if(text.length() < maxChar){
					text = text.substring(0, index) + key + text.substring(index);
					index++;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button){
		if(!selected && isMouseOver(x, y)){
			selected = !selected;
			return true;
		}
		selected = false;
		index = text.length();
		return false;
	}

	@Override
	public boolean isMouseOver(double x, double y){
		return x >= this.x && x <= endX && y >= this.y && y <= endY;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		Minecraft.getInstance().getTextureManager().bindTexture(BAR);
		if(selected){
			GlStateManager.color3f(1, 1, 0);
		}
		AbstractGui.blit(x, y, 0, 0, 2, 20, 300, 20);
		AbstractGui.blit(x + 2, y, 2, 0, endX - x - 4, 20, 300, 20);
		AbstractGui.blit(endX - 2, y, 298, 0, 2, 20, 300, 20);
		GlStateManager.color3f(1, 1, 1);

		return true;
	}

	@Override
	public boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer){
		if(text.isEmpty() && emptyText == null){
			return false;
		}
		char indexChar = index == text.length() ? '_' : '|';
		fontRenderer.drawStringWithShadow(text.isEmpty() ? emptyText : text.substring(0, index) + indexChar + text.substring(index), 5 + baseX, 7 + baseY, text.isEmpty() ? Color.GRAY.getRGB() : Color.WHITE.getRGB());
		GlStateManager.color3f(1, 1, 1);
		return true;
	}

	public String getText(){
		return text;
	}

	public void setText(@Nonnull String text){
		this.text = text;
		index = text.length();
	}

	public boolean isSelected(){
		return selected;
	}
}
