package com.Da_Technomancer.crossroads.API.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TexturedButtonGuiObject extends ButtonGuiObject{

	private final ResourceLocation texture;
	private final int u;
	private final int v;
	private final int uEnd;
	private final int vEnd;
	private final int textureWidth;
	private final int textureHeight;
	
	public TexturedButtonGuiObject(int windowX, int windowY, int x, int y, int width, int height, ResourceLocation texture, int u, int v, int uEnd, int vEnd, int textureWidth, int textureHeight){
		super(windowX, windowY, x, y, width, height, "");
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uEnd = uEnd;
		this.vEnd = vEnd;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		Gui.drawScaledCustomSizeModalRect(x, y, u, v, uEnd - u, vEnd - v, endX - x, endY - y, textureWidth, textureHeight);
		GlStateManager.color(1, 1, 1);
		return true;
	}
}
